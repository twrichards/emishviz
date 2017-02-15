package controllers

import java.io.File

import models.{GhgEmissions, SocioEconomic}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Result, Results}
import play.api.test.Helpers._
import play.api.test._
import play.{Environment, Mode}

import scala.concurrent.Future

class DataControllerSpec extends PlaySpec with Results  {

  val unit:DataController = new DataController(
    new GhgEmissions(getClass.getResourceAsStream("/ghgSample.csv")),
    new SocioEconomic(getClass.getResourceAsStream("/socioSample.csv"))
  )

  "DataController" should {

    "provide green house gas data in json form" in {

      val resultFuture:Future[Result] = unit.emissions().apply(FakeRequest())

      val result:JsValue = contentAsJson(resultFuture)

      result mustBe Json.parse(
        """
          |{
          |   "1991":{
          |      "Albania":{
          |         "gases":{
          |            "CO₂":4.798968,
          |            "CH₄":3.701397696,
          |            "N₂O":1.071802109
          |         },
          |         "source":{
          |            "energy":5.363099999999999,
          |            "industrial":0.3151,
          |            "agriculture":2.6627,
          |            "transport":0.53,
          |            "waste":0.5587,
          |            "land use change":0.1461
          |         }
          |      }
          |   },
          |   "1990":{
          |      "Afghanistan":{
          |         "gases":{
          |            "CO₂":3.654722,
          |            "CH₄":9.309910807,
          |            "N₂O":2.99436272
          |         },
          |         "source":{
          |            "energy":0,
          |            "industrial":0.0571,
          |            "agriculture":7.3512,
          |            "transport":0,
          |            "waste":4.039,
          |            "land use change":0.6643
          |         }
          |      }
          |   }
          |}
        """.stripMargin
      )

    }

    "provide socio economic data in json form" in {

      val resultFuture:Future[Result] = unit.socio().apply(FakeRequest())

      val result:JsValue = contentAsJson(resultFuture)

      result mustBe Json.parse(
        """
          |{
          |   "1991":{
          |      "Albania":{
          |         "population":{
          |            "population":3266790
          |         },
          |         "gdp":{
          |            "ppp":10066,
          |            "usd":3957
          |         }
          |      }
          |   },
          |   "1990":{
          |      "Afghanistan":{
          |         "population":{
          |            "population":11731193
          |         },
          |         "gdp":{
          |            "ppp":0,
          |            "usd":0
          |         }
          |      }
          |   }
          |}
        """.stripMargin
      )

    }

    "support loading data from the Environment" in {

      val fakeEnvironment:Environment = new Environment(new File("/"), getClass.getClassLoader, Mode.TEST)
      val unitUsingFakeEnvironment:DataController = new DataController(
        new GhgEmissions(fakeEnvironment),
        new SocioEconomic(fakeEnvironment)
      )

      contentAsJson(
        unitUsingFakeEnvironment.emissions().apply(FakeRequest())
      )

      contentAsJson(
        unitUsingFakeEnvironment.socio().apply(FakeRequest())
      )

    }

  }

}
