import facades.d3.ImplicitAddons._
import facades.d3.{Slider, Viz, d3plus}
import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.html.{Input, Span}
import org.scalajs.dom.raw.Event
import org.singlespaced.d3js.d3
import shared._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.util.{Failure, Success}

object MainScalaJS extends js.JSApp {

  var socioEconomic: CaitMap = null

  var selectedCountryName:String = null;

  type DrawerFunction = (CaitYearDetail) => Unit

  def main(): Unit = {

    implicit val slider: Slider = d3.slider().axis(true).step(1)

    val weightByPopulationSwitch: Input = dom.document.getElementById("weightByPopulation").asInstanceOf[Input]
    implicit val isWeightByPopulation:()=> Boolean = () => weightByPopulationSwitch.checked

    Ajax.get("/socio").onComplete {

      case Success(xhr) =>
        socioEconomic = parseCait(xhr.responseText)

      case Failure(e) =>
        dom.window.alert("Failed to load socio economic data : " + e.getMessage)

    }

    Ajax.get("/emissions").onComplete {

      case Success(xhr) => init(
        xhr.responseText,
        initTreeMap("#gasTreeMap", "d3plus"),
        initTreeMap("#sourcesTreeMap", "category10"),
        initGeoMap("#mapArea")
      )

      case Failure(e) =>
        dom.window.alert("Failed to load emissions data : " + e.getMessage)

    }

  }


  def init(ajaxResponseText: String, gasTreeMap: Viz, sourceTreeMap: Viz, geoMap: Viz)
          (implicit slider: Slider, isWeightByPopulation:()=> Boolean): Unit = {

    implicit val caitMap: CaitMap = parseCait(ajaxResponseText)

    initSlider("#yearSlider", gasTreeMap, sourceTreeMap, geoMap)

    reDraw(
      selectedYear,
      reDrawGasTreeMap(gasTreeMap),
      reDrawSourceTreeMap(sourceTreeMap),
      reDrawGeoMap(geoMap, selectedYear)
    )
    
    val mapLegendUnits:Span = dom.document.getElementById("mapLegendUnits").asInstanceOf[Span]
    mapLegendUnits.textContent = getUnits(isWeightByPopulation)

    val weightByPopulationChangeFunction = (event: Event) => {
      reDraw(
        selectedYear,
        reDrawGeoMap(geoMap, selectedYear)
      )
      mapLegendUnits.textContent = getUnits(isWeightByPopulation)
    }

    dom.window.addEventListener("change", weightByPopulationChangeFunction, useCapture = true)

    geoMap.focus(
      false,
      countrySelectionChange(
        gasTreeMap,
        dom.document.getElementById("gasFilterLabel").asInstanceOf[Span],
        sourceTreeMap,
        dom.document.getElementById("sourcesFilterLabel").asInstanceOf[Span]
      )
    )

  }


