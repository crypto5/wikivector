package org.wikivector.model

import com.netflix.astyanax.model.Row
import com.netflix.astyanax.model.ColumnList

case class WikiPage(
    val id: String, 
    val title: String, 
    val text: String, 
    val isCategory: Boolean, 
    val lang: String,
    val enPageId: String = null) {
  
  def this(id: String, columns: ColumnList[String]) {
    this(id,
        columns.getStringValue("title", ""),
        columns.getStringValue("text", ""),
        columns.getBooleanValue("isCategory", false),
        columns.getStringValue("lang", "en"),
        columns.getStringValue("en_page_id", null))
  }
  
  def this(row: Row[String, String]) {
    this(row.getKey(),
        row.getColumns())
  }
}