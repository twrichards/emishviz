package models

import org.scalatestplus.play._
import shared._

class GhgEmissionsSpec extends PlaySpec {

  val unit:GhgEmissions = new GhgEmissions(getClass.getResourceAsStream("/ghgSample.csv"))
  val data:CaitMap = parseCait(unit.json)

  "A GhgEmissions" must {

    "extract specifics details of green house gases" in {

      data must have size 2 // doesn't include 'world' and 'eu' entries

      data.keySet mustBe Set("1990", "1991")

      data("1991").keySet mustBe Set("Albania")

      data("1991")("Albania").keySet mustBe Set(GASES, SOURCE)

      data("1991")("Albania")(GASES).keySet mustBe Set(CO2, CH4, N2O)

      data("1991")("Albania")(GASES)(CO2) mustBe 4.798968
      data("1991")("Albania")(GASES)(CH4) mustBe 3.701397696
      data("1991")("Albania")(GASES)(N2O) mustBe 1.071802109

      data("1991")("Albania")(SOURCE).keySet mustBe
        Set(ENERGY, TRANSPORT, AGRICULTURE, INDUSTRIAL, WASTE, LAND_USE_CHANGE)

      data("1991")("Albania")(SOURCE)(ENERGY) mustBe 5.8931 - 0.53
      data("1991")("Albania")(SOURCE)(TRANSPORT) mustBe 0.53 + 0
      data("1991")("Albania")(SOURCE)(AGRICULTURE) mustBe 2.6627
      data("1991")("Albania")(SOURCE)(INDUSTRIAL) mustBe 0.3151
      data("1991")("Albania")(SOURCE)(WASTE) mustBe 0.5587
      data("1991")("Albania")(SOURCE)(LAND_USE_CHANGE) mustBe 0.1461

    }

  }

}
