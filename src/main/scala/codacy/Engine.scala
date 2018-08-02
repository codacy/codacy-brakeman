package codacy

import codacy.brakeman.Brakeman
import com.codacy.tools.scala.seed.DockerEngine

object Engine extends DockerEngine(Brakeman)()
