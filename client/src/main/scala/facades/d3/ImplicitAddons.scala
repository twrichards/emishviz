package facades.d3

import org.singlespaced.d3js.d3

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

// this approach is used because org.singlespaced.d3js.d3 is implemented as an 'object' rather than 'trait'
object ImplicitAddons {

  @js.native
  trait AddonToD3[A] extends js.Object {
    def slider(x: A): Slider
  }

  @JSName("d3")
  @js.native
  implicit object Slidable extends AddonToD3[d3.type] {
    def slider(x: d3.type): Slider = js.native
  }

  implicit class AddonUtil[A](x: A) {
    def slider()(implicit makesSlidable: AddonToD3[A]) = {
      makesSlidable.slider(x)
    }
  }

}
