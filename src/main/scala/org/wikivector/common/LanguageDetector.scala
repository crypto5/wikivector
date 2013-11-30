package org.wikivector.common

import java.io.File
import java.io.IOException
import java.io.StringWriter
import java.net.URL
import java.security.CodeSource
import java.util.ArrayList
import java.util.List
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import org.apache.commons.io.IOUtils
import com.cybozu.labs.langdetect.Detector
import com.cybozu.labs.langdetect.DetectorFactory
import com.cybozu.labs.langdetect.LangDetectException;
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

object LanguageDetector {
  DetectorFactory.loadProfile(getProfiles())

  def getLanguageForWord(word: String): String = {
    try {
      val detector = DetectorFactory.create()
      detector.append(word)
      return detector.detect()
    } catch {
      case e: LangDetectException => "en"
    }
  }
  
  def getLanguageForText(text: String): String = {
    try {
      val detector = DetectorFactory.create()
      Lexer.tokenize(text).foreach{ word =>
        detector.append(word)
      }
      return detector.detect()
    } catch {
      case e: LangDetectException => "en"
    }
  }

  def getDetector = DetectorFactory.create()

  def getProfiles(): Seq[String] = {
    val src = classOf[Detector].getProtectionDomain().getCodeSource()
    val list = new ArrayBuffer[String]

    if (src != null) {
      val jarFile = src.getLocation()
      val zipFile = new ZipFile(jarFile.getFile())
      val zipStream = new ZipInputStream(jarFile.openStream())
      val profilePrefix = "profiles" + File.separator

      var ze = zipStream.getNextEntry()
      while (ze != null) {
        val entryName = ze.getName()
        val beginIndex = entryName.lastIndexOf(File.separator)
        if (entryName.startsWith(profilePrefix) && entryName.substring(beginIndex).length() > 1) {
          val sw = new StringWriter()
          IOUtils.copy(zipFile.getInputStream(ze), sw)
          val profile = sw.toString()
          list += profile
        }
        ze = zipStream.getNextEntry()
      }
    }
    return list;
  }
}
