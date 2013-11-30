package org.wikivector.dao

import org.wikivector.model.LexemeCounters
import org.wikivector.config.CassandraConfig._
import com.netflix.astyanax.model.Row
import scala.collection.JavaConversions._
import scala.collection.mutable
import org.wikivector.common.WikiStats

object LexemeCountersDao {
  val COLUMNS = Set("title_count", "doc_count", "total_count")
  
  def getCounters(lexemes: Set[String]): Seq[LexemeCounters] = {
    val result = new mutable.HashSet[LexemeCounters]
    lexemes.grouped(100).foreach{ subLexemes =>
      val rows = Keyspace.prepareQuery(LexemeCountersCF)
        .getKeySlice(subLexemes)
        .withColumnSlice(COLUMNS)
        .execute()
        .getResult()
        .iterator()
      result.addAll(new LexemeCountersIterator(rows).toSeq)
    }
    return result.toSeq
  }
  
  def getNonStopCounters(lexemes: Set[String], lang: String) =
    getCounters(lexemes)
    .filter(_.docCount.toDouble / WikiStats.pagesCountByLang(lang) < 0.05)
  
  def incrementCounters(counters: Seq[LexemeCounters]) {
    counters.grouped(1000).foreach { subcounters =>
      val m = Keyspace.prepareMutationBatch()
      subcounters.foreach { lexemeCounter =>
        m.withRow(LexemeCountersCF, lexemeCounter.lexeme)
          .incrementCounterColumn("title_count", lexemeCounter.titleCount)
          .incrementCounterColumn("doc_count", lexemeCounter.docCount)
          .incrementCounterColumn("total_count", lexemeCounter.totalCount)
      }
      m.execute()
    }
  }
  
  def deleteCounters(lexemes: Seq[String]) {
    lexemes.grouped(1000).foreach{ subLexemes =>
      val m = Keyspace.prepareMutationBatch()
      subLexemes.foreach{ lexeme =>
        m.deleteRow(Seq(LexemeCountersCF), lexeme)
      }
      m.execute()
    }
  }

  class LexemeCountersIterator(val rows: Iterator[Row[String, String]]) extends java.util.Iterator[LexemeCounters] {
    def hasNext = rows.hasNext
    def next() = new LexemeCounters(rows.next)
    def remove() = rows.remove()
  }
  
  def getAllLexemeCounters(): LexemeCountersIterator = {
    val rows = Keyspace.prepareQuery(LexemeCountersCF)
      .getAllRows()
      .setBlockSize(1000)
      .withColumnSlice(COLUMNS)
      .execute()
      .getResult()
      .iterator()
    return new LexemeCountersIterator(rows)
  }
}