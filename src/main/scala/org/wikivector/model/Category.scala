package org.wikivector.model

import com.netflix.astyanax.model.Row
import com.netflix.astyanax.model.ColumnList
import org.msgpack.annotation.Message

@Message
case class Category(
    var id: Integer, 
    var title: String,   
    var lang: String,
    var pageId: String,
    var enPageId: String = null) {
  def this(id: Integer, columns: ColumnList[String]) {
    this(id,
        columns.getStringValue("title", ""),
        columns.getStringValue("lang", "en"),
        columns.getStringValue("page_id", null),
        columns.getStringValue("en_page_id", null))
  }
  
  def this(row: Row[Integer, String]) {
    this(row.getKey(), row.getColumns())
  }
  
  def this() {
    this(0, "", "", "")
  }
}