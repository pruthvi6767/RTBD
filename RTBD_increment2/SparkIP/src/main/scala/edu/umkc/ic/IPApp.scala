package edu.umkc.ic


import java.nio.file.{Files, Paths}

import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel, LDA}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.bytedeco.javacpp.opencv_highgui._

import scala.collection.mutable
import java.io._
import java.net.{Socket, InetAddress}
import java.nio.file.{Paths, Files}
import javax.imageio.ImageIO

import com.sun.jersey.core.util.Base64
import sun.misc.BASE64Decoder

object IPApp {
  val featureVectorsCluster = new mutable.MutableList[String]

  val IMAGE_CATEGORIES = List("broccoli", "carrot", "eggplant","pumpkin", "tomato")

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

    // Split data into training (60%) and test (40%).
    val splits = parsedData.sample(true,0.6, seed = 11L)
    val training = parsedData
    val test = splits

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
  def testImageClassification(sc: SparkContext) = {

    val model = KMeansModel.load(sc, IPSettings.KMEANS_PATH)
    val vocabulary = ImageUtils.vectorsToMat(model.clusterCenters)

    val path = "newLabel.jpg"
    val desc = ImageUtils.bowDescriptors(path, vocabulary)
    println("Descriptors  :")
    println(desc.asCvMat())

    val testImageMat = imread(path)
    imshow("Test Image", testImageMat)

    val histogram = ImageUtils.matToVector(desc)

    println("-- Histogram size : " + histogram.size)
    println(histogram.toArray.mkString(" "))

    val nbModel = NaiveBayesModel.load(sc, IPSettings.NAIVE_BAYES_PATH)
    println(nbModel.labels.mkString(" "))

    val p = nbModel.predict(histogram)
    println(s"Predicting test image : " + IMAGE_CATEGORIES(p.toInt))

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
      .set("spark.executor.memory", "2g")
    System.setProperty("hadoop.home.dir", "F:\\winutils")
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

    testImageClassification(sc)

    //val ip = InetAddress.getByName("10.182.0.192").getHostName

    //    val lines = ssc.receiverStream(new CustomReceiver(ip,5555))

    val lines = ssc.socketTextStream("192.168.0.15", 3333)
    //println("Connected!!!!")

    val data = lines.map(line => {
      line
    })

    data.print()

        //Filtering out the non base64 strings
        val base64Strings = lines.filter(line => {
          Base64.isBase64(line)
        })


        base64Strings.foreachRDD(rdd => {
          val base64s = rdd.collect()
          for (base64 <- base64s) {
            val bufferedImage = ImageIO.read(new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(base64)))
            val imgOutFile = new File("newLabel.jpg")
            val saved = ImageIO.write(bufferedImage, "jpg", imgOutFile)
            println("Saved : " + saved)

            if (saved) {
              val category = classifyImage(rdd.context, "newLabel.jpg")
              //SocketClient.sendCommandToRobot(category)
            }
          }
        })

        ssc.start()

        ssc.awaitTermination()

    //    ssc.stop()
  }
}