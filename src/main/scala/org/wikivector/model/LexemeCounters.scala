package org.wikivector.model

import com.netflix.astyanax.model.Row
import org.msgpack.annotation.Message

@Message
case class LexemeCounters(
    var lexeme: String, 
    var titleCount: Long, 
    var docCount: Long, 
    var totalCount: Long) {
  def this(row: Row[String, String]) {
    this(row.getKey(),
        row.getColumns().getLongValue("title_count", 0L),
        row.getColumns().getLongValue("doc_count", 0L),
        row.getColumns().getLongValue("total_count", 0L))
  }
  
  def this() {
    this("", 0, 0, 0)
  }
}