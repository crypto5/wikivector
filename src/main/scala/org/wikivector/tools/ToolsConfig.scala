package org.wikivector.tools

import java.util.Properties
import java.io.FileReader
import org.wikivector.config.CassandraConfig

object ToolsConfig {
  def init() {
    val prop = new Properties()
    try {
      prop.load(new FileReader("config.properties"))
      val seedHosts = prop.getProperty("seedhosts")
      if(seedHosts != null && seedHosts.size > 0) {
        CassandraConfig.seedHosts = seedHosts
      }
      val username = prop.getProperty("username")
      val password = prop.getProperty("password")
      if(username != null && username.size > 0 && password != null && password.size > 0) {
        CassandraConfig.username = username
        CassandraConfig.password = password
      }
    } catch {
      case e: Exception => { println("Can't find config.properties file.") }
    }
  }
}