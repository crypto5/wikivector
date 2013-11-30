import sbt._
import Keys._

object ApplicationBuild extends Build {
  val wikivector = Project("wikivector", base = file("."))
}
