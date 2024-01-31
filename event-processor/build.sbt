ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.17"

//assemblyJarName in assembly := "event-processor-assembly-1.0.jar"

lazy val commonSettings = Seq(
  organization := "com.checkout",
  version := "0.1.0-SNAPSHOT"
)

lazy val app = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "event-processor"
  ).
  enablePlugins(AssemblyPlugin)

//resolvers in Global ++= Seq(
//  "Sbt plugins"                   at "https://dl.bintray.com/sbt/sbt-plugin-releases",
//  "Maven Central Server"          at "http://repo1.maven.org/maven2",
//  "TypeSafe Repository Releases"  at "http://repo.typesafe.com/typesafe/releases/",
//  "TypeSafe Repository Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
//)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}

val flinkVersion = "1.14.6"
val flinkFsVersion = "1.11.6"

libraryDependencies += "org.apache.flink" %% "flink-scala" % flinkVersion % "provided"
libraryDependencies += "org.apache.flink" %% "flink-streaming-scala" % flinkVersion % "provided"
libraryDependencies += "org.apache.flink" %% "flink-connector-kafka" % flinkVersion
libraryDependencies += "org.apache.flink" % "flink-core" % flinkVersion % "provided"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.5.1"
libraryDependencies += "org.apache.flink" %% "flink-clients" % "1.14.3"
libraryDependencies += "org.apache.flink" %% "flink-connector-filesystem" % flinkFsVersion
//libraryDependencies += "org.apache.flink" %% "flink-connector-filesystem-hadoop" % flinkFsVersion
libraryDependencies += "io.circe" %% "circe-core" % "0.14.6"
libraryDependencies += "io.circe" %% "circe-generic" % "0.14.6"
libraryDependencies += "io.circe" %% "circe-parser" % "0.14.6"


Compile / run / mainClass := Some("EventProcessor")