package datamodel

import databases.MongoDBHandler
import org.mongodb.scala.bson.collection.immutable.Document
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

case class Merchant(email : String, name: String, companyName  :String, website: String, termsConditionId : String, billingAddress : String) {
  def save(userId : String)(implicit ec: ExecutionContext) = {
    MongoDBHandler.insertOne(Merchant.collectionName, Document("userId" -> userId, "email" -> email,
      "name" -> name, "companyName" -> companyName, "website" -> website, "termsConditionId" -> termsConditionId, "billingAddress" -> billingAddress))
      .map(_=> true)
  }
}

object Merchant{
  val collectionName = "Merchant"

  def get(userId : String)(implicit ec: ExecutionContext) = {
    MongoDBHandler.find(collectionName, Document("userId" -> userId))
      .map{_.headOption.map(doc => jsonify(doc))}
  }

  private def jsonify(doc : Document)= Json.obj("email" -> doc.getString("email"), "name" -> doc.getString("name"),
    "company_name" -> doc.getString("companyName"), "website" -> doc.getString("website"),
    "terms_conditions_id" -> doc.getString("termsConditionId"), "billing_address" -> doc.getString("billingAddress"))
}