package org.wikivector.batch.category

import org.wikivector.dao.WikiPageDao
import scala.collection.JavaConversions._
import org.wikivector.dao.WikiPageCategoryDao
import org.wikivector.dao.CategoryAllParentsDao
import org.wikivector.model.WikiPage
import org.wikivector.common.OtherExecutors
import org.wikivector.dao.CategoryCountersDao
import org.wikivector.common.Lexer

object CalculateCategoryCounters extends App {
  val executor = OtherExecutors.createFixedThreadsBlockingExecutor(4, 10)
  run()
  
  def run() {
    var processed = 0L
    val wikiPages = WikiPageDao.getAllWikiPages()
    wikiPages.foreach { wikiPage =>
      executor.submit(new WikiPageProcessor(wikiPage))
      processed += 1
      if(processed % 1000 == 0) println("Processed " + (processed / 1000) + "k articles.")
    }
    executor.shutdown()
  }
  
  class WikiPageProcessor(wikiPage: WikiPage) extends Runnable {
    override def run() {
      val tokens = Lexer.process(wikiPage.title, wikiPage.lang) ++ Lexer.process(wikiPage.text, wikiPage.lang)
      val pageCategories = WikiPageCategoryDao.getParents(wikiPage.id)
      val allCategories = pageCategories.map(CategoryAllParentsDao.getParents(_)).flatten.toSet
      allCategories.foreach(CategoryCountersDao.incrementDocCounter(_))
      allCategories.foreach(CategoryCountersDao.incrementTotalCounter(_, tokens.size))
    }
  }
}