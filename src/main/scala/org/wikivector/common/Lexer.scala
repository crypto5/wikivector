package org.wikivector.common

import java.io.StringReader
import edu.stanford.nlp.process.PTBTokenizer
import edu.stanford.nlp.process.CoreLabelTokenFactory
import scala.collection.mutable
import org.tartarus.snowball.ext.englishStemmer
import org.tartarus.snowball.ext.russianStemmer
import org.tartarus.snowball.ext.dutchStemmer
import org.tartarus.snowball.ext.germanStemmer
import org.tartarus.snowball.SnowballStemmer
import org.tartarus.snowball.ext.frenchStemmer
import org.tartarus.snowball.ext.italianStemmer
import org.tartarus.snowball.ext.portugueseStemmer
import org.tartarus.snowball.ext.spanishStemmer
import org.tartarus.snowball.ext.turkishStemmer

object Lexer {
  def process(text:String, lang: String):Seq[String] = {
    val out = new mutable.ArrayBuffer[String]
    tokenize(text).foreach{ token =>
      val stemmed = stem(token, lang)
      if(stemmed.length() > 2 && stemmed.length() < 20) out += stemmed.toLowerCase()
    }
    return out
  }
  
  def tokenize(text: String): Seq[String] = {
    val tokens = new mutable.ArrayBuffer[String]
    val stringReader = new StringReader(text)
    val tokenizer = new PTBTokenizer(stringReader, new CoreLabelTokenFactory, 
            "untokenizable=noneKeep")
    val out = new mutable.ArrayBuffer[String]
    while(tokenizer.hasNext()) {
      tokens += tokenizer.next.word().toLowerCase()
    }
    return tokens
  }
  
  def stem(token: String, lang: String): String = {
    val stemmer = getStemmer(lang)
    stemmer.setCurrent(token)
    stemmer.stem()
    return stemmer.getCurrent()
  }
  
  def getStemmer(lang: String): SnowballStemmer =
    lang match {
      case "en" => new englishStemmer
      case "ru" => new russianStemmer
      case "ar" => new turkishStemmer
      case "nl" => new dutchStemmer
      case "de" => new germanStemmer
      case "fr" => new frenchStemmer
      case "pl" => new englishStemmer
      case "it" => new italianStemmer
      case "pt" => new portugueseStemmer
      case "es" => new spanishStemmer
      case "uk" => new russianStemmer
      case _    => new englishStemmer
    }
}