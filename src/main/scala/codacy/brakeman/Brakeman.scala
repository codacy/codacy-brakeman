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
    def isEnabled(result: Result) = conf.map(_.exists(_.patternId == result.patternId)).getOrElse(true)

    val command = getCommandFor(path, conf, files)

    val resultFromTool = command.lineStream(ProcessLogger(_ => ()))

    val results = parseToolResult(resultFromTool)

    Try(results.filter(isEnabled))
  }

  def warningToPatternId(warningCode: Int): String = {
    warningCode match {
      case 0 => "SQL"
      case 1 => "SQL"
      case 2 => "CrossSiteScripting"
      case 6 => "ForgerySetting"
      case 7 => "ForgerySetting"
      case 8 => "SkipBeforeFilter"
      case 9 => "BasicAuth"
      case 10 => "SkipBeforeFilter"
      case 11 => "DefaultRoutes"
      case 12 => "DefaultRoutes"
      case 13 => "Evaluation"
      case 14 => "Execute"
      case 15 => "Render"
      case 16 => "FileAccess"
      case 17 => "MassAssignment"
      case 18 => "Redirect"
      case 19 => "ModelAttributes"
      case 22 => "SelectVulnerability"
      case 23 => "Send"
      case 24 => "UnsafeReflection"
      case 25 => "Deserialize"
      case 26 => "SessionSettings"
      case 27 => "SessionSettings"
      case 28 => "TranslateBug"
      case 29 => "SessionSettings"
      case 30 => "ValidationRegex"
      case 31 => "NestedAttributes"
      case 32 => "MailTo"
      case 33 => "ForgerySetting"
      case 34 => "FilterSkipping"
      case 35 => "QuoteTableName"
      case 36 => "StripTags"
      case 37 => "ResponseSplitting"
      case 42 => "DigestDoS"
      case 43 => "SelectTag"
      case 44 => "SingleQuotes"
      case 45 => "StripTags"
      case 48 => "YAMLParsing"
      case 49 => "JSONParsing"
      case 50 => "ModelSerialize"
      case 52 => "JSONParsing"
      case 54 => "WithoutProtection"
      case 55 => "SymbolDoSCVE"
      case 57 => "JRubyXML"
      case 59 => "SymbolDoS"
      case 60 => "ModelAttrAccessible"
      case 61 => "DetailedExceptions"
      case 62 => "DetailedExceptions"
      case 63 => "I18nXSS"
      case 64 => "HeaderDoS"
      case 70 => "MassAssignment"
      case 71 => "SSLVerify"
      case 72 => "SQLCVEs"
      case 73 => "NumberToCurrency"
      case 74 => "NumberToCurrency"
      case 75 => "RenderDoS"
      case 76 => "RegexDoS"
      case 77 => "DefaultRoutes"
      case 80 => "CreateWith"
      case 81 => "CreateWith"
      case 82 => "UnscopedFind"
      case 83 => "EscapeFunction"
      case 85 => "FileDisclosure"
      case 86 => "ForgerySetting"
      case 87 => "JSONEncoding"
      case 88 => "XMLDoS"
      case _ => "UnknowError"
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

  private[this] def getCommandFor(path: Path, conf: Option[Seq[PatternDef]], files: Option[Set[Path]])(implicit spec: Spec): Seq[String] = {

    val filesToTest = files.filter(paths => paths.nonEmpty).fold(Seq[String]()) {
      paths => Seq("--only-files", paths.mkString(","))
    }

    val patternsToTest = conf.filter(patterns => patterns.nonEmpty).fold(Seq[String]()) {
      patterns =>
        val patternsIds = patterns.map(p => p.patternId.toString)
        Seq("-t", patternsIds.mkString(","))
    }

    Seq("brakeman", path.toString) ++ filesToTest ++ Seq( "-f", "json") ++ patternsToTest
  }
}

case class BrakemanParserException(message: String) extends Exception(message)