package SparkTwitter

import scala.collection.immutable.ListMap
import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by pradyumnad on 07/07/15.
 */
object TwitterStreaming {

  def main(args: Array[String]) {


    val filters = args

    // Set the system properties so that Twitter4j library used by twitter stream
    // can use them to generate OAuth credentials

    System.setProperty("twitter4j.oauth.consumerKey", "iBQ80mhEysZL1eJe0TQkmq8XQ")
    System.setProperty("twitter4j.oauth.consumerSecret", "2lCHDeO0txhx8uAHXggGsh1gSh1FSOVZiiS9JTORPnRwSFejLT")
    System.setProperty("twitter4j.oauth.accessToken", "131923775-zvFxzZmD6PIxQFTNxFQ1X8XsLH6wWHoAkJ2FozYn")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "V2VVc4yRUPAQB3cgDVMF9r7Y0yHAKR6oUuyz905yOAnss")

    //Create a spark configuration with a custom name and master
    // For more master configuration see  https://spark.apache.org/docs/1.2.0/submitting-applications.html#master-urls
    val sparkConf = new SparkConf().setAppName("rtdb").setMaster("local[*]")
    //Create a Streaming COntext with 2 second window
    val ssc = new StreamingContext(sparkConf, Seconds(10))
    //Using the streaming context, open a twitter stream (By the way you can also use filters)
    //Stream generates a series of random tweets
    val stream = TwitterUtils.createStream(ssc, None, filters)
stream.print()
    //Map : Retrieving Hash Tags
    val hashTags = stream.flatMap(status => status.getText.split(" ").filter(_.startsWith("#")))

    //Finding the top hash Tags on 30 second window
    val topCounts30 = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(30))
      .map{case (topic, count) => (count, topic)}
      .transform(_.sortByKey(false))
    //Finding the top hash Tgas on 10 second window
    val topCounts10 = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(10))
      .map{case (topic, count) => (count, topic)}
      .transform(_.sortByKey(false))

    // Print popular hashtags
    topCounts30.foreachRDD(rdd => {
      val topList = rdd.take(1)
      println("\nPopular topics in last 30 seconds (%s total):".format(rdd.count()))
      SocketClient.sendCommandToRobot("Count1 "+rdd.count())
      topList.foreach{case (count,tag) => SocketClient.sendCommandToRobot("Tweet2 %s (%s tweets)".format(tag, count));}
      //topList.foreach{case (count, tag) => println("%s (%s tweets)".format(tag, count))}
     // println("key is "+topList.maxBy(_._2)._2+" the value is "+topList.maxBy(_._2)._1)
      //val max30key = topList.minBy(_._2)._2
      //val max30value = topList.minBy(_._2)._1
      //println("top 30 tag ", max30key)
      //println("top 30 value",max30value)


    })

    topCounts10.foreachRDD(rdd => {
      val topList = rdd.take(1)
     SocketClient.sendCommandToRobot("Count2 "+rdd.count())
      topList.foreach{case (count,tag) => SocketClient.sendCommandToRobot("Tweet1 %s (%s tweets)".format(tag, count));}
      println("\nPopular topics in last 10 seconds (%s total):".format(rdd.count()))
      topList.foreach{case (count, tag) => println("Tweet %s (%s tweets)".format(tag, count))}


     //println("key is "+topList.maxBy(_._2)._2+" the value is "+topList.maxBy(_._2)._1)
      //val maxkey = topList.minBy(_._2)._2
      //val maxvalue=topList.minBy(_._2)._1
      //println("top tag",maxkey)
      //println("top value",maxvalue)
      //SocketClient.sendCommandToRobot("%s (%s tweets)",maxkey.);

    })
     ssc.start()

    ssc.awaitTermination()
  }
}
