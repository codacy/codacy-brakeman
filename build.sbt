import com.typesafe.sbt.packager.docker.Cmd

name := "codacy-brakeman"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq("com.typesafe.play" %% "play-json" % "2.8.1",
                            "com.codacy" %% "codacy-engine-scala-seed" % "4.0.0")

enablePlugins(JavaAppPackaging)

enablePlugins(DockerPlugin)

version in Docker := "1.0.0-SNAPSHOT"

organization := "com.codacy"

lazy val toolVersion = taskKey[String]("Retrieve the version of the underlying tool from patterns.json")
toolVersion := {
  import better.files.File
  import play.api.libs.json.{JsString, JsValue, Json}

  val jsonFile = resourceDirectory.in(Compile).value / "docs" / "patterns.json"
  val patternsJsonValues = Json.parse(File(jsonFile.toPath).contentAsString).as[Map[String, JsValue]]

  patternsJsonValues
    .collectFirst {
      case ("version", JsString(version)) => version
    }
    .getOrElse(throw new Exception("Failed to retrieve version from docs/patterns.json"))
}

mappings in Universal ++= (resourceDirectory in Compile).map { resourceDir: File =>
  val src = resourceDir / "docs"
  val dest = "/docs"

  for {
    path <- src.allPaths.get if !path.isDirectory
  } yield path -> path.toString.replaceFirst(src.toString, dest)
}.value

val dockerUser = "docker"
val dockerGroup = "docker"

daemonUser in Docker := dockerUser

daemonGroup in Docker := dockerGroup

dockerBaseImage := "codacy-brakeman-base"

dockerCommands := {
  dockerCommands.dependsOn(toolVersion).value.flatMap {
    case cmd @ (Cmd("ADD", _)) =>
      List(Cmd("RUN", s"adduser -u 2004 -D $dockerUser"), cmd, Cmd("RUN", "mv /opt/docker/docs /docs"))
    case other => List(other)
  }
}
