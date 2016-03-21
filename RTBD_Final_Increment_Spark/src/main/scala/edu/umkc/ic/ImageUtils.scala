package edu.umkc.ic

import org.apache.spark.mllib.linalg._
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_features2d._
import org.bytedeco.javacpp.opencv_highgui._
import org.bytedeco.javacpp.opencv_nonfree._
import org.bytedeco.javacpp.opencv_imgproc._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object ImageUtils {

  def descriptors(file: String): Mat = {
    //  val category = file.split("/")

    val img_1 = imread(file, CV_LOAD_IMAGE_ANYCOLOR)

    if (img_1.empty()) {
      println("Image is empty")
      -1
    }

    //var img_2 = img_1
    //-- Step 1: Detect the keypoints using ORB
    val detector = new SIFT()

    val keypoints_1 = new KeyPoint
    val outputimage_1 = new Mat
    val mask = new Mat
    val descriptors = new Mat
    val Fast1 = new FastFeatureDetector()
    Fast1.detect(img_1,keypoints_1)
    detector.compute(img_1,keypoints_1,descriptors)

    //    val colorDetector = new ColorDetector
    //    colorDetector.colorDistanceThreshold = 100

    //    if(category{9} == "Carrot")
    //      {
    //        colorDetector.targetColor = new ColorRGB(255,128,0)
    //        img_2 = colorDetector.process(img_1)
    //
    //      }
    //    else if  (category{9} == "Eggplant")
    //      {
    //        colorDetector.targetColor = new ColorRGB(55,255,55)
    //        img_2 = colorDetector.process(img_1)
    //      }
    //    else if (category{9} == "Tomato")
    //      {
    //        colorDetector.targetColor = new ColorRGB(255,0,0)
    //        img_2 = colorDetector.process(img_1)
    //
    //      }
    //
    val hsv = new Mat()
    cvtColor(img_1, hsv, CV_BGR2HSV)
    //detector.detectAndCompute(hsv, mask, keypoints_1, descriptors)

    //    drawKeypoints(img_2,keypoints_1,outputimage_1, new Scalar (0,255,0,0),DrawMatchesFlags.DRAW_RICH_KEYPOINTS)
    //    imshow(category{10},outputimage_1)
    //    waitKey()
    //    //    println(s"No of Keypoints ${keypoints_1.size()}")
    println(s"Key Descriptors ${descriptors.rows()} x ${descriptors.cols()}","file:",file)
    descriptors
  }

  def bowDescriptors(file: String, dictionary: Mat): Mat = {
    val matcher = new FlannBasedMatcher()
    val detector = new SIFT()
    val extractor = DescriptorExtractor.create("SIFT")
    val bowDE = new BOWImgDescriptorExtractor(extractor, matcher)
    bowDE.setVocabulary(dictionary)
    println(bowDE.descriptorSize() + " " + bowDE.descriptorType())

    val img = imread(file, CV_LOAD_IMAGE_ANYCOLOR)
    if (img.empty()) {
      println("Image is empty")
      -1
    }
    //    var img_2 = img
    //    val colorDetector = new ColorDetector
    //    colorDetector.colorDistanceThreshold = 100
    //    colorDetector.targetColor = new ColorRGB(255,128,0)
    //    img_2 = colorDetector.process(img)

    val keypoints = new KeyPoint
    val hsv1 = new Mat()
    cvtColor(img, hsv1, CV_BGR2HSV)

    detector.detect(hsv1, keypoints)
    val outputimage_1 = new Mat
    val response_histogram = new Mat

    // drawKeypoints(hsv1,keypoints,outputimage_1, new Scalar (0,255,0,0),DrawMatchesFlags.DRAW_RICH_KEYPOINTS)
    //imshow("Keypoints",outputimage_1)
    //waitKey()

    val orb = new ORB()
    val fast = new FastAdjuster()
    val fast1 = new FastFeatureDetector()
    val mask1 = new Mat()
    val mask2 = new Mat()
    val descriptors1 = new Mat()
    val descriptors2 = new Mat()
    val keypoints_1 = new KeyPoint()
    val keypoints_2 = new KeyPoint()
    val keypoints_3 = new KeyPoint()
    val keypoints_4 = new KeyPoint()
    detector.detectAndCompute(img, mask1, keypoints_1, descriptors1)
    orb.detectAndCompute(img,mask2,keypoints_2,descriptors2)
    fast.detect(img,keypoints_3)
    fast1.detect(img,keypoints_4)
    val outputimage_2 = new Mat
    val outputimage_3 = new Mat
    val outputimage_4 = new Mat
    val outputimage_5 = new Mat
    drawKeypoints(img,keypoints_1,outputimage_5, new Scalar (0,255,0,0),DrawMatchesFlags.DRAW_RICH_KEYPOINTS)
    drawKeypoints(img,keypoints_2,outputimage_2, new Scalar (0,255,0,0),DrawMatchesFlags.DRAW_RICH_KEYPOINTS)
    drawKeypoints(img,keypoints_3,outputimage_3, new Scalar (0,255,0,0),DrawMatchesFlags.DRAW_RICH_KEYPOINTS)
    drawKeypoints(img,keypoints_4,outputimage_4, new Scalar (0,255,0,0),DrawMatchesFlags.DRAW_RICH_KEYPOINTS)
    //    imshow("Sift",outputimage_5)
    //    imshow("ORB",outputimage_2)
    //    imshow("FAST",outputimage_3)
     //   imshow("FAST1",outputimage_4)
      //  waitKey()
    bowDE.compute(hsv1, keypoints_4, response_histogram)
    println("Histogram size : " + response_histogram.size().asCvSize().toString)
    println("Histogram : " + response_histogram.asCvMat().toString)
    response_histogram

  }

  def matToVector(mat: Mat): Vector = {
    val imageCvmat = mat.asCvMat()

    val noOfCols = imageCvmat.cols()

    //Channels discarded, take care of this when you are using multiple channels

    val imageInDouble = new Array[Double](noOfCols)
    for (col <- 0 to noOfCols - 1) {
      val pixel = imageCvmat.get(0, col)
      imageInDouble(col) = pixel
    }
    val featureVector = new DenseVector(imageInDouble)
    featureVector
  }

  def matToVectors(mat: Mat): Array[Vector] = {
    val imageCvmat = mat.asCvMat()

    val noOfCols = imageCvmat.cols()
    val noOfRows = imageCvmat.rows()

    val fVectors = new ArrayBuffer[DenseVector]()
    //Channels discarded, take care of this when you are using multiple channels

    for (row <- 0 to noOfRows - 1) {
      val imageInDouble = new Array[Double](noOfCols)
      for (col <- 0 to noOfCols - 1) {
        val pixel = imageCvmat.get(row, col)
        imageInDouble :+ pixel
      }
      val featureVector = new DenseVector(imageInDouble)
      fVectors :+ featureVector
    }

    fVectors.toArray
  }

  def matToDoubles(mat: Mat): Array[Array[Double]] = {
    val imageCvmat = mat.asCvMat()

    val noOfCols = imageCvmat.cols()
    val noOfRows = imageCvmat.rows()

    val fVectors = new ArrayBuffer[Array[Double]]()
    //Channels discarded, take care of this when you are using multiple channels

    for (row <- 0 to noOfRows - 1) {
      val imageInDouble = new Array[Double](noOfCols)
      for (col <- 0 to noOfCols - 1) {
        val pixel = imageCvmat.get(row, col)
        imageInDouble :+ pixel
      }
      fVectors :+ imageInDouble
    }
    fVectors.toArray
  }

  def matToString(mat: Mat): List[String] = {
    val imageCvmat = mat.asCvMat()

    val noOfCols = imageCvmat.cols()
    val noOfRows = imageCvmat.rows()

    val fVectors = new mutable.MutableList[String]
    //Channels discarded, take care of this when you are using multiple channels

    for (row <- 0 to noOfRows - 1) {
      val vecLine = new StringBuffer("")
      for (col <- 0 to noOfCols - 1) {
        val pixel = imageCvmat.get(row, col)
        vecLine.append(pixel + " ")
      }

      fVectors += vecLine.toString
    }
    fVectors.toList
  }

  def vectorsToMat(centers: Array[Vector]): Mat = {

    val vocab = new Mat(centers.size, centers(0).size, CV_32FC1)

    var i = 0
    for (c <- centers) {

      var j = 0
      for (v <- c.toArray) {
        vocab.asCvMat().put(i, j, v)
        j += 1
      }
      i += 1
    }

    //    println("The Mat is")
    //    println(vocab.asCvMat().toString)

    vocab
  }

}
