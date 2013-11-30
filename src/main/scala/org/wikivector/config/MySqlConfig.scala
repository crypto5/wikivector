package org.wikivector.config

import java.sql.Connection
import java.sql.DriverManager

object MySqlConfig {
  Class.forName("com.mysql.jdbc.Driver")
  
  def getConnection() = DriverManager.getConnection(
      "jdbc:mysql://localhost/wikivector?useUnicode=true&characterEncoding=UTF-8", 
      "a", "1")
}