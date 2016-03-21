package edu.umkc.ic


import java.nio.file.{Files, Paths}

import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS, NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel, LDA}
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.bytedeco.javacpp.opencv_highgui._

import scala.collection.mutable

object IPApp {
  val featureVectorsCluster = new mutable.MutableList[String]

  val IMAGE_CATEGORIES = List("avocado","banana","carrot","corn","eggplant", "greenpepper","strawberry","tomato")

  /**
   *
   * @param sc : SparkContext
   * @param images : Images list from the training set
   */
  def extractDescriptors(sc: SparkContext, images: RDD[(String, String)]): Unit = {

    if (Files.exists(Paths.get(IPSettings.FEATURES_PATH))) {
      println(s"${IPSettings.FEATURES_PATH} exists, skipping feature extraction..")
      return
    }

    val data = images.map {
      case (name, contents) => {
        val n = name.split("file:/")(1)
        val desc = ImageUtils.descriptors(n)
        val list = ImageUtils.matToString(desc)
        println("-- " + list.size)
        list
      }
    }.reduce((x, y) => x ::: y)

    val featuresSeq = sc.parallelize(data)

    featuresSeq.saveAsTextFile(IPSettings.FEATURES_PATH)
    println("Total size : " + data.size)
  }

  def LDAGrouping(sc: SparkContext): Unit = {
    // Load and parse the data
    val data = sc.textFile(IPSettings.FEATURES_PATH)
    val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()
    val corpus = parsedData.zipWithIndex.map(_.swap).cache()

    // Cluster the documents into three topics using LDA
    val ldaModel = new LDA().setK(3).run(corpus)

    // Output topics. Each is a distribution over words (matching word count vectors)
    println("Learned topics (as distributions over vocab of " + ldaModel.vocabSize + " words):")
    val topics = ldaModel.topicsMatrix
    for (topic <- Range(0, 3)) {
      print("Topic " + topic + ":")
      for (word <- Range(0, ldaModel.vocabSize)) {
        print(" " + topics(word, topic));
      }
      println()
    }

  }

  def kMeansCluster(sc: SparkContext): Unit = {
    if (Files.exists(Paths.get(IPSettings.KMEANS_PATH))) {
      println(s"${IPSettings.KMEANS_PATH} exists, skipping clusters formation..")
      return
    }

    // Load and parse the data
    val data = sc.textFile(IPSettings.FEATURES_PATH)
    val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()

    // Cluster the data into two classes using KMeans
    val numClusters = 400
    val numIterations = 20
    val clusters = KMeans.train(parsedData, numClusters, numIterations)

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    val WSSSE = clusters.computeCost(parsedData)
    println("Within Set Sum of Squared Errors = " + WSSSE)

    clusters.save(sc, IPSettings.KMEANS_PATH)
    println(s"Saves Clusters to ${IPSettings.KMEANS_PATH}")
  }

  def createHistogram(sc: SparkContext, images: RDD[(String, String)]): Unit = {
    if (Files.exists(Paths.get(IPSettings.HISTOGRAM_PATH))) {
      println(s"${IPSettings.HISTOGRAM_PATH} exists, skipping histograms creation..")
      return
    }

    val sameModel = KMeansModel.load(sc, IPSettings.KMEANS_PATH)

    val kMeansCenters = sc.broadcast(sameModel.clusterCenters)

    val categories = sc.broadcast(IMAGE_CATEGORIES)


    val data = images.map {
      case (name, contents) => {

        val vocabulary = ImageUtils.vectorsToMat(kMeansCenters.value)

        val desc = ImageUtils.bowDescriptors(name.split("file:/")(1), vocabulary)
        val list = ImageUtils.matToString(desc)
        // println("-- " + list.size)

        val segments = name.split("file:/")
        val segment = segments(1).split("/")
        val cat = segment(segment.length - 2)
        List(categories.value.indexOf(cat) + "," + list(0))
      }
    }.reduce((x, y) => x ::: y)

    val featuresSeq = sc.parallelize(data)

    featuresSeq.saveAsTextFile(IPSettings.HISTOGRAM_PATH)
    println("Total size : " + data.size)
  }

  def generateNaiveBayesModel(sc: SparkContext): Unit = {
    if (Files.exists(Paths.get(IPSettings.NAIVE_BAYES_PATH))) {
      println(s"${IPSettings.NAIVE_BAYES_PATH} exists, skipping Naive Bayes model formation..")
      return
    }

    val data = sc.textFile(IPSettings.HISTOGRAM_PATH)
    val parsedData = data.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }
    val splits = parsedData.sample(true,0.6, seed = 11L)
    val training = parsedData
    val test = splits

