package controllers

import org.scalatestplus.play.PlaySpec
import play.api.mvc.{Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class HomeControllerSpec extends PlaySpec with Results  {

  val unit:HomeController = new HomeController()

  "HomeController" should {

    "supply some valid HTML" in {

      val resultFuture: Future[Result] = unit.index().apply(FakeRequest())

      val bodyHTML:String = contentAsString(resultFuture).trim

      bodyHTML must startWith ("<html>")

      bodyHTML must endWith ("</html>")

    }

  }

}
