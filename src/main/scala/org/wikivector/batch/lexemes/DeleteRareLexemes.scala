package org.wikivector.batch.lexemes

import org.wikivector.dao.LexemeCountersDao
import scala.collection.JavaConversions._
import scala.collection.mutable
import org.wikivector.model.LexemeCounters

object DeleteRareLexemes extends App {
  run()
  
  def run() {
	var processed = 0L
	var deleted = 0L
	val toDelete = new mutable.ArrayBuffer[String]
	LexemeCountersDao.getAllLexemeCounters.foreach{ lexeme =>
	  if(lexeme.totalCount < 3) toDelete += lexeme.lexeme
	  processed += 1
	  if(processed % 1000 == 0) {
	    LexemeCountersDao.incrementCounters(toDelete.map(new LexemeCounters(_, 1, 1, 1)))
	    LexemeCountersDao.deleteCounters(toDelete)
	    deleted += toDelete.size
	    toDelete.clear
	    println("Processed %d lexemes. %d deleted.".format(processed, deleted))
	  }
	}
  }
}