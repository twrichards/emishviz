package controllers

import play.api.mvc._
import shared.SharedMessages

class HomeController extends Controller {

  def index = Action {
    Ok(views.html.index(SharedMessages.welcome))
  }

}