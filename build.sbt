import com.typesafe.sbt.packager.docker.{Cmd}

name := "codacy-brakeman"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq("com.typesafe.play" %% "play-json" % "2.8.1",
                            "com.codacy" %% "codacy-engine-scala-seed" % "3.1.0")

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

val installAll =
  s"""apk add --no-cache bash ca-certificates build-base ruby ruby-bundler ruby-dev
     |&& echo 'gem: --no-document' > /etc/gemrc
     |&& cd /opt/docker/setup
     |&& bundle install
     |&& gem cleanup
     |&& apk del build-base ruby-bundler ruby-dev
     |&& rm -rf /opt/docker/setup /tmp/* /var/cache/apk/*""".stripMargin
    .replaceAll(System.lineSeparator(), " ")

mappings.in(Universal) ++= resourceDirectory
  .in(Compile)
  .map { resourceDir: File =>
    val src = resourceDir / "docs"
    val dest = "/docs"

    val docFiles = for {
      path <- src.allPaths.get if !path.isDirectory
    } yield path -> path.toString.replaceFirst(src.toString, dest)

    val rubyFiles = Seq((file("Gemfile"), "/setup/Gemfile"),
                        (file("Gemfile.lock"), "/setup/Gemfile.lock"),
                        (file(".ruby-version"), "/setup/.ruby-version"),
                        (file(".brakeman-version"), "/setup/.brakeman-version"))

    docFiles ++ rubyFiles
  }
  .value

val dockerUser = "docker"
val dockerGroup = "docker"

daemonUser in Docker := dockerUser

daemonGroup in Docker := dockerGroup

dockerBaseImage := "openjdk:8-jre-alpine"

dockerCommands := {
  dockerCommands.dependsOn(toolVersion).value.flatMap {
    case cmd @ (Cmd("ADD", _)) =>
      List(Cmd("RUN", s"adduser -u 2004 -D $dockerUser"),
           cmd,
           Cmd("RUN", "mv /opt/docker/docs /docs"),
           Cmd("RUN", installAll))
    case other => List(other)
  }
}
