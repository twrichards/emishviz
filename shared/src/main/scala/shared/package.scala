import upickle.{json, default}

package object shared {

  type CaitMap = Map[String, CaitYearDetail]
  private type CaitYearDetail = Map[String, CaitYearCountryDetail]
  type CaitYearCountryDetail = Map[String, Map[String, Double]]

  def stringifyCait(caitMap: CaitMap): String =
    upickle.json.write(upickle.default.writeJs[CaitMap](caitMap))

  def stringifyCaitYearDetail(yearDetail: CaitYearDetail): String =
    upickle.json.write(upickle.default.writeJs[CaitYearDetail](yearDetail), indent = 4)

  def parseCait(unparsedJSON: String): CaitMap =
    default.readJs[CaitMap](json.read(unparsedJSON))

}
