package org.wikivector.batch.category

import org.wikivector.dao.LexemeCountersDao
import scala.collection.JavaConversions._
import org.wikivector.dao.LexemeCategoryCountersDao
import org.wikivector.model.LexemeCategoryInfo
import org.wikivector.dao.CategoryCountersDao
import org.wikivector.dao.LexemeCategoryInfoDao
import org.wikivector.model.LexemeCounters
import org.wikivector.common.OtherExecutors

object CalculateLexemeCategoryInfo extends App {
  var processedLexemes = 0L
  var processedLexemeCategoryCounters = 0L
  var saved = 0L
  val executor = OtherExecutors.createFixedThreadsBlockingExecutor(4, 100)
  run()

  def run() {
    LexemeCountersDao.getAllLexemeCounters.foreach { lexemeCounter =>
      if (lexemeCounter.totalCount > 5) {
        executor.submit(new LexemeProcessor(lexemeCounter))
      }
      processedLexemes += 1
      if (processedLexemes % 1000 == 0) println("saved = %d, processedLexemes = %d, processedLexemeCategoryCounters = %d"
        .format(saved, processedLexemes, processedLexemeCategoryCounters))
    }
    executor.shutdown()
  }

  class LexemeProcessor(lexemeCounter: LexemeCounters) extends Runnable {
    override def run() {
      val lexemeCategoryTitleCounters = LexemeCategoryCountersDao.getTitleCounters(lexemeCounter.lexeme)
      val lexemeCategoryDocCounters = LexemeCategoryCountersDao.getDocCounters(lexemeCounter.lexeme)
      val lexemeCategoryTotalCounters = LexemeCategoryCountersDao.getTotalCounters(lexemeCounter.lexeme)
      lexemeCategoryTotalCounters.foreach { categoryTotalCounter =>
        if (categoryTotalCounter._2.toDouble / lexemeCounter.totalCount > 0.01) {
          val categoryId = categoryTotalCounter._1
          val info = new LexemeCategoryInfo
          info.lexeme = lexemeCounter.lexeme
          info.categoryId = categoryId
          info.categoryDocCount = CategoryCountersDao.getDocCounter(categoryId)
          info.categoryTotalCount = CategoryCountersDao.getTotalCounter(categoryId)
          info.lexemeCategoryTitleCount = lexemeCategoryTitleCounters.getOrElse(categoryId, 0L)
          info.lexemeCategoryDocCount = lexemeCategoryDocCounters.getOrElse(categoryId, 0L)
          info.lexemeCategoryTotalCount = lexemeCategoryTotalCounters.getOrElse(categoryId, 0L)
          info.lexemeTitleCount = lexemeCounter.titleCount
          info.lexemeDocCount = lexemeCounter.docCount
          info.lexemeTotalCount = lexemeCounter.totalCount
          LexemeCategoryInfoDao.save(info)
          saved += 1
        }
        processedLexemeCategoryCounters += 1
      }
    }
  }
}