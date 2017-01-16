import facades.d3.ImplicitAddons._
import facades.d3.{Slider, Viz, d3plus}
import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.raw.Event
import org.singlespaced.d3js.d3
import shared._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.util.{Failure, Success}

object MainScalaJS extends js.JSApp {

  def main(): Unit = {

    implicit val slider: Slider = d3.slider().axis(true).step(1)

    Ajax.get("/emissions").onComplete {
      case Success(xhr) => init(
        xhr.responseText,
        initTreeMap("gasTreeMap"),
        initTreeMap("sourcesTreeMap")
      )
      case Failure(e) => dom.window.alert("Failed to load emissions data : " + e.getMessage)
    }

  }


  def init(ajaxResponseText: String, gasTreeMap: Viz, sourceTreeMap: Viz)(implicit slider: Slider): Unit = {

    implicit val parsed: CaitMap = parseCait(ajaxResponseText)

    val startingYear = paramateriseSlider

    yearChangeHandler(gasTreeMap, sourceTreeMap, startingYear)

    slider.on("slide", yearChangeHandler(gasTreeMap, sourceTreeMap))

  }


  def initTreeMap(domID: String): Viz = d3plus.viz()
    .`type`("tree_map")
    .container("#" + domID)
    .resize(true)
    .id(NAME)
    .size(VALUE)


  def yearChangeHandler(gasTreeMap: Viz, sourceTreeMap: Viz)(implicit caitMap: CaitMap): (Event, Int) => Unit =
    (event: Event, selectedYear: Int) => yearChangeHandler(gasTreeMap, sourceTreeMap, selectedYear)


  def yearChangeHandler(gasTreeMap: Viz, sourceTreeMap: Viz, selectedYear: Int)(implicit caitMap: CaitMap): Unit = {

    caitMap.get(selectedYear.toString) match {

      case Some(yearDetail) =>
        drawTreeMap(yearDetail, GASES, gasTreeMap, CO2, N2O, CH4)
        drawTreeMap(yearDetail, SOURCE, sourceTreeMap, ENERGY, TRANSPORT, AGRICULTURE, INDUSTRIAL, WASTE, LAND_USE_CHANGE)

      case None => //TODO clear data

    }

  }


  def drawTreeMap(implicit yearDetail: CaitYearDetail, section: String, treeMap: Viz, keys: String*): Unit = {

    treeMap.data(
      keys.map((key: String) => keyToSum(section, key)).toJSArray
    ).draw()

  }


  def keyToSum(section: String, key: String)(implicit yearDetail: CaitYearDetail) =
    js.Dictionary(NAME -> key, VALUE -> yearDetail.foldLeft(0.0)(sumFunction(section, key)))


  def sumFunction(section: String, key: String) = (runningTotal: Double, keyValue: (String, CaitYearCountryDetail)) =>
    runningTotal + keyValue._2(section)(key)


  def paramateriseSlider(implicit caitMap: CaitMap, slider: Slider): Int = {

    slider.min(caitMap.keySet.min.toInt)

    val max: Int = caitMap.keySet.max.toInt
    slider.max(max)
    slider.value(max)

    d3.select("#yearSlider").call(slider)

    max //this is now the selected value

  }

}