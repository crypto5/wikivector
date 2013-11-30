package org.wikivector.batch.page

import org.wikivector.dao.WikiPageDao
import scala.collection.JavaConversions._
import org.wikivector.dao.LangLinkDao
import org.wikivector.common.OtherExecutors
import org.wikivector.model.WikiPage

object TranslateWikiPages extends App {
  val executor = OtherExecutors.createFixedThreadsBlockingExecutor(4, 1000)
  run()
  executor.shutdown()
  
  def run() {
    var processed = 0L
    WikiPageDao.getAllWikiPages.foreach { enPage =>
      if (enPage.lang == "en") {
        executor.submit(new PageProcessor(enPage))
      }
      processed += 1
      if (processed % 1000 == 0) println(processed)
    }
    println("Done")
  }

  class PageProcessor(val enPage: WikiPage) extends Runnable {
    override def run() {
      val translations = LangLinkDao.getTranslations(enPage.id.drop(2).toInt)
      val translationPages = translations.map(WikiPageDao.getByTitle(_).toSeq).flatten
      translationPages.foreach { translatedPage =>
        WikiPageDao.setEnPageId(translatedPage.id, enPage.id)
      }
    }
  }
}