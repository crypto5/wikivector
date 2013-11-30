package org.wikivector.batch.page

import info.bliki.wiki.dump.WikiXMLParser
import org.wikivector.config.GlobalConfig
import info.bliki.wiki.dump.IArticleFilter
import info.bliki.wiki.dump.Siteinfo
import info.bliki.wiki.dump.WikiArticle
import org.wikivector.model.WikiPage
import org.wikivector.model.WikiPage
import org.wikivector.dao.WikiPageDao

// Read wiki xml dumps and write them to wikicategories cassandra table.
object LoadWikipages extends App {
  run()
  
  def run() {
    new WikiPageLoader("en", GlobalConfig.dataPath + "enwiki-latest-pages-articles.xml").run()
    new WikiPageLoader("ru", GlobalConfig.dataPath + "ruwiki-latest-pages-articles.xml").run()
    new WikiPageLoader("ar", GlobalConfig.dataPath + "arwiki-latest-pages-articles.xml").run()
    new WikiPageLoader("nl", GlobalConfig.dataPath + "nlwiki-latest-pages-articles.xml").run()
    new WikiPageLoader("de", GlobalConfig.dataPath + "dewiki-latest-pages-articles.xml").run()
    new WikiPageLoader("fr", GlobalConfig.dataPath + "frwiki-latest-pages-articles.xml").run()
    new WikiPageLoader("pl", GlobalConfig.dataPath + "plwiki-latest-pages-articles.xml").run()
    new WikiPageLoader("it", GlobalConfig.dataPath + "itwiki-latest-pages-articles.xml").run()
    new WikiPageLoader("pt", GlobalConfig.dataPath + "ptwiki-latest-pages-articles.xml").run()
    new WikiPageLoader("es", GlobalConfig.dataPath + "eswiki-latest-pages-articles.xml").run()
    new WikiPageLoader("uk", GlobalConfig.dataPath + "ukwiki-latest-pages-articles.xml").run()
  }
}

class WikiPageLoader(val lang: String, val path: String) extends IArticleFilter {
  var processed = 0L
  
  def run() {
    val wxp = new WikiXMLParser(path, this)
    wxp.parse
  }
  
  override def process(page: WikiArticle, siteInfo: Siteinfo) {
    val wikiPage = new WikiPage(lang + page.getId(), page.getTitle(), page.getText(), page.isCategory(), lang)
    if(wikiPage.title != null && wikiPage.text != null) {
    	WikiPageDao.save(wikiPage)
    }
    processed += 1
    if(processed % 100 == 0) println("Loaded %d wiki pages from %s.".format(processed, path))
  }
}