ThisBuild / version := "0.1-SNAPSHOT"
ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file(".")).settings(
  name := "xenocraft",
  idePackagePrefix := Some("dev.hawu.plugins.xenocraft"),
  resolvers ++= Seq(
    Resolver.mavenLocal,
    "spigot" at "https://hub.spigotmc.org/nexus/content/repositories/snapshots",
  ),
  libraryDependencies ++= Seq(
    "org.spigotmc" % "spigot-api" % "1.19.3-R0.1-SNAPSHOT",
    "dev.hawu.plugins" % "hikari-library" % "2.0.1-SNAPSHOT" exclude
      ("org.bukkit", "bukkit"),
  ),
  scalacOptions ++= Seq(
    "--deprecation",
    "--explain-types",
    "-Wunused:nowarn",
    "-Werror",
    "-feature",
    "-language:implicitConversions",
  ),
  artifactName := { (_, module, _) =>
    val standardName = module.name.head.toUpper + module.name.tail.toLowerCase
    s"$standardName-${module.revision}.jar"
  },
)
