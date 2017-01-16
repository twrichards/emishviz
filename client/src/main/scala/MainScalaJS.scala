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

  var isoMapping: Map[String, String] = null

  def main(): Unit = {

    implicit val slider: Slider = d3.slider().axis(true).step(1)

    Ajax.get("/assets/js/vendor/ISO-3166.json").onComplete {

      case Success(xhr) =>
        isoMapping = parseISO3166(xhr.responseText) map (entry => entry(NAME) -> entry(COUNTRY_CODE)) toMap

      case Failure(e) =>
        dom.window.alert("Failed to load emissions data : " + e.getMessage)

    }

    Ajax.get("/emissions").onComplete {

      case Success(xhr) => init(
        xhr.responseText,
        initTreeMap("#gasTreeMap"),
        initTreeMap("#sourcesTreeMap"),
        initGeoMap("#mapArea")
      )

      case Failure(e) =>
        dom.window.alert("Failed to load emissions data : " + e.getMessage)

    }

  }


  def init(ajaxResponseText: String, gasTreeMap: Viz, sourceTreeMap: Viz, geoMap: Viz)
          (implicit slider: Slider): Unit = {

    implicit val parsed: CaitMap = parseCait(ajaxResponseText)

    val startingYear = initSlider("#yearSlider", gasTreeMap, sourceTreeMap, geoMap)

    yearChangeHandler(gasTreeMap, sourceTreeMap, geoMap: Viz, startingYear)

  }


  def initSlider(domSelector: String, gasTreeMap: Viz, sourceTreeMap: Viz, geoMap: Viz)
                (implicit caitMap: CaitMap, slider: Slider): Int = {

    val startingYear = paramateriseSlider

    slider.on("slide", yearChangeHandler(gasTreeMap, sourceTreeMap, geoMap))

    val drawSliderFunction = () => {
      d3.select(domSelector).append("div").call(slider)
    }

    var sliderContainer = drawSliderFunction()

    val sliderResizeFunction = (event: Event) => {
      sliderContainer.remove()
      sliderContainer = drawSliderFunction()
    }

    dom.window.addEventListener("resize", sliderResizeFunction, useCapture = true)

    startingYear

  }


  def paramateriseSlider(implicit caitMap: CaitMap, slider: Slider): Int = {

    slider.min(caitMap.keySet.min.toInt)

    val max: Int = caitMap.keySet.max.toInt
    slider.max(max)
    slider.value(max)

    max

  }


  def initTreeMap(domSelector: String): Viz = d3plus.viz()
    .`type`("tree_map")
    .container(domSelector)
    .resize(true)
    .id(NAME)
    .size(VALUE)


  def yearChangeHandler(gasTreeMap: Viz, sourceTreeMap: Viz, geoMap: Viz)
                       (implicit caitMap: CaitMap): (Event, Int) => Unit =
    (event: Event, selectedYear: Int) => yearChangeHandler(gasTreeMap, sourceTreeMap, geoMap, selectedYear)


  def yearChangeHandler(gasTreeMap: Viz, sourceTreeMap: Viz, geoMap: Viz, selectedYear: Int)
                       (implicit caitMap: CaitMap): Unit = {

    caitMap.get(selectedYear.toString) match {

      case Some(yearDetail) =>
        drawTreeMap(yearDetail, GASES, gasTreeMap, CO2, N2O, CH4)
        drawTreeMap(yearDetail, SOURCE, sourceTreeMap, ENERGY, TRANSPORT, AGRICULTURE, INDUSTRIAL, WASTE, LAND_USE_CHANGE)
        drawGeoMap(yearDetail, geoMap)

      case None => //TODO clear data

    }

  }


  def drawTreeMap(implicit yearDetail: CaitYearDetail, section: String, treeMap: Viz, keys: String*): Unit = {

    treeMap.data(
      keys.map(
        (key: String) => keyToSum(section, key)
      ).toJSArray
    ).draw()

  }

  def keyToSum(section: String, key: String)(implicit yearDetail: CaitYearDetail) =
    js.Dictionary(
      NAME -> key,
      VALUE -> yearDetail.foldLeft(0.0)(specificSumFunction(section, key))
    )


  def specificSumFunction(section: String, key: String) = (runningTotal: Double, keyValue: (String, CaitYearCountryDetail)) =>
    runningTotal + keyValue._2(section)(key)


  def drawGeoMap(implicit yearDetail: CaitYearDetail, geoMap: Viz): Unit = {

    geoMap.data(
      yearDetail.map(countryToSum).toJSArray
    ).draw()

  }


  def initGeoMap(domSelector: String): Viz = d3plus.viz()
    .`type`("geo_map")
    .container(domSelector)
    .resize(true)
    .coords(
      js.Dictionary(
        "mute" -> "010", // hides Antarctica
        "value" -> "/assets/js/vendor/world-50m.v1.json"
      )
    )
    .id(ID)
    .text(NAME)
    .color(VALUE)
    .tooltip(VALUE)


  def countryToSum = (keyValue: (String, CaitYearCountryDetail)) =>
    js.Dictionary(
      NAME -> keyValue._1,
      ID -> mapCountryToISO3166(keyValue._1), //TODO move mapping to CSV load (none AJAX)
      VALUE -> keyValue._2(GASES).foldLeft(0.0)(countrySumFunction)
    )


  def countrySumFunction = (runningTotal: Double, keyValue: (String, Double)) =>
    runningTotal + keyValue._2


  def mapCountryToISO3166(country: String): String = isoMapping get country match {

    case Some(matching) => matching

    case None => isoMapping find (_._1.contains(country)) match {

      case Some(partialMatch) => {
        println(country + "=" + partialMatch._1)
        partialMatch._2
      }

      case None => {
        js.Dynamic.global.console.error(country)
        null
      }

    }

  }

}