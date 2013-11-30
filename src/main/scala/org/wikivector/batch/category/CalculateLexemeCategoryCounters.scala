package org.wikivector.batch.category

import org.wikivector.common.OtherExecutors
import org.wikivector.dao.WikiPageDao
import scala.collection.JavaConversions._
import org.wikivector.model.WikiPage
import org.wikivector.common.Lexer
import org.wikivector.dao.LexemeCountersDao
import org.wikivector.dao.WikiPageCategoryDao
import org.wikivector.dao.CategoryAllParentsDao
import scala.collection.mutable
import org.wikivector.model.LexemeCategoryCounter
import org.wikivector.dao.CategoryDao
import org.wikivector.model.LexemeCategoryCounter
import org.wikivector.dao.LexemeCategoryCountersDao
import org.wikivector.dao.CategoryCountersDao
import org.wikivector.dao.ProcessedDao

object CalculateLexemeCategoryCounters extends App {
  val executor = OtherExecutors.createFixedThreadsBlockingExecutor(4, 10)
  var writesCount = 0L
  var t1 = System.currentTimeMillis()
  var t2 = 0L
  
  run()
  
  def run() {
    var processed = 0L
    val wikiPages = WikiPageDao.getAllWikiPages()
    wikiPages.foreach { wikiPage =>
      executor.submit(new WikiPageProcessor(wikiPage))
//      new WikiPageProcessor(wikiPage).run
      processed += 1
      if (processed % 100 == 0) {
        println("Processed %d wikipages.".format(processed))
        t2 = System.currentTimeMillis()
        println("Write rate: " + (writesCount * 1000 / (t2 - t1)))
        t1 = t2
        writesCount = 0L
      }
    }
  }

  class WikiPageProcessor(wikiPage: WikiPage) extends Runnable {
    override def run() {
      try {
        val processedKey = "CalculateLexemeCategoryCounters:" + wikiPage.lang + ":" + wikiPage.id
        if(ProcessedDao.isProcessed(processedKey)) return
        val titleTokens = Lexer.process(wikiPage.title, wikiPage.lang)
        val docTokens = Lexer.process(wikiPage.text, wikiPage.lang)
        val totalTokens = titleTokens ++ docTokens
        val totalGroupedTokens = totalTokens.groupBy(token => token)
        val titleLexemeCounters = LexemeCountersDao.getCounters(titleTokens.toSet).filter(counter => counter.docCount > 3 && counter.docCount < 1000000)
        val docLexemeCounters = LexemeCountersDao.getCounters(totalTokens.toSet).filter(counter => counter.docCount > 3 && counter.docCount < 1000000)
        val pageCategories = WikiPageCategoryDao.getParents(wikiPage.id)
        val allCategories = pageCategories.map(CategoryAllParentsDao.getParents(_)).flatten.toSet
        val categoryCounters = CategoryCountersDao.getDocCounters(allCategories)
        val filteredCategories = allCategories.filter(categoryId => categoryCounters(categoryId) < 100000)
        val titleCounters = new mutable.ArrayBuffer[LexemeCategoryCounter]
        val docCounters = new mutable.ArrayBuffer[LexemeCategoryCounter]
        val totalCounters = new mutable.ArrayBuffer[LexemeCategoryCounter]
        titleLexemeCounters.foreach { titleLexemeCounter =>
          filteredCategories.foreach { category =>
            titleCounters += new LexemeCategoryCounter(titleLexemeCounter.lexeme, category, 1)
          }
        }
        docLexemeCounters.foreach { docLexemeCounter =>
          filteredCategories.foreach { category =>
            docCounters += new LexemeCategoryCounter(docLexemeCounter.lexeme, category, 1)
            val tokenCount = totalGroupedTokens.get(docLexemeCounter.lexeme).size
            totalCounters += new LexemeCategoryCounter(docLexemeCounter.lexeme, category, tokenCount)
          }
        }
        LexemeCategoryCountersDao.saveTitleCounters(titleCounters)
        LexemeCategoryCountersDao.saveDocCounters(docCounters)
        LexemeCategoryCountersDao.saveTotalCounters(totalCounters)
        writesCount += titleCounters.size + docCounters.size + totalCounters.size
        ProcessedDao.markProcessed(processedKey)
      } catch {
        case e: Throwable => e.printStackTrace()
      }
    }
  }
}