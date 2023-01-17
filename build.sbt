ThisBuild / version := "0.1-SNAPSHOT"
ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "xenocraft",
    idePackagePrefix := Some("dev.hawu.plugins.xenocraft"),
    resolvers ++= Seq(
      "spigot-repo" at "https://hub.spigotmc.org/nexus/content/repositories/snapshots/",
      Resolver.mavenLocal,
    ),
    libraryDependencies ++= Seq(
      "dev.hawu.plugins" % "hikari-library" % "2.0.1-SNAPSHOT" exclude("org.bukkit", "bukkit") exclude("com.google", "gson"),
      "org.spigotmc" % "spigot-api" % "1.16.1-R0.1-SNAPSHOT",
    ),
    artifactName := { (_, module: ModuleID, _) =>
      s"Xenocraft-${module.revision}.jar"
    },
  )
