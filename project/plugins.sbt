resolvers += Classpaths.sbtPluginReleases

// Plugin for code formatting:
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

// Plugin for checking code style:
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0")
