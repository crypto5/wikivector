package org.wikivector.misc

import org.wikivector.dao.LangLinkDao
import java.nio.charset.Charset
import org.wikivector.dao.WikiPageDao
import scala.collection.JavaConversions._
import org.wikivector.dao.WikiPageCategoryDao
import org.wikivector.dao.LexemeCategoryInfoDao
import org.wikivector.dao.LexemeCategoryCountersDao
import org.wikivector.common.Lexer
import org.wikivector.dao.LexemeCountersDao
import org.wikivector.dao.CategoryDao
import scala.collection.mutable

object Tests extends App {
  LexemeCategoryInfoDao.get(Set("java"))("java")
    .map(_.categoryId)
    .foreach(categoryId => println(CategoryDao.get(categoryId)))
}