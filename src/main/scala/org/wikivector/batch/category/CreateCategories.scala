package org.wikivector.batch.category

import org.wikivector.dao.WikiPageDao
import scala.collection.JavaConversions._
import org.wikivector.model.Category
import org.wikivector.dao.CategoryDao

object CreateCategories extends App {
  run()
  
  def run() {
    var processed = 0L
    var id = 1    
    WikiPageDao.getAllWikiPages.filter(_.isCategory).foreach{ page =>
      val category = new Category(id, page.title, page.lang, page.id, page.enPageId)
      CategoryDao.save(category)
      processed += 1
      id += 1
      if(processed % 1000 == 0) println(processed)
    }
  }
}