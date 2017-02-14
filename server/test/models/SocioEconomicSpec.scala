package models

import org.scalatestplus.play._
import shared._

class SocioEconomicSpec extends PlaySpec {

  val unit:SocioEconomic = new SocioEconomic(getClass.getResourceAsStream("/socioSample.csv"))
  val data:CaitMap = parseCait(unit.json)

  "A SocioEconomic" must {

    "extract specifics details of socio economic factors" in {

      data must have size 2 // doesn't include 'world' and 'eu' entries

      data.keySet mustBe Set("1990", "1991")

      data("1991").keySet mustBe Set("Albania")

      data("1991")("Albania").keySet mustBe Set(POPULATION, GDP)

      data("1991")("Albania")(POPULATION).keySet mustBe Set(POPULATION)

      data("1991")("Albania")(POPULATION)(POPULATION) mustBe 3266790

      data("1991")("Albania")(GDP).keySet mustBe Set(PPP, USD)

      data("1991")("Albania")(GDP)(PPP) mustBe 10066
      data("1991")("Albania")(GDP)(USD) mustBe 3957

    }

  }

}
