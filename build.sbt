import Dependencies._

ThisBuild / scalaVersion     := "2.13.3"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.dodona"
ThisBuild / organizationName := "dodona"

val AkkaVersion = "2.6.9"
val AkkaHttpVersion = "10.2.0"
val CirceVersion = "0.12.3"

lazy val root = (project in file("."))
  .settings(
    name := "dodona-data-collector",
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe" % "config" % "1.4.0",
      "com.influxdb" % "influxdb-client-scala" % "1.12.0"
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % CirceVersion)
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
