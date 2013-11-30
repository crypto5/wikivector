package org.wikivector.dao

import org.wikivector.model.Category
import org.wikivector.config.CassandraConfig._
import scala.collection.JavaConversions._
import com.netflix.astyanax.model.Row
import org.wikivector.model.Category
import org.wikivector.model.Category

object CategoryDao {
  val COLUMNS = Seq("title", "lang", "page_id", "en_page_id")
  
  def save(page: Category) {
    val m = Keyspace.prepareMutationBatch()
    val row = m.withRow(CategoryCF, page.id)
      .putColumn("title", page.title)
      .putColumn("lang", page.lang)
      .putColumn("page_id", page.pageId)
    if(page.enPageId != null) row.putColumn("en_page_id", page.enPageId)
    m.execute()
  }
  
  def getCategoryIdByTitle(title: String, lang: String): Integer = {
    val rows = Keyspace.prepareQuery(CategoryCF)
      .searchWithIndex()
      .addExpression()
        .whereColumn("title").equals().value(title)
      .withColumnSlice("lang")
      .execute()
      .getResult()
    rows.foreach{ row =>
      if(row.getColumns().getStringValue("lang", null) == lang) 
        return row.getKey()
    }
    return null
  }
  
  def get(id: Integer): Category = {
    val row = Keyspace.prepareQuery(CategoryCF)
      .getKey(id)
      .withColumnSlice(COLUMNS)
      .execute()
      .getResult()
    return new Category(id, row)
  }
  
  class CategoryIterator(val rows: Iterator[Row[Integer, String]]) extends java.util.Iterator[Category] {
	  def hasNext = rows.hasNext
	  def next() = new Category(rows.next)
	  def remove() = rows.remove()
  }
  
  def getAllCategories(): java.util.Iterator[Category] = {
    val rows = Keyspace.prepareQuery(CategoryCF)
      .getAllRows()
      .setBlockSize(1000)
      .withColumnSlice(COLUMNS)
      .execute()
      .getResult()
      .iterator()
    return new CategoryIterator(rows)
  }
}