package org.wikivector.api

import scala.collection.JavaConversions._
import org.wikivector.api.impl.MetaDataExtractorImpl
import org.wikivector.model.LexemeCategoryInfo

case class Response(
    tfIdfKeywords: java.util.List[WeightedLexeme],
    categoryLexemes: java.util.List[WeightedLexeme],
    categories: java.util.List[WeightedCategory])
case class WeightedCategory(categoryId: Int, categoryTitle: String, lexemes: java.util.List[WeightedLexeme]) {
  def weight = lexemes.map(_.weight).sum
}
case class WeightedLexeme(lexeme: String, weight: Double)

object MetaDataExtractor {
  def extract(text: String) = MetaDataExtractorImpl.extract(text)
}