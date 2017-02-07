import sbt.Keys.libraryDependencies
import scalariform.formatter.preferences._



val commonSettings = Seq(
  scalaVersion := "2.12.1",
  organization := "com.zahari",
  crossScalaVersions := Seq("2.10.4", "2.11.5"),
  scalacOptions ++= Seq("-Xfatal-warnings", "-feature","-deprecation"))


lazy val `ductaper` = project.in(file(".")).
  configs(IntegrationTest).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.rabbitmq" % "amqp-client" % "3.4.2",
      "org.slf4j" % "slf4j-api" % "1.7.5",
      "org.slf4j" % "slf4j-simple" % "1.7.5",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "org.scalamock" %% "scalamock-scalatest-support" % "3.4.2" % "test",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.6",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.6"))

scalariformSettings ++ Seq(
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DoubleIndentClassDeclaration, true)
    .setPreference(PreserveDanglingCloseParenthesis, true)
    .setPreference(PreserveSpaceBeforeArguments, true)
    .setPreference(RewriteArrowSymbols, true)
)

