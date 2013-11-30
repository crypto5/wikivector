package org.wikivector.dao

import org.wikivector.config.CassandraConfig._
import scala.collection.JavaConversions._

object ProcessedDao {
  def markProcessed(key: String) {
    val m = Keyspace.prepareMutationBatch()
    m.withRow(ProcessedCF, key)
    	.putColumn("value", "1")
    m.execute()
  }
  
  def isProcessed(key: String): Boolean = {
    val columns = Keyspace.prepareQuery(ProcessedCF)
      .getKey(key)
      .execute()
      .getResult()
    return !columns.isEmpty()
  }
  
  def isProcessed(keys: Set[String]): Set[String] = {
    val rows = Keyspace.prepareQuery(ProcessedCF)
      .getKeySlice(keys)
      .execute()
      .getResult()
    return rows.getKeys().toSet
  }
}