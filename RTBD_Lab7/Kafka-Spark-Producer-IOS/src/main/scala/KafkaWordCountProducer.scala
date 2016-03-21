import java.io.{BufferedReader, IOException, InputStreamReader}
import java.net.{InetAddress, Socket}
import java.util.Properties

import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import org.apache.log4j.Logger


object KafkaWordCountProducer {
  def main(args: Array[String]) {
    if (args.length < 4) {
      System.err.println("Usage: KafkaWordCountProducer <metadataBrokerList> <topic> " +
        "<messagesPerSec> <wordsPerMessage>")
      System.exit(1)
    }

    val Array(brokers, topic, messagesPerSec, wordsPerMessage) = args

    // Zookeeper connection properties
    val props = new Properties()
    props.put("metadata.broker.list", brokers)
    props.put("serializer.class", "kafka.serializer.StringEncoder")
    props.put("producer.type","async")

    val config = new ProducerConfig(props)
    val producer = new Producer[String, String](config)

    // Send some messages
    while(true) {
      val messages = (1 to messagesPerSec.toInt).map { messageNum =>
        val str = (1 to wordsPerMessage.toInt).map(x => scala.util.Random.nextInt(10).toString)
          .mkString(" ")

        new KeyedMessage[String, String](topic, str)
      }.toArray
      // Simple client
      val  log=Logger.getLogger(getClass.getName)
      try {
        //lazy val address: Array[Byte] = Array(134.toByte, 193.toByte, 19.toByte, 19.toByte)
        lazy val address: Array[Byte] = Array(10.toByte, 205.toByte, 3.toByte, 42.toByte)
        val ia = InetAddress.getByAddress(address)
        val socket = new Socket(ia, 1234)
        // val out = new PrintStream(socket.getOutputStream)
        val in = new BufferedReader( new InputStreamReader(socket.getInputStream))

        while (socket.isConnected) {
          val userInput=in.readLine
          if(userInput!=null) {
            log.info(userInput)
            val km=new KeyedMessage[String,String](topic,userInput)
            producer.send(km)
          }
        }

        in.close()
        socket.close()
      }
      catch {
        case e: IOException =>

      }
     // producer.send(messages: _*)
      Thread.sleep(100)
    }
  }
}
