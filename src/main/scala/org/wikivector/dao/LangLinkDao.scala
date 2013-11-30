package org.wikivector.dao

import org.wikivector.config.MySqlConfig
import scala.collection.mutable

object LangLinkDao {
  def getTranslations(enWikiPageId: Int): Seq[String] = {
    val conn = MySqlConfig.getConnection()
    val rs = conn.createStatement().executeQuery("select ll_title from langlinks where ll_from = " + enWikiPageId)
    val res = new mutable.ArrayBuffer[String]
    while(rs.next()) {
      res += new String(rs.getBytes(1))
    }
    rs.close()
    conn.close()
    return res
  }
}