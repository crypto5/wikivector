package org.wikivector.dao

import org.wikivector.model.LexemeCategoryCounter
import org.wikivector.config.CassandraConfig._
import com.netflix.astyanax.util.RangeBuilder
import scala.collection.JavaConversions._

object LexemeCategoryCountersDao {
  def getTitleCounters(lexeme: String): Map[Integer, Long] = {
    val result = Keyspace.prepareQuery(LexemeCategoryTitleCountersCF)
      .getKey(lexeme)
      .withColumnRange(new RangeBuilder().build())
      .execute()
      .getResult()
    return result.getColumnNames().map(column => (column, result.getLongValue(column, 0L).toLong)).toMap
  }
  
  def getDocCounters(lexeme: String): Map[Integer, Long] = {
    val result = Keyspace.prepareQuery(LexemeCategoryDocCountersCF)
      .getKey(lexeme)
      .withColumnRange(new RangeBuilder().build())
      .execute()
      .getResult()
    return result.getColumnNames().map(column => (column, result.getLongValue(column, 0L).toLong)).toMap
  }
  
  def getTotalCounters(lexeme: String): Map[Integer, Long] = {
    val result = Keyspace.prepareQuery(LexemeCategoryTotalCountersCF)
      .getKey(lexeme)
      .withColumnRange(new RangeBuilder().build())
      .execute()
      .getResult()
    return result.getColumnNames().map(column => (column, result.getLongValue(column, 0L).toLong)).toMap
  }
  
  def saveTitleCounters(counters: Seq[LexemeCategoryCounter]) {
    counters.grouped(100).foreach{ subCounters =>
      val m = Keyspace.prepareMutationBatch()
      subCounters.foreach{ subCounter =>
        m.withRow(LexemeCategoryTitleCountersCF, subCounter.lexeme)
          .incrementCounterColumn(subCounter.categoryId, subCounter.count)
      }
      m.execute()
    }
  }
  
  def saveDocCounters(counters: Seq[LexemeCategoryCounter]) {
    counters.grouped(100).foreach{ subCounters =>
      val m = Keyspace.prepareMutationBatch()
      subCounters.foreach{ subCounter =>
        m.withRow(LexemeCategoryDocCountersCF, subCounter.lexeme)
          .incrementCounterColumn(subCounter.categoryId, subCounter.count)
      }
      m.execute()
    }
  }
  
  def saveTotalCounters(counters: Seq[LexemeCategoryCounter]) {
    counters.grouped(100).foreach{ subCounters =>
      val m = Keyspace.prepareMutationBatch()
      subCounters.foreach{ subCounter =>
        m.withRow(LexemeCategoryTotalCountersCF, subCounter.lexeme)
          .incrementCounterColumn(subCounter.categoryId, subCounter.count)
      }
      m.execute()
    }
  }
}