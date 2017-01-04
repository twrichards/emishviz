package facades.d3

import org.scalajs.dom.raw.Event
import org.singlespaced.d3js.svg.Axis

import scala.scalajs.js

@scala.scalajs.js.native
trait Slider extends scala.scalajs.js.Function with scala.scalajs.js.Function1[scala.scalajs.js.|[org.singlespaced.d3js.Selection[scala.scalajs.js.Any], org.singlespaced.d3js.Transition[scala.scalajs.js.Any]], scala.Unit] {

  def axis(enabled: Boolean): Slider = js.native

  def axis(axis: Axis): Slider = js.native

  def value(value: Int): Slider = js.native

  def min(value: Int): Slider = js.native

  def max(value: Int): Slider = js.native

  def step(value: Int): Slider = js.native

  def on(`type`: String, listener: js.Function2[Event, Int, Unit]): Slider = js.native

}
