package org.wikivector.tools

import org.wikivector.model.LexemeCategoryInfo
import org.wikivector.dao.LexemeCategoryInfoDao
import scala.collection.JavaConversions._
import org.msgpack.MessagePack
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import org.wikivector.dao.CategoryDao
import org.wikivector.dao.LexemeCountersDao
import java.util.zip.ZipOutputStream
import java.util.zip.GZIPOutputStream

object DumpDb extends App {
  ToolsConfig.init()
  if(args.length == 0) {
    println("Please provide destination folder as argument.")
    exit
  }
  dumpLexemeCategoryInfo()
  dumpLexemeCounters()
  dumpCategories()
  
  
  def dumpLexemeCategoryInfo() {
    val destDir = args(0)
    val messagePack = new MessagePack
    val out = new GZIPOutputStream(new FileOutputStream(destDir + "/lexeme_category_info.bin"))
    var processed = 0L
    LexemeCategoryInfoDao.getAll.foreach{ info =>
      val data = messagePack.write(info)
      out.write(data)
      processed += 1
      if(processed % 1000 == 0) println("Saved " + (processed / 1000) + "k entries to lexeme_category_info.bin.")
    }
    out.close
  }
  
  def dumpLexemeCounters() {
    val destDir = args(0)
    val messagePack = new MessagePack
    val out = new GZIPOutputStream(new FileOutputStream(destDir + "/lexeme_counters.bin"))
    var processed = 0L
    LexemeCountersDao.getAllLexemeCounters.foreach{ counter =>
      val data = messagePack.write(counter)
      out.write(data)
      processed += 1
      if(processed % 1000 == 0) println("Saved " + (processed / 1000) + "k entries to lexeme_counters.bin.")
    }
    out.close
  }
  
  def dumpCategories() {
    val destDir = args(0)
    val messagePack = new MessagePack
    val out = new GZIPOutputStream(new FileOutputStream(destDir + "/categories.bin"))
    var processed = 0L
    CategoryDao.getAllCategories.foreach{ category =>
      val data = messagePack.write(category)
      out.write(data)
      processed += 1
      if(processed % 1000 == 0) println("Saved " + (processed / 1000) + "k entries categories.bin.")
    }
    out.close
  }
}