  def initSlider(domSelector: String, gasTreeMap: Viz, sourceTreeMap: Viz, geoMap: Viz)
                (implicit caitMap: CaitMap, slider: Slider, isWeightByPopulation:()=>Boolean) = {

    paramateriseSlider

    slider.on(
      "slideend",
      reDraw(
        reDrawGasTreeMap(gasTreeMap),
        reDrawSourceTreeMap(sourceTreeMap),
        reDrawGeoMap(geoMap, selectedYear)
      )
    )

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


  def initTreeMap(domSelector: String, colorScaleName:Any): Viz = formattedViz(()=>false)
    .`type`("tree_map")
    .container(domSelector)
    .resize(true)
    .id(NAME)
    .color(
      js.Dictionary(
        SCALE -> colorScaleName
      )
    )
    .size(VALUE)


  def reDraw(toDraw:DrawerFunction*)(implicit caitMap: CaitMap): (Event, Int) => Unit =
    (event: Event, selectedYear: Int) => reDraw(selectedYear, toDraw: _*)


  def reDraw(selectedYear: Int, toDraw:DrawerFunction*)(implicit caitMap: CaitMap) =
    caitMap.get(selectedYear.toString) match {
      case Some(yearDetail) => toDraw.foreach((drawer:DrawerFunction) => drawer(yearDetail))
      case None => //TODO clear data
    }


  def reDrawGasTreeMap(gasTreeMap: Viz):DrawerFunction = (yearDetail: CaitYearDetail) => {
    drawTreeMap(yearDetail, GASES, gasTreeMap, CO2, N2O, CH4)
  }

  def reDrawSourceTreeMap(sourceTreeMap: Viz):DrawerFunction = (yearDetail: CaitYearDetail) => {
    drawTreeMap(yearDetail, SOURCE, sourceTreeMap, ENERGY, TRANSPORT, AGRICULTURE, INDUSTRIAL, WASTE, LAND_USE_CHANGE)
  }

  def drawTreeMap(yearDetail: CaitYearDetail, section: String, treeMap: Viz, keys: String*): Unit = {

    treeMap.data(
      keys.map(
        (key: String) => keyToTreeMapEntry(yearDetail, section, key)
      ).toJSArray
    ).draw()

  }


  def keyToTreeMapEntry(yearDetail: CaitYearDetail, section: String, key: String) =
    js.Dictionary(
      NAME -> key,
      VALUE -> sumYearDetailIfApplicable(yearDetail, section, key)
    )


  def sumYearDetailIfApplicable(yearDetail: CaitYearDetail, section: String, key: String): Double =
    if (selectedCountryName == null) yearDetail.foldLeft(0.0)(specificSumFunction(section, key))
    else if (selectedCountryName == NO_DATA) 0.0
    else yearDetail(selectedCountryName)(section)(key)


  def specificSumFunction(section: String, key: String) = (runningTotal: Double, keyValue: (String, CaitYearCountryDetail)) =>
    runningTotal + keyValue._2(section)(key)


  def reDrawGeoMap(geoMap: Viz, selectedYear: Int)(implicit isWeightByPopulation:()=> Boolean):DrawerFunction =
    (yearDetail: CaitYearDetail) => drawGeoMap(yearDetail, geoMap)(isWeightByPopulation, selectedYear)


  def drawGeoMap(yearDetail: CaitYearDetail, geoMap: Viz)(implicit isWeightByPopulation:()=> Boolean, selectedYear: Int): Unit = {

    implicit val weightedByPopulation = isWeightByPopulation()

    geoMap
      .data(
        yearDetail
          .filterKeys(filterCountriesMissingPopulationIfApplicable)
          .mapValues(countryToSum)
          .filter(filterCountriesMissingData)
          .map(countrySumToGeoMapEntry)
          .toJSArray
      )
      .draw()

  }

  def formattedViz(isWeightedByPopulation:()=>Boolean): Viz = d3plus.viz()
    .format(
      js.Dictionary(
        TEXT -> formatValueLabelsIfApplicable,
        NUMBER -> formatNumbersIfApplicable(isWeightedByPopulation)
      )
    )

  def formatValueLabelsIfApplicable = (text:Any, params:js.Dictionary[Any]) => {
    if(VALUE.equals(text)) "Emissions"
    else text.toString.split(" ").map(_.capitalize).mkString(" ") //better title caseing than d3plus
  }


  def formatNumbersIfApplicable(isWeightedByPopulation:()=>Boolean) = (number:Any, params:js.Dictionary[Any]) => {
    val oneDecimalPlace = d3.format(",")(d3.round(number.asInstanceOf[Double], 0))
    if(VALUE.equals(params(KEY)) && params.contains("data")) oneDecimalPlace + "&nbsp;" + getUnits(isWeightedByPopulation)
    else if (SHARE.equals(params(KEY))) oneDecimalPlace + "%"
    else oneDecimalPlace
  }


  def getUnits(isWeightedByPopulation:()=>Boolean) =
    if (isWeightedByPopulation())
      "kilograms CO₂ equivalent per person"
    else
      "million metric tons CO₂ equivalent"


  def filterCountriesMissingData = (countrySumPair: (String, Double)) => countrySumPair._2 != 0


  def filterCountriesMissingPopulationIfApplicable(implicit weightedByPopulation: Boolean, selectedYear: Int) =
    (caitCountry: String) =>
      !weightedByPopulation || socioEconomic(selectedYear.toString)(caitCountry)(POPULATION)(POPULATION) > 0


  def initGeoMap(domSelector: String)(implicit isWeightedByPopulation:()=>Boolean): Viz =
    formattedViz(isWeightedByPopulation)
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


  def countrySelectionChange(gasTreeMap:Viz, gasFilterLabel: Span, sourceTreeMap:Viz, sourcesFilterLabel: Span)
                            (implicit slider: Slider, caitMap: CaitMap) =
    (nodeIDs:js.Array[String], viz:Viz) => {

      if(nodeIDs==null || nodeIDs.isEmpty) {
        selectedCountryName = null
      }
      else {
        selectedCountryName = Alpha5ToCaitCountry.getOrElse(nodeIDs(0), NO_DATA)
      }

      if(selectedCountryName==null || selectedCountryName == NO_DATA)
        updateFilterLabels("", gasFilterLabel, sourcesFilterLabel)
      else
        updateFilterLabels("for " + selectedCountryName, gasFilterLabel, sourcesFilterLabel)

      reDraw(
        selectedYear,
        reDrawGasTreeMap(gasTreeMap),
        reDrawSourceTreeMap(sourceTreeMap)
      )

    }

  def updateFilterLabels(newValue:String, gasFilterLabel: Span, sourcesFilterLabel: Span): Unit ={
    gasFilterLabel.textContent = newValue
    sourcesFilterLabel.textContent = newValue
  }


  def countryToSum = (caitYearCountryDetail: CaitYearCountryDetail) =>
    caitYearCountryDetail(GASES).foldLeft(0.0)(countrySumFunction)


  def countrySumToGeoMapEntry(implicit weightByPopulation: Boolean, selectedYear: Int) = (keyValue: (String, Double)) => {

    val caitCountry = keyValue._1

    js.Dictionary(
      NAME -> caitCountry,
      ID -> CaitCountryToAlpha5(caitCountry),
      VALUE -> weightByPopulationIfApplicable(
        caitCountry,
        quashNegatives(keyValue._2)
      )
    )

  }


  def quashNegatives(potentiallyNegative: Double): Double = if (potentiallyNegative > 0.0) potentiallyNegative else 0.0


  def countrySumFunction = (runningTotal: Double, keyValue: (String, Double)) =>
    runningTotal + keyValue._2


  def weightByPopulationIfApplicable(caitCountry: String, rawCountryTotal: Double)
                                    (implicit weightByPopulation: Boolean, selectedYear: Int): Double = {

    // 1,000,000,000 is to convert 'million metric tons' to kilograms
    // as per https://groups.google.com/forum/#!topic/climate-analysis-indicators-tool/yWVxUKYKqcE
    if (weightByPopulation)
      1000000000 * rawCountryTotal / socioEconomic(selectedYear.toString)(caitCountry)(POPULATION)(POPULATION)
    else
      rawCountryTotal

  }

}