import facades.d3.ImplicitAddons._
import facades.d3.Slider
import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.html
import org.scalajs.dom.raw.Event
import org.singlespaced.d3js.d3
import shared._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.util.{Failure, Success}

object MainScalaJS extends js.JSApp {

  def main(): Unit = {

    implicit val slider: Slider = d3.slider().axis(true).step(1)

    Ajax.get("/emissions").onComplete {
      case Success(xhr) => init(xhr.responseText)
      case Failure(e) => dom.window.alert("Failed to load emissions data : " + e.getMessage)
    }

  }

  def init(ajaxResponseText: String)(implicit slider: Slider): Unit = {

    implicit val parsed: CaitMap = parseCait(ajaxResponseText)

    paramateriseSlider

    slider.on("slide", yearChangeHandler)

  }

  def yearChangeHandler(implicit caitMap: CaitMap): (Event, Int) => Unit = (event: Event, value: Int) => {

    dom.document.getElementById("year-detail-json").asInstanceOf[html.Pre].textContent =
      caitMap.get(value.toString) match {
        case Some(yearDetail) => stringifyCaitYearDetail(yearDetail)
        case None => "No data found for year"
      }
  }

  def paramateriseSlider(implicit caitMap: CaitMap, slider: Slider): Unit = {

    slider.min(caitMap.keySet.min.toInt)

    val max: Int = caitMap.keySet.max.toInt
    slider.max(max)
    slider.value(max)

    d3.select("#year-slider").call(slider)

  }

}