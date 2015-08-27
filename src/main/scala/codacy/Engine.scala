package codacy

import codacy.dockerApi.DockerEngine
import codacy.brakeman.Brakeman

object Engine extends DockerEngine(Brakeman)