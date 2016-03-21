import java.io.{BufferedReader, IOException, InputStreamReader}
import java.net.{InetAddress, Socket}



  object iOSConnector {

  def main(args: Array[String]) {

      receiveFromRobot()

  }
  def findIpAdd():String =
  {
    val localhost = InetAddress.getLocalHost
    val localIpAddress = localhost.getHostAddress

    return localIpAddress
  }
  def receiveFromRobot()
  {
    try {
      lazy val address: Array[Byte] = Array(134.toByte, 193.toByte, 19.toByte, 19.toByte)
      val ia = "10.205.1.137" //InetAddress.getByAddress(address)
      val socket = new Socket(ia, 1234)
      // val out = new PrintStream(socket.getOutputStream)
      val in = new BufferedReader( new InputStreamReader(socket.getInputStream))

      while (socket.isConnected) {
        val userInput=in.readLine
        if(userInput!=null) {
          println(userInput)
         // val km=new KeyedMessage[String,String](topic,userInput)
         // producer.send(km)
        }
      }

      in.close()
      socket.close()
    }
    catch {
      case e: IOException =>

    }

  }


}
