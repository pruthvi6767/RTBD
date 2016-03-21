object Storage {
  var numberofDocs = 0
  var tf : Double=0
  var idf : Double=0
  var tfidf : Double=0

}
object TfIdf {

  def tfidf(word: String, document: String, documentList: Seq[String]) : Double =
    tf(word, document) * idf(word, documentList)

  private[this] def tf(word: String, document: String) = {
    val documentTokens = document.toLowerCase.split(" +")
    val wordfreq = (word: String, document: String) => {
      val documentWordFrequency = documentTokens.groupBy(e => e).map(e => e._1 -> e._2.length)
      documentWordFrequency.getOrElse(word.toLowerCase(), 0)

    }
    Storage.tf= wordfreq(word, document).toDouble / documentTokens.size
    wordfreq(word, document).toDouble / documentTokens.size
    //println("The Document word count is"+documentTokens.size)
  }

  private[this] def idf(word: String, documentList: Seq[String]) = {
    val DocsContaining = (word: String, documentList: Seq[String]) => documentList.foldLeft(0) {
      (acc, e) => if (e.toLowerCase.contains(word)) acc + 1 else acc
    }

    Storage.numberofDocs = DocsContaining(word,documentList)
    //println("The number of docs"+numDocsContaining(word,documentList))
    Storage.idf=Math.log(documentList.length) / DocsContaining(word, documentList)
    Math.log(documentList.length) / DocsContaining(word, documentList)
  }

}

object Runner extends App {

  import TfIdf._

  val documentList = "new york post" :: "new york times " :: "los angeles times" :: Nil
  val wordcount=documentList.flatMap(line=>{line.split(" |,|:|_")}).distinct
  println(wordcount)
  val result=Array.ofDim[String](4,wordcount.length)
  //foreach

  for(i <- 0 to wordcount.length-1)
  {
    var j=1
    //result {0}{i+1}= wordcount {i}
    for (document <- documentList) {
      var s=tfidf(wordcount{i}, document, documentList)
      print("\n The Word is :"+wordcount{i}+"\t and the tfidf value in document "+j+  ": " +tfidf(wordcount{i}, document, documentList) )
      j=j+1
      iOSConnector.sendCommandToRobot(s.toString())
    }
    println("\nThe Total number of Docs with "+wordcount{i}+ " is :" + Storage.numberofDocs)
    //println(result(0)(1))
  }


}