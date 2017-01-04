import facades.d3.ImplicitAddons._
import facades.d3.Slider
import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.html
import org.scalajs.dom.raw.Event
import org.singlespaced.d3js.d3
import shared.CaitMap
import upickle._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

object MainScalaJS extends js.JSApp {

  def main(): Unit = {

    implicit val slider: Slider = d3.slider().axis(true).step(1)

    implicit val sliderLabel: html.Label = dom.document.getElementById("year-slider-label").asInstanceOf[html.Label]

    slider.on("slide", yearChangeHandler)

    Ajax.get("/emissions").onSuccess {

      case xhr => initSlider(xhr.responseText)

    }

  }

  def yearChangeHandler(implicit sliderLabel: html.Label): (Event, Int) => Unit = (event: Event, value: Int) => {
    sliderLabel.textContent = value.toString
  }

  def initSlider(ajaxResponseText: String)(implicit slider: Slider): Unit = {

    val parsed: CaitMap = default.readJs[CaitMap](json.read(ajaxResponseText))

    slider.min(parsed.keySet.min.toInt)

    val max: Int = parsed.keySet.max.toInt
    slider.max(max)
    slider.value(max)

    d3.select("#year-slider").call(slider)

  }

}