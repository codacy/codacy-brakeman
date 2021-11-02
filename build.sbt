name := "codacy-brakeman"

scalaVersion := "2.13.6"

libraryDependencies ++= Seq("com.typesafe.play" %% "play-json" % "2.8.1",
                            "com.codacy" %% "codacy-engine-scala-seed" % "5.0.1")

enablePlugins(JavaAppPackaging)
