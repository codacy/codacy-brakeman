package codacy.brakeman

import java.nio.file.{Paths, Files, Path}
import codacy.dockerApi._
import play.api.libs.json._
import scala.sys.process._
import scala.util.{ Success, Properties, Try}

object Brakeman extends Tool {


  override def apply(path: Path, conf: Option[Seq[PatternDef]], files: Option[Set[Path]])(implicit spec: Spec): Try[Iterable[Result]] = {
    //Use this method to make sure results are for selected files only
    def isEnabled(result: Result) = conf.map(_.exists(_.patternId == result.patternId)).getOrElse(true)

    val command = getCommandFor(path, conf, files)

    val resultFromTool = command.lineStream(ProcessLogger(_ => ()))

    val results = parseToolResult(resultFromTool)

    Try(results)
  }

  def parseToolResult(resultFromTool: Stream[String]): Iterable[Result] = {

    lazy val ErrorPattern = """(.+):\s*([0-9]+)\s*::(.+)""".r

    val jsonResult = Json.parse(resultFromTool.mkString)

    val errors = (jsonResult \ "errors").asOpt[JsArray]
      .fold(Seq[JsValue]())(arr => arr.value)
      .map(err => err \ "error")
      .map(err => err.asOpt[String]
      .getOrElse(""))

    errors.foreach{
      case ErrorPattern(filename, line, msg) =>
        println(s"TODO: Error at $filename;\n  line $line;\n  msg = $msg")
      case err => println(s"TODO: Generic error => $err")
    }

    val warnings = (jsonResult \ "warnings").asOpt[JsArray].fold(Seq[JsValue]())(arr => arr.value)

    println("\n\n\nJSON Result Warning\n\n\n" + warnings.mkString("\n") + "\n\n\nEndResults\n\n\n")

    //Dummy Results
    val dummyRes = Result(SourcePath("XPTO.file"), ResultMessage("XPTO.message"), PatternId("XPTO.id"), ResultLine(42))
    val dummyRes2 = Result(SourcePath("XPTO2.file"), ResultMessage("XPTO2.message"), PatternId("XPTO2.id"), ResultLine(24))
    Seq(dummyRes, dummyRes2)
  }


  private[this] implicit lazy val writer = Json.reads[Result]

  private[this] def parseLine(line: String) = Try(Json.parse(line)).toOption.flatMap(_.asOpt[Result])

  //Probably will use, remember that tool results come without the /src
  private[this] def toRelativePath(rootDirectory: Path, path: String): Option[SourcePath] = {
    val absolutePath = Paths.get(path)
    Try(rootDirectory.relativize(absolutePath)).map { case relativePaths => SourcePath(relativePaths.toString) }.toOption
  }


  private[this] def getCommandFor(path: Path, conf: Option[Seq[PatternDef]], files: Option[Set[Path]])(implicit spec: Spec): Seq[String] = {

    val filesToTest = files.filter(paths => paths.nonEmpty).fold(Seq[String]()) {
      paths => Seq("--only-files") ++ paths.map(p => p.toString)
    }

    val patternsToTest = conf.filter(patterns => patterns.nonEmpty).fold(Seq[String]()) {
      patterns =>
        val patternsIds = patterns.map(p => p.patternId.toString)
        Seq("-t") ++ patternsIds
    }

    Seq("brakeman", path.toString) ++ filesToTest ++ Seq( "-f", "json") ++ patternsToTest
  }
}

case class BrakemanParserException(message: String) extends Exception(message)


