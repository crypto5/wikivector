package org.wikivector.dao

import org.wikivector.config.CassandraConfig._
import com.netflix.astyanax.util.RangeBuilder
import scala.collection.JavaConversions._

object CategoryAllParentsDao {
  def save(id: Integer, parentId: Integer) {
    Keyspace.prepareColumnMutation(CategoryAllParentsCF, id, parentId)
    	.putValue(1, null)
    	.execute()
  }
  
  def save(data: Set[(Integer, Integer)]) {
    val m = Keyspace.prepareMutationBatch()
    data.foreach{ row =>
      m.withRow(CategoryAllParentsCF, row._1)
        .putColumn(row._2, 1)
    }
    m.execute()
  }
  
  def getParents(id: Integer): Set[Integer] = {
    return Keyspace.prepareQuery(CategoryAllParentsCF)
      .getKey(id)
      .withColumnRange(new RangeBuilder().setMaxSize(10000).build())
      .execute()
	  .getResult()
	  .getColumnNames()
	  .toSet
  }
}