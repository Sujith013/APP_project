name := """app_project"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.15"

libraryDependencies += guice
libraryDependencies += ws

libraryDependencies ++= Seq(
  "com.google.apis" % "google-api-services-youtube" % "v3-rev20240514-2.0.0",
  "com.google.apis" % "google-api-services-youtube" % "v3-rev20240514-2.0.0",
  "com.google.api-client" % "google-api-client" % "2.6.0",
  "com.google.oauth-client" % "google-oauth-client-jetty" % "1.36.0",
  "com.google.http-client" % "google-http-client-jackson2" % "1.44.2",
  "org.mockito" % "mockito-core" % "5.11.0" % Test,
  "junit" % "junit" % "4.13.2" % Test,
  "org.scalatest" %% "scalatest" % "3.2.19" % Test,
  "org.powermock" % "powermock-api-mockito2" % "2.0.9" % Test,
  "org.powermock" % "powermock-module-junit4" % "2.0.9" % Test,
  "ch.qos.logback" % "logback-classic" % "1.5.6",
  "org.apache.pekko" %% "pekko-http" % "1.0.1",
  "org.apache.pekko" %% "pekko-stream" % "1.0.3",
  "org.apache.pekko" %% "pekko-testkit" % "1.0.3" % Test,
  "org.apache.pekko" %% "pekko-stream-testkit" % "1.0.3" % Test)

jacocoExcludes := Seq(
  "views.*",          // Exclude all classes in the `views` package
  "controllers.routes.*", // Example: Exclude Play auto-generated routes file
)



