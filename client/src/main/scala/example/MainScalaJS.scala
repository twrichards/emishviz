package example

import org.scalajs.dom
import org.scalajs.dom.ext._
import org.scalajs.dom.{Event, html}
import shared.CaitMap
import upickle._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

import org.singlespaced.d3js.Ops._
import org.singlespaced.d3js.d3

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

    val sliderLabel: html.Label = dom.document.getElementById("year-slider-label").asInstanceOf[html.Label]
    slider.oninput = (event: Event) => sliderLabel.textContent = slider.value

    Ajax.get("/emissions").onSuccess {

      case xhr => parameteriseSlider(xhr.responseText)

    }

    val graphHeight = 450

    //The width of each bar.
    val barWidth = 80

    //The distance between each bar.
    val barSeparation = 10

    //The maximum value of the data.
    val maxData = 50

    //The actual horizontal distance from drawing one bar rectangle to drawing the next.
    val horizontalBarDistance = barWidth + barSeparation

    //The value to multiply each bar's value by to get its height.
    val barHeightMultiplier = graphHeight / maxData

    //Color for start
    val c = d3.rgb("DarkSlateBlue")

    val rectXFun = (d: Int, i: Int) => i * horizontalBarDistance
    val rectYFun = (d: Int) => graphHeight - d * barHeightMultiplier
    val rectHeightFun = (d: Int) => d * barHeightMultiplier
    val rectColorFun = (d: Int, i: Int) => c.brighter(i * 0.5).toString

    val svg = d3.select("#playground").append("svg").attr("width", "100%").attr("height", "450px")
    val sel = svg.selectAll("rect").data(js.Array(8, 22, 31, 36, 48, 17, 25))
    sel.enter()
      .append("rect")
      .attr("x", rectXFun)
      .attr("y", rectYFun)
      .attr("width", barWidth)
      .attr("height", rectHeightFun)
      .style("fill", rectColorFun)


  }

}