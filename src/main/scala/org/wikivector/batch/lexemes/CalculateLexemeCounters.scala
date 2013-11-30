package org.wikivector.batch.lexemes

import org.wikivector.dao.WikiPageDao
import scala.collection.JavaConversions._
import org.wikivector.model.WikiPage
import org.wikivector.common.Lexer
import scala.collection.mutable.ArrayBuffer
import org.wikivector.model.LexemeCounters
import org.wikivector.model.LexemeCounters
import org.wikivector.dao.LexemeCountersDao
import org.wikivector.common.OtherExecutors
import org.wikivector.dao.ProcessedDao

object CalculateLexemeCounters extends App {
  val executor = OtherExecutors.createFixedThreadsBlockingExecutor(4, 1000)
  
  run()
  
  def run() {
    var processed = 0L
    val wikiPages = WikiPageDao.getAllWikiPages()
    wikiPages.foreach { wikiPage =>
      executor.submit(new CalculateLexemProcessor(wikiPage))
      processed += 1
      if (processed % 100 == 0) println("Processed %d wikipages.".format(processed))
    }
  }
}

class CalculateLexemProcessor(val wikiPage: WikiPage) extends Runnable {
  override def run() {
    try {
      val processedKey = "CalculateLexemProcessor" + wikiPage.id
      if (ProcessedDao.isProcessed(processedKey)) {
        return
      }
      val titleTokens = Lexer.process(wikiPage.title, wikiPage.lang)
      val docTokens = Lexer.process(wikiPage.text, wikiPage.lang)
      val totalTokens = titleTokens ++ docTokens
      val groupedTokens = totalTokens.groupBy(token => token)
      val lexemes = new ArrayBuffer[LexemeCounters]
      groupedTokens.foreach { token =>
        val titleCount = if (titleTokens.contains(token._1)) 1 else 0
        val docCount = 1
        val totalCount = token._2.size
        lexemes += new LexemeCounters(token._1, titleCount, docCount, totalCount)
      }
      LexemeCountersDao.incrementCounters(lexemes)
      ProcessedDao.markProcessed(processedKey)
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}