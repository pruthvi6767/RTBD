import java.util

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by Mayanka on 24-Sep-15.
 */
object TwitterSentimentMain {

  def main(args: Array[String]) {

    val filters = args

    // Set the system properties so that Twitter4j library used by twitter stream
    // can use them to generate OAuth credentials

    System.setProperty("twitter4j.oauth.consumerKey", "XmuCJg6wqok0kM4atoBWyzX70")
    System.setProperty("twitter4j.oauth.consumerSecret", "M791X1Py0jy52DG2f18EsxS0CYaMJhOfEZykO8H3mOLmfMXOBD")
    System.setProperty("twitter4j.oauth.accessToken", "66398818-wqoEXxQRTtb5GS24eqvn4DS5yQHIfay0NkgN3YDed")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "xP3IHuIaGJAuDES88Mt6TuxVEz3oSDz5AlYOgtZ7MEZD1")

    //Create a spark configuration with a custom name and master
    // For more master configuration see  https://spark.apache.org/docs/1.2.0/submitting-applications.html#master-urls
    val sparkConf = new SparkConf().setAppName("STweetsApp").setMaster("local[*]")
    //Create a Streaming COntext with 2 second window
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    //Using the streaming context, open a twitter stream (By the way you can also use filters)
    //Stream generates a series of random tweets
    val stream = TwitterUtils.createStream(ssc, None,Array("recipe") )
  //  stream.print()

    val sentiment:DStream[TweetWithSentiment]=stream.map{Status=>{
      val st=Status.getText()

      val sa=new SentimentAnalyzer()
      val tw=sa.findSentiment(st)
      tw
    }}

    sentiment.foreachRDD {
      rdd => rdd.foreach {
        tw => {
          if (tw != null) {

            println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
            println(tw.getLine + "      "+"Sentiment Value:" + tw.getCssClass)
            println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
           // val Tweet = tw.getLine
            val sa1 = new SentimentAnalyzer()
            // val Tweetwithouturl = sa1.removeUrl(Tweet)
            // println("Tweet without url:" + Tweetwithouturl)
            // val Noun = sa1.pop(Tweet)
            //println("Nouns in Tweet:" + Noun)
            //val Url=sa1.extractUrls(Tweet)
            //println("Url is:  "+Url)
            var result = new util.ArrayList[String](3)
            result =sa1.pop(tw.getLine,tw.getCssClass)
            println("Tweet is :"+result.get(0)+ "\n" + "Url is :" +result.get(1)+"\n"+result.get(2))
            println(result.get(0)+" xyztagxyz "+result.get(1)+ " xyztagxyz "+result.get(2))
            SocketClient.sendCommandToRobot(result.get(0)+" xyztagxyz "+result.get(1)+ " xyztagxyz "+result.get(2))

          }
        }
      }
    }
    ssc.start()

    ssc.awaitTermination()
  }

}
