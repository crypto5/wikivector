package org.wikivector.dao

import org.wikivector.model.WikiPage
import org.wikivector.config.CassandraConfig._
import com.netflix.astyanax.model.Row
import scala.collection.JavaConversions._
import org.wikivector.model.WikiPage

object WikiPageDao {
  val COLUMNS = Seq("title", "text", "isCategory", "lang", "en_page_id")
  
  def save(page: WikiPage) {
    val m = Keyspace.prepareMutationBatch()
    m.withRow(WikiPageCF, page.id)
      .putColumn("title", page.title)
      .putColumn("text", page.text)
      .putColumn("isCategory", page.isCategory)
      .putColumn("lang", page.lang)
      .putColumn("en_page_id", page.enPageId)
    m.execute()
  }
  
  def getById(id: String): WikiPage = {
    val columns = Keyspace.prepareQuery(WikiPageCF)
      .getKey(id)
      .withColumnSlice(COLUMNS)
      .execute()
      .getResult()
    if(columns.isEmpty()) {
      return null
    } else {
      return new WikiPage(id, columns)
    }
  }
  
  def setEnPageId(id: String, enPageId: String) {
    val m = Keyspace.prepareMutationBatch()
    m.withRow(WikiPageCF, id)
      .putColumn("en_page_id", enPageId)
    m.execute()
  }
  
  def getByTitle(title: String): WikiPageIterator = {
    val rows = Keyspace.prepareQuery(WikiPageCF)
      .searchWithIndex()
      .addExpression()
        .whereColumn("title").equals().value(title)
      .execute()
      .getResult()
      .iterator()
    return new WikiPageIterator(rows)
  }
  
  def getCategoryIdByTitle(title: String, lang: String): String = {
    val rows = Keyspace.prepareQuery(WikiPageCF)
      .searchWithIndex()
      .addExpression()
        .whereColumn("title").equals().value(title)
      .withColumnSlice("lang", "isCategory")
      .execute()
      .getResult()
    rows.foreach{ row =>
      if(row.getColumns().getStringValue("lang", null) == lang 
          && row.getColumns().getBooleanValue("isCategory", false) == true) 
        return row.getKey()
    }
    return null
  }
  
  class WikiPageIterator(val rows: Iterator[Row[String, String]]) extends java.util.Iterator[WikiPage] {
	  def hasNext = rows.hasNext
	  def next() = new WikiPage(rows.next)
	  def remove() = rows.remove()
  }
  
  def getAllWikiPages(): WikiPageIterator = {
    val rows = Keyspace.prepareQuery(WikiPageCF)
      .getAllRows()
      .setBlockSize(100)
      .withColumnSlice(COLUMNS)
      .execute()
      .getResult()
      .iterator()
    return new WikiPageIterator(rows)
  } 
}