import facades.d3.ImplicitAddons._
import facades.d3.{Slider, Viz, d3plus}
import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.html.Input
import org.scalajs.dom.raw.Event
import org.singlespaced.d3js.d3
import shared._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.util.{Failure, Success}

object MainScalaJS extends js.JSApp {

  var socioEconomic: CaitMap = null

  def main(): Unit = {

    implicit val slider: Slider = d3.slider().axis(true).step(1)
    implicit val weightByPopulationSwitch: Input =
      dom.document.getElementById("weightByPopulation").asInstanceOf[Input]

    Ajax.get("/socio").onComplete {

      case Success(xhr) =>
        socioEconomic = parseCait(xhr.responseText)

      case Failure(e) =>
        dom.window.alert("Failed to load socio economic data : " + e.getMessage)

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
          (implicit slider: Slider, weightByPopulationSwitch: Input): Unit = {

    implicit val caitMap: CaitMap = parseCait(ajaxResponseText)

    initSlider("#yearSlider", gasTreeMap, sourceTreeMap, geoMap)

    yearChangeHandler(gasTreeMap, sourceTreeMap, geoMap: Viz, selectedYear)

    val weightByPopulationChangeFunction = (event: Event) => {
      drawGeoMap(caitMap(selectedYear.toString), geoMap)(weightByPopulationSwitch, selectedYear)
    }

    dom.window.addEventListener("change", weightByPopulationChangeFunction, useCapture = true)

  }


  def initSlider(domSelector: String, gasTreeMap: Viz, sourceTreeMap: Viz, geoMap: Viz)
                (implicit caitMap: CaitMap, slider: Slider, weightByPopulationSwitch: Input) = {

    paramateriseSlider

    slider.on("slideend", yearChangeHandler(gasTreeMap, sourceTreeMap, geoMap))

    val drawSliderFunction = () => {
      d3.select(domSelector).append("div").call(slider)
    }

    var sliderContainer = drawSliderFunction()

    val sliderResizeFunction = (event: Event) => {
      sliderContainer.remove()
      sliderContainer = drawSliderFunction()
    }

    dom.window.addEventListener("resize", sliderResizeFunction, useCapture = true)

  }


  def paramateriseSlider(implicit caitMap: CaitMap, slider: Slider) = {

    slider.min(caitMap.keySet.min.toInt)

    val max: Int = caitMap.keySet.max.toInt
    slider.max(max)
    slider.value(max)

  }

  def selectedYear(implicit slider: Slider): Int = slider.value()


  def initTreeMap(domSelector: String): Viz = d3plus.viz()
    .`type`("tree_map")
    .container(domSelector)
    .resize(true)
    .id(NAME)
    .size(VALUE)


  def yearChangeHandler(gasTreeMap: Viz, sourceTreeMap: Viz, geoMap: Viz)
                       (implicit caitMap: CaitMap, weightByPopulationSwitch: Input): (Event, Int) => Unit =
    (event: Event, selectedYear: Int) => yearChangeHandler(gasTreeMap, sourceTreeMap, geoMap, selectedYear)


  def yearChangeHandler(gasTreeMap: Viz, sourceTreeMap: Viz, geoMap: Viz, selectedYear: Int)
                       (implicit caitMap: CaitMap, weightByPopulationSwitch: Input): Unit = {

    caitMap.get(selectedYear.toString) match {

      case Some(yearDetail) =>
        drawTreeMap(yearDetail, GASES, gasTreeMap, CO2, N2O, CH4)
        drawTreeMap(yearDetail, SOURCE, sourceTreeMap, ENERGY, TRANSPORT, AGRICULTURE, INDUSTRIAL, WASTE, LAND_USE_CHANGE)
        drawGeoMap(yearDetail, geoMap)(weightByPopulationSwitch, selectedYear)

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


  def drawGeoMap(yearDetail: CaitYearDetail, geoMap: Viz)(implicit weightByPopulationSwitch: Input, selectedYear: Int): Unit = {

    implicit val weightByPopulation: Boolean = weightByPopulationSwitch.checked

    geoMap.data(
      yearDetail.filterKeys(filterCountriesMissingPopulationIfApplicable).map(countryToSum).toJSArray
    ).draw()

  }


  def filterCountriesMissingPopulationIfApplicable(implicit weightByPopulation: Boolean, selectedYear: Int) =
    (caitCountry: String) =>
      !weightByPopulation || socioEconomic(selectedYear.toString)(caitCountry)(POPULATION)(POPULATION) > 0


  def initGeoMap(domSelector: String): Viz = d3plus.viz()
    .`type`("geo_map")
    .container(domSelector)
    .resize(true)
    .coords(
      js.Dictionary(
        MUTE -> "anata", // hides Antarctica
        VALUE -> "/assets/js/vendor/countries.json"
      )
    )
    .id(ID)
    .text(NAME)
    .color(
      js.Dictionary(
        HEATMAP -> js.Array("#FFEE8D", "#B22200"),
        VALUE -> VALUE
      )
    )
    .tooltip(VALUE)


  def countryToSum(implicit weightByPopulation: Boolean, selectedYear: Int) = (keyValue: (String, CaitYearCountryDetail)) => {

    val caitCountry = keyValue._1

    val caitYearCountryDetail = keyValue._2

    js.Dictionary(
      NAME -> caitCountry,
      ID -> CaitCountryToAlpha5(caitCountry),
      VALUE -> weightByPopulationIfApplicable(
        caitCountry,
        quashNegatives(
          caitYearCountryDetail(GASES).foldLeft(0.0)(countrySumFunction)
        )
      )
    )

  }


  def quashNegatives(potentiallyNegative: Double): Double = if (potentiallyNegative > 0.0) potentiallyNegative else 0.0


  def countrySumFunction = (runningTotal: Double, keyValue: (String, Double)) =>
    runningTotal + keyValue._2


  def weightByPopulationIfApplicable(caitCountry: String, rawCountryTotal: Double)
                                    (implicit weightByPopulation: Boolean, selectedYear: Int): Double = {

    if (weightByPopulation) // 1,000,000 is to convert metric tons to grams
      1000000 * rawCountryTotal / socioEconomic(selectedYear.toString)(caitCountry)(POPULATION)(POPULATION)
    else
      rawCountryTotal

  }

}