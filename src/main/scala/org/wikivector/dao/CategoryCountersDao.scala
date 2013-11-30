package org.wikivector.dao

import org.wikivector.config.CassandraConfig._
import scala.collection.JavaConversions._
import com.netflix.astyanax.model.Row
import scala.collection.mutable

object CategoryCountersDao {
  def incrementDocCounter(categoryId: Integer) {
    val m = Keyspace.prepareMutationBatch()
    m.withRow(CategoryCountersCF, categoryId)
      .incrementCounterColumn("doc_count", 1)
    m.execute()
  }
  
  def incrementTotalCounter(categoryId: Integer, count: Int) {
    val m = Keyspace.prepareMutationBatch()
    m.withRow(CategoryCountersCF, categoryId)
      .incrementCounterColumn("total_count", count)
    m.execute()
  }
  
  def getDocCounter(categoryId: Integer): Long = {
    return getDocCounters(Set(categoryId)).getOrElse(categoryId, 0L)
  }
  
  def getTotalCounter(categoryId: Integer): Long = {
    return getTotalCounters(Set(categoryId)).getOrElse(categoryId, 0L)
  }
  
  def getDocCounters(categoryIds: Set[Integer]): Map[Integer, Long] = {
    val result = new mutable.ArrayBuffer[(Integer, Long)]
    categoryIds.grouped(100).foreach{ subIds =>
      result ++= Keyspace.prepareQuery(CategoryCountersCF)
        .getKeySlice(subIds)
        .withColumnSlice("doc_count")
        .execute()
        .getResult()
        .iterator()
        .map(row => (row.getKey(), row.getColumns().getLongValue("doc_count", 0)))
    }
    return result.toMap
  }
  
  def getTotalCounters(categoryIds: Set[Integer]): Map[Integer, Long] = {
    val result = new mutable.ArrayBuffer[(Integer, Long)]
    categoryIds.grouped(100).foreach{ subIds =>
      result ++= Keyspace.prepareQuery(CategoryCountersCF)
        .getKeySlice(subIds)
        .withColumnSlice("total_count")
        .execute()
        .getResult()
        .iterator()
        .map(row => (row.getKey(), row.getColumns().getLongValue("total_count", 0)))
    }
    return result.toMap
  }
}