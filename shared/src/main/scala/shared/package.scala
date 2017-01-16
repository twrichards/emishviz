import upickle.{json, default}

package object shared {

  type CaitMap = Map[String, CaitYearDetail]
  type CaitYearDetail = Map[String, CaitYearCountryDetail]
  type CaitYearCountryDetail = Map[String, Map[String, Double]]

  def stringifyCait(caitMap: CaitMap): String =
    upickle.json.write(upickle.default.writeJs[CaitMap](caitMap))

  def stringifyCaitYearDetail(yearDetail: CaitYearDetail): String =
    upickle.json.write(upickle.default.writeJs[CaitYearDetail](yearDetail), indent = 4)

  def parseCait(unparsedJSON: String): CaitMap =
    default.readJs[CaitMap](json.read(unparsedJSON))

  val NAME = "name"
  val VALUE = "value"

  val GASES = "gases"
  val CO2 = "co2"
  val CH4 = "ch4"
  val N2O = "n2o"

  val SOURCE = "source"
  val ENERGY = "energy"
  val TRANSPORT = "transport"
  val AGRICULTURE = "agriculture"
  val INDUSTRIAL = "industrial"
  val WASTE = "waste"
  val LAND_USE_CHANGE = "land use change"

  val POPULATION = "population"
  val GDP = "gdp"
  val PPP = "ppp"
  val USD = "usd"

}
