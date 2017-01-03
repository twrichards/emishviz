package example

import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.{Event, html}
import shared.CaitMap
import upickle._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

object MainScalaJS extends js.JSApp {

  def parameteriseSlider(ajaxResponseText: String)(implicit slider: html.Input): Unit = {

    val parsed: CaitMap = default.readJs[CaitMap](json.read(ajaxResponseText))

    slider.min = parsed.keySet.min

    val max = parsed.keySet.max
    slider.max = max
    slider.value = max

  }

  def main(): Unit = {

    implicit val slider: html.Input = dom.document.getElementById("year-slider").asInstanceOf[html.Input]
    implicit val sliderLabel: html.Label = dom.document.getElementById("year-slider-label").asInstanceOf[html.Label]

    slider.oninput = (event: Event) => sliderLabel.textContent = slider.value

    Ajax.get("/emissions").onSuccess {

      case xhr => parameteriseSlider(xhr.responseText)

    }


  }

}