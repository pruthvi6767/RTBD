package edu.umkc.ic

import java.io.{IOException, PrintStream}
import java.net.{InetAddress, Socket}


object SocketClient {

  def findIpAdd():String =
  {
    val localhost = InetAddress.getLocalHost
    val localIpAddress = localhost.getHostAddress

    return localIpAddress
  }
  def sendCommandToRobot(string: String)
  {
    // Simple server

    try {


      lazy val address: Array[Byte] = Array(10.toByte, 205.toByte, 0.toByte, 88.toByte)
     // val ia = InetAddress.getByAddress(address)
      val ia="192.168.0.15"
      val socket = new Socket(ia,3333)
      val out = new PrintStream(socket.getOutputStream)
      //val in = new DataInputStream(socket.getInputStream())

      out.print(string)
      out.flush()

      out.close()
      //in.close()
      socket.close()
    }
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }


}
