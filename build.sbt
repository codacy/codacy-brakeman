import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}

import scala.io.Source
import scala.util.parsing.json.JSON

name := """codacy-engine-brakeman"""

version := "1.0.0-SNAPSHOT"

val languageVersion = "2.11.12"

scalaVersion := languageVersion

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.4.8",
  "com.codacy" %% "codacy-engine-scala-seed" % "2.7.10"
)

enablePlugins(JavaAppPackaging)

enablePlugins(DockerPlugin)

version in Docker := "1.0.0-SNAPSHOT"

organization := "com.codacy"

lazy val toolVersion = TaskKey[String]("toolVersion", "Retrieve the version of the underlying tool from patterns.json")

toolVersion := {
  val jsonFile = (resourceDirectory in Compile).value / "docs" / "patterns.json"
  val toolMap = JSON.parseFull(Source.fromFile(jsonFile).getLines().mkString)
    .getOrElse(throw new Exception("patterns.json is not a valid json"))
    .asInstanceOf[Map[String, String]]
  toolMap.getOrElse[String]("version", throw new Exception("Failed to retrieve 'version' from patterns.json"))
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

mappings in Universal ++= {
  (resourceDirectory in Compile) map { (resourceDir: File) =>
    val src = resourceDir / "docs"
    val dest = "/docs"

    val docFiles = for {
      path <- src.***.get if !path.isDirectory
    } yield path -> path.toString.replaceFirst(src.toString, dest)

    val rubyFiles = Seq(
      (file("Gemfile"), "/setup/Gemfile"),
      (file("Gemfile.lock"), "/setup/Gemfile.lock"),
      (file(".ruby-version"), "/setup/.ruby-version"),
      (file(".brakeman-version"), "/setup/.brakeman-version")
    )

    docFiles ++ rubyFiles
  }
}.value


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
