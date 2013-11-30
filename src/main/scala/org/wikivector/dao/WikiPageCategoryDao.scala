package org.wikivector.dao

import org.wikivector.config.CassandraConfig._
import com.netflix.astyanax.util.RangeBuilder
import scala.collection.JavaConversions._

object WikiPageCategoryDao {
	def save(wikiPageId: String, categoryWikiPageId: Integer) {
		val m = Keyspace.prepareMutationBatch()
		m.withRow(WikiPageCategoryCF, wikiPageId)
		  .putColumn(categoryWikiPageId, 1)
		m.execute()
	}
	
	def getParents(wikiPageId: String): Seq[Integer] = {
	  return Keyspace.prepareQuery(WikiPageCategoryCF)
	    .getKey(wikiPageId)
	    .withColumnRange(new RangeBuilder().setMaxSize(10000).build())
	    .execute()
	    .getResult()
	    .getColumnNames()
	    .toSeq
	}
}