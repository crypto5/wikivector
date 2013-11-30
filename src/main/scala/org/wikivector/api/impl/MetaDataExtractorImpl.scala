package org.wikivector.api.impl

import org.wikivector.api._
import org.wikivector.common.Lexer
import com.cybozu.labs.langdetect.Language
import org.wikivector.common.LanguageDetector
import org.wikivector.dao.LexemeCategoryInfoDao
import org.wikivector.dao.LexemeCountersDao
import org.wikivector.model.LexemeCounters
import scala.math._
import org.wikivector.common.WikiStats
import scala.collection.immutable
import scala.collection.mutable
import org.wikivector.model.LexemeCategoryInfo
import org.wikivector.dao.CategoryDao
import scala.collection.JavaConversions._

object MetaDataExtractorImpl {
  def extract(text: String): Response = {
    val lang = LanguageDetector.getLanguageForText(text)
    val tokens = Lexer.process(text, lang)
    val lexemeCounters = LexemeCountersDao.getNonStopCounters(tokens.toSet, lang)
    val tfIdfKeywords = calculateTfIdf(lang, tokens, lexemeCounters)
    val categories = calculateWikiCategories(tfIdfKeywords)
    val categoryLexemes = calculateCategoryLexemes(categories)
    return new Response(
        tfIdfKeywords.sortBy(-_.weight),
        categoryLexemes.sortBy(-_.weight),
        categories.sortBy(-_.weight))
  }
  
  def calculateCategoryLexemes(categories: java.util.List[WeightedCategory]): java.util.List[WeightedLexeme] = {
    val lexemesMap = new mutable.HashMap[String, Double]
    for(category <- categories;
      lexeme <- category.lexemes
    ) {
      lexemesMap += lexeme.lexeme -> (lexemesMap.getOrElse(lexeme.lexeme, 0.toDouble) + lexeme.weight)
    }
    return lexemesMap.map(lexeme => new WeightedLexeme(lexeme._1, lexeme._2)).toSeq
  }
  
  def calculateWikiCategories(tfIdfKeywords: Seq[WeightedLexeme]): java.util.List[WeightedCategory] = {
    val lexemeCategoryInfos = LexemeCategoryInfoDao.get(tfIdfKeywords.map(_.lexeme).toSet)
    val infoByCategoryId = new mutable.HashMap[Int, mutable.Set[LexemeCategoryInfo]] with mutable.MultiMap[Int, LexemeCategoryInfo]
    for(lexeme <- lexemeCategoryInfos.keys;
        info <- lexemeCategoryInfos(lexeme)) {
      infoByCategoryId.addBinding(info.categoryId, info)
    }
    val weightedCategories = infoByCategoryId
      .map(_._1)
      .map(categoryId => new WeightedCategory(categoryId, CategoryDao.get(categoryId).title, calculateWeightedLexemes(infoByCategoryId(categoryId).toSet, tfIdfKeywords)))
      .toSeq
      .sortBy(_.weight)
      .filter(weightedCategory => infoByCategoryId(weightedCategory.categoryId).size > 1)
    return weightedCategories
  }
  
  def calculateWeightedLexemes(infos: Set[LexemeCategoryInfo], tfIdfKeywords: Seq[WeightedLexeme]): Seq[WeightedLexeme] = {
    val keywordsMap = tfIdfKeywords.map(keyword => keyword.lexeme -> keyword.weight).toMap
    val weightedInfos = infos
      .filter(info => info.categoryDocCount > 5 && info.categoryDocCount < 1000)
      .map(info => new WeightedLexeme(info.lexeme, calculateInfoWeight(info, keywordsMap(info.lexeme))))
    val topInfos = weightedInfos
      .toSeq
      .sortBy(- _.weight)
      .take(10)
    return topInfos
  }
  
  def calculateInfoWeight(info: LexemeCategoryInfo, tfIdf: Double) = sigmoid(info.lexemeCategoryTotalCount.toDouble / info.lexemeTotalCount) *
          tfIdf *
          sigmoid(info.lexemeCategoryTotalCount.toDouble / info.categoryTotalCount)
  
  def sigmoid(a: Double): Double = 1 / (1 + Math.exp(-a))
  
  def calculateTfIdf(lang: String, tokens: Seq[String], lexemeCounters: Seq[LexemeCounters]): Seq[WeightedLexeme] = {
    val tf = lexemeCounters.map(counter => counter.lexeme -> tokens.count(_ == counter.lexeme))
    val idf = lexemeCounters
      .map(counter => counter.lexeme -> calculateIdf(counter, lang))
      .toMap
    val tfIdfKeywords = tf.map(tfEntry => tfEntry._1 -> tfEntry._2 * idf(tfEntry._1)).toMap
    return tfIdfKeywords.filter(_._2 >= 0)
      .map(keyword => new WeightedLexeme(keyword._1, keyword._2))
      .toSeq
  }
  
  def calculateIdf(lexemeCounter: LexemeCounters, lang: String): Double = {
    if(lexemeCounter.docCount == 0) return 0
    return Math.log(WikiStats.pagesCountByLang(lang).toDouble / lexemeCounter.docCount)
  }
}