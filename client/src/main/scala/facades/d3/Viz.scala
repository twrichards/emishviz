package facades.d3

import scala.scalajs.js

@js.native
trait Viz extends js.Object {

  def container(selector: String): Viz = js.native

  def data(dataArray: js.Array[js.Dictionary[Any]]): Viz = js.native

  def `type`(typeName: String): Viz = js.native

  def id(uniquenessKey: String): Viz = js.native

  def size(sizingKey: String): Viz = js.native

  def resize(enabled: Boolean): Viz = js.native

  def coords(config: js.Dictionary[Any]): Viz = js.native

  def text(labelKey: String): Viz = js.native

  def color(config: js.Dictionary[Any]): Viz = js.native

  def tooltip(tooltipKey: String): Viz = js.native

  def mouse(config: js.Dictionary[Any]): Viz = js.native

  def focus(config: Any, callback: js.Function2[js.Array[String], Viz, Unit]): Viz = js.native

  def draw(): Unit = js.native

}
