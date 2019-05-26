package v1.merchant

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class MerchantRouter @Inject()(controller: MerchantController) extends SimpleRouter {
  val prefix = "/v1/merchant"

  override def routes: Routes = {
    case POST(p"/signup") => controller.process
    case POST(p"/info") => controller.process
    case GET(p"/info") => controller.getMerchantInfo()
    case GET(p"/terms_conditions") => controller.termsAndConditions()
  }
}
