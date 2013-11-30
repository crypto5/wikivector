package org.wikivector.batch.category

import org.wikivector.dao.WikiPageDao
import scala.collection.JavaConversions._
import scala.collection.mutable
import java.util.regex.Pattern
import org.wikivector.dao.WikiPageCategoryDao
import org.wikivector.common.OtherExecutors
import org.wikivector.model.WikiPage
import org.wikivector.dao.ProcessedDao
import org.wikivector.dao.CategoryDao

object CreateWikiPageCategoryGraph extends App {
  val executor = OtherExecutors.createFixedThreadsBlockingExecutor(4, 100)
  val categoryPattern = Pattern.compile("\\[\\[.*\\]\\]")
  run()

  def run() {
    var processed = 0L
    WikiPageDao.getAllWikiPages.foreach { page =>
      val pageHandler = new WikiPageHandler(page)
      executor.submit(pageHandler)
      processed += 1
      if(processed % 100 == 0) println(processed)
    } 
  }
  
  class WikiPageHandler(val page: WikiPage) extends Runnable {
    override def run() {
      val processedKey = "CreateWikiPageCategoryGraph-" + page.id
      if(ProcessedDao.isProcessed(processedKey)) return
      val matcher = categoryPattern.matcher(page.text)
      val groups = new mutable.ArrayBuffer[String]
      while(matcher.find()) groups += matcher.group()
      groups.filter(_.size < 300).foreach{ group =>
        val categoryTitle = getCategoryTitle(group)
        val categoryWikiPageId = CategoryDao.getCategoryIdByTitle(categoryTitle, page.lang)
        if(categoryWikiPageId != null) {
          WikiPageCategoryDao.save(page.id, categoryWikiPageId)
        }
      }
      ProcessedDao.markProcessed(processedKey)
    }
    
    def getCategoryTitle(group: String): String = {
      // trim [[ and ]] and get right side from |
      return group.drop(2).dropRight(2).takeWhile(_ != '|')
    }
  }
}