    // Logistic Regression Starts here
    // Split data into training (60%) and test (40%).
    val lparsedData = data.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }
    val lsplits = lparsedData.randomSplit(Array(0.6, 0.4), seed = 11L)
    val ltraining = lsplits(0).cache()
    val ltest = lsplits(1)
    // Run training algorithm to build the model
    val lmodel = new LogisticRegressionWithLBFGS()
      .setNumClasses(10).run(ltraining)

    // Compute raw scores on the test set.
    val predictionAndLabels = ltest.map { case LabeledPoint(label, features) =>
      val prediction = lmodel.predict(features)
      (prediction, label)
    }

    // Get evaluation metrics.
    val metrics = new MulticlassMetrics(predictionAndLabels)
    val precision = metrics.precision
    println("Precision = " + precision)

    // Save and load model
    lmodel.save(sc, IPSettings.LOGISTIC_PATH)
    println("Logistic Model has been generated")
    val sameModel = LogisticRegressionModel.load(sc,IPSettings.LOGISTIC_PATH)
    val lpredictionAndLabel = ltest.map(p => (lmodel.predict(p.features), p.label))
    lpredictionAndLabel.collect().foreach(f=>println(f))
    val laccuracy = 1.0 * lpredictionAndLabel.filter(x => x._1 == x._2).count() / ltest.count()
    println("Confusion Matrix for Logistic")
    println("Logistic Accuracy " +laccuracy)
    ModelEvaluation.evaluateModel(lpredictionAndLabel)

    //Ends here
    // Decision Tree
    val dsplits = parsedData.randomSplit(Array(0.8, 0.2))
    //val (trainingData, testData) = (dsplits(0), dsplits(1))
    val trainingData = dsplits(0)
    val testData = dsplits(1)
    // Train a DecisionTree model.
    //  Empty categoricalFeaturesInfo indicates all features are continuous.
    val numClasses = 9
    val categoricalFeaturesInfo = Map[Int, Int]()
    val impurity = "gini"
    val maxDepth = 8
    val maxBins = 32

    val dmodel = DecisionTree.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
      impurity, maxDepth, maxBins)

    // Evaluate model on test instances and compute test error
    val labelAndPreds = testData.map { point =>
      val prediction = dmodel.predict(point.features)
      (point.label, prediction)
    }
    val testErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / testData.count()
    println("Test Error = " + testErr)
    println("Learned classification tree model:\n" + dmodel.toDebugString)

    // Save and load model
    dmodel.save(sc,IPSettings.DECISION_PATH)
    val dpredictionAndLabel = testData.map(p => (dmodel.predict(p.features), p.label))
    dpredictionAndLabel.collect().foreach(f=>println(f))
    val daccuracy = 1.0 * dpredictionAndLabel.filter(x => x._1 == x._2).count() / testData.count()

    println("Confusion Matrix for Decision")
    println("Decision Accuracy " +daccuracy)

    ModelEvaluation.evaluateModel(dpredictionAndLabel)


    val dsameModel = DecisionTreeModel.load(sc, IPSettings.DECISION_PATH)
    //Ends here

    // Split data into training (60%) and test (40%).

    val model = NaiveBayes.train(training, lambda = 1.0)

    val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
    predictionAndLabel.collect().foreach(f=>println(f))
    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()

    ModelEvaluation.evaluateModel(predictionAndLabel)

    // Save and load model
    model.save(sc, IPSettings.NAIVE_BAYES_PATH)
    println("Naive Bayes Model generated")
  }

  /**
   * @note Test method for classification on Spark
   * @param sc : Spark Context
   * @return
   */
  def testImageClassification(sc: SparkContext, path: String) = {

    val model = KMeansModel.load(sc, IPSettings.KMEANS_PATH)
    val vocabulary = ImageUtils.vectorsToMat(model.clusterCenters)


    val desc = ImageUtils.bowDescriptors(path, vocabulary)
    println("Descriptors  :")
    println(desc.asCvMat())

    val testImageMat = imread(path)
    ///imshow("Test Image", testImageMat)

    val histogram = ImageUtils.matToVector(desc)

    println("-- Histogram size : " + histogram.size)
    println(histogram.toArray.mkString(" "))
    val lmodel = LogisticRegressionModel.load(sc,IPSettings.LOGISTIC_PATH)
    val nbModel = NaiveBayesModel.load(sc, IPSettings.NAIVE_BAYES_PATH)
    val dmodel = DecisionTreeModel.load(sc,IPSettings.DECISION_PATH)

    println(nbModel.labels.mkString(" "))
    val l = lmodel.predict(histogram)
    val p = nbModel.predict(histogram)
    val d = dmodel.predict(histogram)



    println(path)
    println(s"Predicting test image (Naive) : " + IMAGE_CATEGORIES(p.toInt))
    println(s"Predicting test image (Logisitic) : " + IMAGE_CATEGORIES(l.toInt))
    println(s"Predicting test image (Decision) : " + IMAGE_CATEGORIES(d.toInt))



  }

  /**
   * @note Test method for classification from Client
   * @param sc : Spark Context
   * @param path : Path of the image to be classified
   */
  def classifyImage(sc: SparkContext, path: String): String = {

    val model = KMeansModel.load(sc, IPSettings.KMEANS_PATH)
    val vocabulary = ImageUtils.vectorsToMat(model.clusterCenters)

    val desc = ImageUtils.bowDescriptors(path, vocabulary)

    val histogram = ImageUtils.matToVector(desc)

    println("--Histogram size : " + histogram.size)

    val nbModel = NaiveBayesModel.load(sc, IPSettings.NAIVE_BAYES_PATH)
    println(nbModel.labels.mkString(" "))

    val p = nbModel.predict(histogram)
    println(s"Predicting test image : " + IMAGE_CATEGORIES(p.toInt))

    //val predict = IMAGE_CATEGORIES(p.toInt)
    //SocketClient.sendCommandToRobot(IMAGE_CATEGORIES(p.toInt))
    IMAGE_CATEGORIES(p.toInt)
  }

  def main(args: Array[String]) {
    val conf = new SparkConf()
      .setAppName(s"IPApp")
      .setMaster("local[*]")
      .set("spark.executor.memory", "3g")
    System.setProperty("hadoop.home.dir", "C:\\winutils")
    val ssc = new StreamingContext(conf, Seconds(2))
    val sc = ssc.sparkContext

    val images = sc.wholeTextFiles(s"${IPSettings.INPUT_DIR}/*/*.jpg").cache()

    /**
     * Extracts Key Descriptors from the Training set
     * Saves it to a text file
     */
    extractDescriptors(sc, images)

    /**
     * Reads the Key descriptors and forms a 'K' topics
     * Saves the centers as a text file
     */
    // LDAGrouping(sc);
    /**
     * Reads the Key descriptors and forms a 'K' cluster
     * Saves the centers as a text file
     */

    kMeansCluster(sc)

    /**
     * Forms a labeled Histogram using the Training set
     * Saves it in the form of label, [Histogram]
     *
     * This shall be used as a input to Naive Bayes to create a model
     */
    createHistogram(sc, images)

    /**
     * From the labeled Histograms a Naive Bayes Model is created
     */
    generateNaiveBayesModel(sc)
    var path = Array("files/Train/avocado/1005837.jpg","files/Train/avocado/2214959-single-avocado-isolated-on-white.jpg","files/Train/avocado/download (2).jpg",
                "files/Train/avocado/stock-photo-single-whole-avocado-pear-isolated-on-white-34393636.jpg",
    "files/Train/banana/7694680-Ripe-banana-isolated-on-white-background-Stock-Photo.jpg","files/Train/banana/CVLfYOrXIAM80qF.jpg",
    "files/Train/banana/single-banana.jpg","files/Train/carrot/9835164-Single-carrot-isolated-over-white-background--Stock-Photo.jpg",
      "files/Train/carrot/carrot_single01.jpg","files/Train/carrot/images (47).jpg","files/Train/carrot/ugly-carrot-white-background-single-isolated-43245838.jpg",
    "files/Train/corn/Corn (15).jpg","files/Train/corn/Corn (25).jpg","files/Train/corn/images (20).jpg","files/Train/eggplant/eggplant (24).jpg",
    "files/Train/eggplant/eggplant (15).jpg","files/Train/eggplant/images (20).jpg","files/Train/eggplant/images (47).jpg",
    "files/Train/greenpepper/green-bell-pepper-model - Copy.jpg", "files/Train/greenpepper/GreenBellPepper01 - Copy.png", "files/Train/greenpepper/pepper-green - Copy.jpg",
    "files/Train/greenpepper/RealFoodToronto-Organic-Green-Bell-Pepper.jpg","files/Train/strawberry/strawberry_430x340px.jpg",
    "files/Train/strawberry/images (15).jpg","files/Train/strawberry/images (26).jpg","files/Train/strawberry/images (52).jpg")
    path.foreach(f=>testImageClassification(sc,f))

    //val ip = InetAddress.getByName("10.182.0.192").getHostName

    //    val lines = ssc.receiverStream(new CustomReceiver(ip,5555))
//
//    val lines = ssc.socketTextStream("192.168.0.15", 3333)
//    //println("Connected!!!!")
//
//    val data = lines.map(line => {
//      line
//    })
//
//    data.print()
//
//        //Filtering out the non base64 strings
//        val base64Strings = lines.filter(line => {
//          Base64.isBase64(line)
//        })
//
//
//        base64Strings.foreachRDD(rdd => {
//          val base64s = rdd.collect()
//          for (base64 <- base64s) {
//            val bufferedImage = ImageIO.read(new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(base64)))
//            val imgOutFile = new File("newLabel.jpg")
//            val saved = ImageIO.write(bufferedImage, "jpg", imgOutFile)
//            println("Saved : " + saved)
//
//            if (saved) {
//              val category = classifyImage(rdd.context, "newLabel.jpg")
//              //SocketClient.sendCommandToRobot(category)
//            }
//          }
//        })

//        ssc.start()

  //      ssc.awaitTermination()

    //    ssc.stop()
  }
}