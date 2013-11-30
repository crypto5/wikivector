package org.wikivector.tools

import org.msgpack.MessagePack
import java.io.BufferedInputStream
import java.io.FileInputStream
import scala.collection.mutable
import org.wikivector.model.LexemeCategoryInfo
import scala.collection.JavaConversions._
import org.wikivector.dao.LexemeCategoryInfoDao
import org.wikivector.model.LexemeCounters
import org.wikivector.dao.LexemeCountersDao
import org.wikivector.model.Category
import org.wikivector.dao.CategoryDao
import java.util.zip.GZIPInputStream
import java.io.EOFException

object LoadDb extends App {
  ToolsConfig.init()
  if (args.length == 0) {
    println("Please provide source folder as argument.")
    exit
  }
  loadLexemeCategoryInfo()
  loadLexemeCounters()
  loadCategories()

  def loadLexemeCategoryInfo() {
    val srcDir = args(0)
    val messagePack = new MessagePack
    val fin = new FileInputStream(srcDir + "/lexeme_category_info.bin")
    val in = new GZIPInputStream(fin)
    var processed = 0L
    val buffer = new mutable.ArrayBuffer[LexemeCategoryInfo]
    try {
      while (true) {
        val info = messagePack.read(in, classOf[LexemeCategoryInfo])
        buffer += info
        processed += 1
        if (buffer.size == 1000) {
          println("Processed " + (processed / 1000) + "k records from lexeme_category_info.bin.")
          LexemeCategoryInfoDao.save(buffer)
          buffer.clear
        }
      }
    } catch {
      case e: EOFException => {}
    }
    LexemeCategoryInfoDao.save(buffer)
    in.close()
  }

  def loadLexemeCounters() {
    val srcDir = args(0)
    val messagePack = new MessagePack
    val fin = new FileInputStream(srcDir + "/lexeme_counters.bin")
    val in = new GZIPInputStream(fin)
    var processed = 0L
    val buffer = new mutable.ArrayBuffer[LexemeCounters]
    try {
      while (true) {
        val counters = messagePack.read(in, classOf[LexemeCounters])
        buffer += counters
        processed += 1
        if (buffer.size == 1000) {
          println("Processed " + (processed / 1000) + "k records from lexeme_counters.bin.")
          LexemeCountersDao.incrementCounters(buffer)
          buffer.clear
        }
      }
    } catch {
      case e: EOFException => {}
    }
    LexemeCountersDao.incrementCounters(buffer)
    in.close()
  }

  def loadCategories() {
    val srcDir = args(0)
    val messagePack = new MessagePack
    val fin = new FileInputStream(srcDir + "/categories.bin")
    val in = new GZIPInputStream(fin)
    var processed = 0L
    try {
      while (true) {
        val category = messagePack.read(in, classOf[Category])
        CategoryDao.save(category)
        processed += 1
        if (processed % 1000 == 0) {
          println("Processed " + (processed / 1000) + "k records from categories.bin.")
        }
      }
    } catch {
      case e: EOFException => {}
    }
    in.close()
  }
}