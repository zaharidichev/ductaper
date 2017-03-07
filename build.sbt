import sbt.Keys.libraryDependencies
import scalariform.formatter.preferences._
import sbt.ProjectExtra


lazy val `ductaper-core` = project.
  settings(publishSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.rabbitmq" % "amqp-client" % "3.4.2",
      "org.slf4j" % "slf4j-api" % "1.7.5",
      "org.slf4j" % "slf4j-simple" % "1.7.5",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % "test",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.6",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.6",
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-smile" % "2.8.6",
      "com.typesafe" % "config" % "1.3.1"))




lazy val `ductaper-dsl` = project.
  dependsOn(`ductaper-core`).
  settings(publishSettings: _*)


lazy val artifactoryCredentials = Credentials("Artifactory Realm", "artifactory.airconomist.com", System.getenv("ARTIFACTORY_USER"), System.getenv("ARTIFACTORY_PASSWORD"))

lazy val snapshotArtifactoryAdress = Some("Artifactory Realm" at "http://artifactory.airconomist.com/artifactory/ext-snapshot-local;build.timestamp=" + new java.util.Date().getTime)
lazy val releaseArtifactoryAdress = Some("Artifactory Realm" at "http://artifactory.airconomist.com/artifactory/ext-release-local")


lazy val publishSettings = Seq(publishTo := (if (isSnapshot.value) snapshotArtifactoryAdress else releaseArtifactoryAdress),
                               credentials += artifactoryCredentials)




lazy val ductaper =
     project.in( file("."))
    .aggregate(`ductaper-core`, `ductaper-dsl`)
    .settings(inThisBuild(List(
      scalaVersion := "2.12.1",
      organization := "com.zahari",
      version      := "0.1-SNAPSHOT",
      crossScalaVersions := Seq("2.10.4", "2.11.5"),
      scalacOptions ++= Seq("-Xfatal-warnings", "-feature","-deprecation"))))
    .settings(publishSettings: _*)



  scalariformSettings ++ Seq(
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(DoubleIndentClassDeclaration, true)
    .setPreference(PreserveDanglingCloseParenthesis, true)
    .setPreference(PreserveSpaceBeforeArguments, true)
    .setPreference(RewriteArrowSymbols, true)
)

