package org.wikivector.dao

import org.wikivector.model.LexemeCategoryInfo
import org.wikivector.config.CassandraConfig._
import org.msgpack.MessagePack
import scala.collection.mutable
import scala.collection.JavaConversions._
import com.netflix.astyanax.util.RangeBuilder
import com.netflix.astyanax.model.Row
import com.netflix.astyanax.model.Column

object LexemeCategoryInfoDao {
  val messagePack = new MessagePack
  
  def save(info: LexemeCategoryInfo) {
    save(Seq(info))
  }
  
  def save(infos: Seq[LexemeCategoryInfo]) {
    val m = Keyspace.prepareMutationBatch()
    infos.foreach{ info =>
      m.withRow(LexemeCategoryInfoCF, info.lexeme)
        .putColumn(info.categoryId, messagePack.write(info))
    }
    m.execute()
  }
  
  def get(lexemes: Set[String]): mutable.MultiMap[String, LexemeCategoryInfo] = {
    val infos = new mutable.HashMap[String, mutable.Set[LexemeCategoryInfo]] with mutable.MultiMap[String, LexemeCategoryInfo]
    val rows = Keyspace.prepareQuery(LexemeCategoryInfoCF)
        .getKeySlice(lexemes)
        .withColumnRange(new RangeBuilder().setMaxSize(Integer.MAX_VALUE).build())
        .execute()
        .getResult()
        .iterator()
    rows.foreach{ row =>
      row.getColumns().foreach{ column =>
        val info = messagePack.read(column.getByteArrayValue(), classOf[LexemeCategoryInfo])
        infos.addBinding(row.getKey(),  info)
      }
    }
    return infos
  }
  
  class LexemeCategoryInfoIterator(val rows: Iterator[Row[String, Integer]]) extends java.util.Iterator[LexemeCategoryInfo] {
    var columns: Iterator[Column[Integer]] = null
    
    def hasNext: Boolean = {
      if(columns != null) return columns.hasNext || rows.hasNext
      return rows.hasNext
    }
    
    def next: LexemeCategoryInfo = {
      if(columns == null) {
        columns = rows.next.getColumns().iterator()
      }
      if(columns.hasNext) {
        return messagePack.read(columns.next.getByteArrayValue(), classOf[LexemeCategoryInfo])
      }
      columns = rows.next.getColumns().iterator()
      return messagePack.read(columns.next.getByteArrayValue(), classOf[LexemeCategoryInfo])
    }
    
    def remove = {}
  }
  
  def getAll: LexemeCategoryInfoIterator = {
    val rows = Keyspace.prepareQuery(LexemeCategoryInfoCF)
      .getAllRows()
      .setBlockSize(100)
      .withColumnRange(new RangeBuilder().setMaxSize(Integer.MAX_VALUE).build())
      .execute()
      .getResult()
      .iterator()
    return new LexemeCategoryInfoIterator(rows)
  }
}