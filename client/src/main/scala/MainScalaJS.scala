import facades.d3.ImplicitAddons._
import facades.d3.{Slider, Viz, d3plus}
import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.Event
import org.singlespaced.d3js.d3
import shared._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.util.{Failure, Success}

object MainScalaJS extends js.JSApp {

  def main(): Unit = {

    implicit val slider: Slider = d3.slider().axis(true).step(1)
    implicit val gasTreeMap: Viz = d3plus.viz()
      .`type`("tree_map")
      .container("#gasTreemap")
      .resize(true)
      .id(NAME)
      .size(VALUE)

    Ajax.get("/emissions").onComplete {
      case Success(xhr) => init(xhr.responseText)
      case Failure(e) => dom.window.alert("Failed to load emissions data : " + e.getMessage)
    }

  }

  def init(ajaxResponseText: String)(implicit slider: Slider, gasTreeMap: Viz): Unit = {

    implicit val parsed: CaitMap = parseCait(ajaxResponseText)

    val selectedYear = paramateriseSlider

    slider.on("slide", yearChangeHandler)

    drawGasesTreeMap(parsed(selectedYear.toString), gasTreeMap)

  }

  def yearChangeHandler(implicit caitMap: CaitMap, gasTreeMap: Viz): (Event, Int) => Unit = (event: Event, value: Int) => {

    caitMap.get(value.toString) match {

      case Some(yearDetail) =>
        drawGasesTreeMap(yearDetail, gasTreeMap)

      case None => //TODO clear data

    }

  }

  def drawGasesTreeMap(implicit yearDetail: CaitYearDetail, gasTreeMap: Viz): Unit = {

    val data = js.Array(
      sumIntoTreeMapEntry(CO2),
      sumIntoTreeMapEntry(CH4),
      sumIntoTreeMapEntry(N2O)
    )

    println(data)

    gasTreeMap.data(data).draw()

  }

  def sumIntoTreeMapEntry(gas: String)(implicit yearDetail: CaitYearDetail) =
    js.Dictionary(NAME -> gas, VALUE -> yearDetail.foldLeft(0.0)(gasSumFunction(gas)))

  def gasSumFunction(gas: String) = (runningTotal: Double, keyValue: (String, CaitYearCountryDetail)) =>
    runningTotal + keyValue._2(GASES)(gas)

  def paramateriseSlider(implicit caitMap: CaitMap, slider: Slider): Int = {

    slider.min(caitMap.keySet.min.toInt)

    val max: Int = caitMap.keySet.max.toInt
    slider.max(max)
    slider.value(max)

    d3.select("#yearSlider").call(slider)

    max //this is now the selected value

  }

}