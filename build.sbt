import Dependencies._

ThisBuild / scalaVersion     := "2.13.3"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.dodona"
ThisBuild / organizationName := "dodona"

val AkkaVersion = "2.6.9"
val AkkaHttpVersion = "10.2.0"
val CirceVersion = "0.12.3"
val SlickVersion = "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "dodona-data-collector",
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe" % "config" % "1.4.0",
      "com.influxdb" % "influxdb-client-scala" % "1.12.0",
      "com.typesafe.slick" %% "slick" % SlickVersion,
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion,
      "org.xerial" % "sqlite-jdbc" % "3.32.3.2"
    ),
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % CirceVersion)
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
