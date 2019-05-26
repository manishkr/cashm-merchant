package v1.merchant

import datamodel.{Merchant, TermsAndConditions}
import javax.inject.Inject
import play.api.{Configuration, Logger}
import play.api.data.Form
import play.api.libs.ws.WSClient
import play.api.libs.json.Json
import play.api.mvc._
import utils.CurrencyCode
import v1.auth.{AuthBaseController, AuthControllerComponents, AuthRequest}

import scala.concurrent.{ExecutionContext, Future}

class MerchantController @Inject()(cc: AuthControllerComponents)(ws: WSClient)(config: Configuration)(implicit ec: ExecutionContext)
  extends AuthBaseController(cc){

  private val logger = Logger(getClass)
  private val authURL = config.get[String]("cashm.authserver")

    private val form: Form[Merchant] = {
    import play.api.data.Forms._

  Form(
      mapping(
        "email" -> nonEmptyText,
        "name" -> nonEmptyText,
        "company_name" -> nonEmptyText,
        "website" -> nonEmptyText,
        "terms_conditions_id" -> nonEmptyText,
        "billing_address" -> text
      )(Merchant.apply)(Merchant.unapply))
  }

  def process: Action[AnyContent] = AuthAction.async { implicit request =>
    logger.trace("process: ")
    processJsonPost()
  }

  def termsAndConditions(): Action[AnyContent] = AuthAction.async { implicit request =>
    logger.trace("termsAndConditions: ")
    for {
      termsAndCond <- TermsAndConditions.get()
    } yield {
      Ok(Json.obj("terms_conditions" -> termsAndCond))
    }
  }

  def getMerchantInfo: Action[AnyContent] = AuthAction.async { implicit request =>
    logger.trace("getMerchantInfo: ")
    getAuth().map {
      case Some((userId, _)) => Merchant.get(userId)
        .map{
          case Some(json) => Ok(Json.obj("info" -> json))
          case _ => NotFound
        }
      case _ => Future(Unauthorized)
    }.flatten
  }

  private def getAuth[A]()(implicit request: AuthRequest[A]) = {
    {
      {
        for {
          auth <- request.headers.get("Authorization")
        } yield {
          val url = s"$authURL/v1/auth"
          ws.url(url).withHttpHeaders("Authorization" -> auth)
            .get()
            .map { response =>
              if(response.status == 200) {
                val userId = (response.json \ "userId").asOpt[String]
                val mobile = (response.json \ "mobile").asOpt[String]
                (userId, mobile)
              }else{
                (None, None)
              }
            }
            .map {
              case (Some(userId), Some(mobile)) => Some((userId, mobile))
              case _ => None
            }
        }
      } match {
        case Some(f) => f.map(Some(_))
        case None => Future(None)
      }
    }
      .map{
        case Some(Some(f)) => Some(f)
        case _ => None
      }
  }

  private def processJsonPost[A]()(implicit request: AuthRequest[A]): Future[Result] = {
    def failure(badForm: Form[Merchant]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(merchant: Merchant) = {
      getAuth().map {
        case Some((userId, _)) => merchant.save(userId)
          .map(_ => Ok(Json.obj("status" -> "success")))
        case _ => Future(BadRequest)
      }.flatten
    }

    form.bindFromRequest().fold(failure, success)
  }
}
