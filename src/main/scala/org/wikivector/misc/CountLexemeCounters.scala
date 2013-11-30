package org.wikivector.misc

import org.wikivector.dao.LexemeCountersDao
import scala.collection.mutable
import scala.collection.JavaConversions._

object CountLexemeCounters extends App {
	val lexemeCounters = LexemeCountersDao.getAllLexemeCounters
	val histogram = new mutable.HashMap[Long, Long]
	var processed = 0L
	lexemeCounters.foreach{ lexeme =>
	  if(lexeme.totalCount < 20) {
	    val count = histogram.getOrElse(lexeme.totalCount, 0L)
	    histogram += lexeme.totalCount -> (count + 1L)
	  }
	  processed += 1
	  if(processed % 1000 == 0) println("Processed %d lexemes.".format(processed))
	}
	println(histogram)
}