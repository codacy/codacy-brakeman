import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}

name := "codacy-engine-brakeman"

version := "1.0.0-SNAPSHOT"

val languageVersion = "2.12.6"

scalaVersion := languageVersion

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "com.codacy" %% "codacy-engine-scala-seed" % "3.0.183"
)

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

    val docFiles = (for {
      path <- better.files.File(src.toPath).listRecursively()
      if !path.isDirectory
    } yield path.toJava -> path.toString.replaceFirst(src.toString, dest)).toSeq

    val rubyFiles = Seq(
      (file("Gemfile"), "/setup/Gemfile"),
      (file("Gemfile.lock"), "/setup/Gemfile.lock"),
      (file(".ruby-version"), "/setup/.ruby-version"),
      (file(".brakeman-version"), "/setup/.brakeman-version")
    )

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
    case cmd@(Cmd("ADD", _)) => List(
      Cmd("RUN", s"adduser -u 2004 -D $dockerUser"),
      cmd,
      Cmd("RUN", "mv /opt/docker/docs /docs"),
      Cmd("RUN", installAll)
    )
    case other => List(other)
  }
}
