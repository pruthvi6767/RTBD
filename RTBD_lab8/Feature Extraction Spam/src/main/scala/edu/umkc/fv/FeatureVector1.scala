package edu.umkc.fv


import edu.umkc.fv.NLPUtils._
import edu.umkc.fv.Utils._
import org.apache.spark.SparkConf
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by Mayanka on 14-Jul-15.
  */
object FeatureVector1 {

  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "F:\\winutils")
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("FeatureVector1").set("spark.driver.memory", "3g").set("spark.executor.memory", "3g")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    val sc = ssc.sparkContext
    val stopWords = sc.broadcast(loadStopWords("/stopwords.txt")).value
    val labelToNumeric = createLabelMap("data/training/")
    var model: NaiveBayesModel = null
    // Training the data
    /*
      for(line <- Source.fromFile("data/SMSSpamCollection.txt").getLines()) {
        var firstword:String = line.split("\\s+", 2)(0)
        if(firstword == "ham") {
          val smsmessage = line.split("\\s+", 2)(1)
          val fw = new FileWriter("data/training/NotSpam/notspam.txt", true)
          fw.write(smsmessage+"\n")
          fw.close()
          println(smsmessage)
        }
        else if (firstword == "spam"){
          val smsmessage = line.split("\\s+", 2)(1)
          val fw = new FileWriter("data/training/spam/spam.txt", true)
          fw.write(smsmessage+"\n")
          fw.close()
        }

      }
*/
    val training = sc.wholeTextFiles("data/training/*")
      .map(rawText => createLabeledDocument(rawText, labelToNumeric, stopWords))
    val X_train = tfidfTransformer(training)
    X_train.foreach(vv => println(vv))

    model = NaiveBayes.train(X_train, lambda = 1.0)

    val lines = sc.wholeTextFiles("data/testing/*")
    val data = lines.map(line => {
      val test = createLabeledDocumentTest(line._2, labelToNumeric, stopWords)

      println("The test body is " + test)
      test
    })


    val X_test = tfidfTransformerTest(sc, data)

    val predictionAndLabel = model.predict(X_test)
    println("PREDICTION")
    SocketClient.sendCommandToRobot("PREDICTION")
    predictionAndLabel.foreach(x => {

      labelToNumeric.foreach { y => if (y._2 == x) {
        SocketClient.sendCommandToRobot(y._1)
        println(y._1)
      }
      }
    })

  }
}



