package codacy.brakeman

import play.api.libs.json.Json

private[brakeman] case class PylintResult(message:String,obj:String,column:Int,path:String,line:Int,`type`:String,symbol: String, module:String)
private[brakeman] object PylintResult{ implicit lazy val reads = Json.reads[PylintResult]}
