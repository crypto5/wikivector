import AssemblyKeys._

assemblySettings

name := "wikivector"

version := "1.0.0"

scalaVersion := "2.10.3"

crossPaths := false

libraryDependencies += "info.bliki.wiki" % "bliki-core" % "3.0.19"

libraryDependencies += "com.google.guava" % "guava" % "11.0.2"

libraryDependencies += "com.netflix.astyanax" % "astyanax-core" % "1.56.34"

libraryDependencies += "com.netflix.astyanax" % "astyanax-cassandra" % "1.56.34"

libraryDependencies += "com.netflix.astyanax" % "astyanax-thrift" % "1.56.34"

libraryDependencies += "com.cybozu.labs" % "langdetect" % "1.1-20120112"

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.25"

libraryDependencies += "commons-codec" % "commons-codec" % "1.8"

libraryDependencies += "org.mapdb" % "mapdb" % "0.9.2"

libraryDependencies += "org.msgpack" % "msgpack" % "0.6.7"

libraryDependencies += "xerces" % "xerces" % "2.4.0"

libraryDependencies += "edu.stanford.nlp" % "stanford-parser" % "3.2.0"

resolvers += "BlikiWiki" at "http://gwtwiki.googlecode.com/svn/maven-repository/"

fork in run := true

javaOptions in run += "-Xmx2048m"

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList("javax", xs @ _*) => MergeStrategy.first
    case "Errors$2.class" => MergeStrategy.first
    case x => old(x)
  }
}

