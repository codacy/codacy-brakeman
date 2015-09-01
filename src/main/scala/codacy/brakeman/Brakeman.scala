package codacy.brakeman

import java.nio.file.{Paths, Files, Path}
import codacy.dockerApi._
import play.api.libs.json._
import scala.sys.process._
import scala.util.{ Success, Properties, Try}

import play.api.libs.functional.syntax._

case class WarnResult(warningCode: Int, message: String, file: String, line: Int)

object  WarnResult {
  implicit val warnReads = (
    (__ \ "warning_code").read[Int] and
      (__ \ "message").read[String] and
      (__ \ "file").read[String] and
      (__ \ "line").read[Int]
    )(WarnResult.apply _)
}

object Brakeman extends Tool {


  override def apply(path: Path, conf: Option[Seq[PatternDef]], files: Option[Set[Path]])(implicit spec: Spec): Try[Iterable[Result]] = {
    //Use this method to make sure results are for selected files only
    def isEnabled(result: Result) = conf.map(_.exists(_.patternId == result.patternId)).getOrElse(true)

    val command = getCommandFor(path, conf, files)

    val resultFromTool = command.lineStream(ProcessLogger(_ => ()))

    val results = parseToolResult(resultFromTool)

    Try(results)
  }

  def warningToPatternId(warningCode: Int): String = {
    warningCode match {
      case 0 => "SQL"

      case _ => "Unknow Error"
    }
  }

  def warningToResult(warn: JsValue): Option[Result] = {

    warn.asOpt[WarnResult].map{
      res =>
        val source = SourcePath(res.file)
        val resultMessage = ResultMessage(res.message)
        val patternId = PatternId(warningToPatternId(res.warningCode))
        val line = ResultLine(res.line)

        Result(source, resultMessage, patternId, line)
    }

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

    warnings.flatMap(warningToResult)
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


