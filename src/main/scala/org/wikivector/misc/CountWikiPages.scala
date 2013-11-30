package org.wikivector.misc

import scala.collection.JavaConversions._
import org.wikivector.dao.WikiPageDao

object CountWikiPages extends App {
  var total = 0L
  var enPages = 0L
  var nonEnPages = 0L
  var notTranslated = 0L
  val rows = WikiPageDao.getAllWikiPages
  while (rows.hasNext()) {
    val page = rows.next()
    if(page.lang == "en") {
      enPages += 1
    } else {
      nonEnPages += 1
      if(page.enPageId == null) notTranslated += 1
    }
    total += 1
    if (total % 1000 == 0) println("total = %d, enPages = %d, nonEnPages = %d, notTranslated = %d".format(total, enPages, nonEnPages, notTranslated))
  }
}