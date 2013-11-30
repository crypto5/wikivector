package org.wikivector.dao

import org.wikivector.config.CassandraConfig._
import com.netflix.astyanax.util.RangeBuilder
import scala.collection.JavaConversions._

object CategoryDirectParentsDao {
  def save(id: Integer, parentId: Integer) {
    Keyspace.prepareColumnMutation(CategoryDirectParentsCF, id, parentId)
    	.putValue(1, null)
    	.execute()
  }
  
  def save(data: Set[(Integer, Integer)]) {
    val m = Keyspace.prepareMutationBatch()
    data.foreach{ row =>
      m.withRow(CategoryDirectParentsCF, row._1)
        .putColumn(row._2, 1)
    }
    m.execute()
  }
  
  def getParents(id: Integer): Set[Integer] = {
    Keyspace.prepareQuery(CategoryDirectParentsCF)
      .getKey(id)
      .withColumnRange(new RangeBuilder().setMaxSize(10000).build())
      .execute()
      .getResult()
      .getColumnNames()
      .toSet
  }
}