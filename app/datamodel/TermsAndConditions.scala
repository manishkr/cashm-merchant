package datamodel

import databases.MongoDBHandler
import org.bson.types.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext

object TermsAndConditions {
  val collectionName = "TermsAndConditions"

  def get()(implicit ec: ExecutionContext) ={
    MongoDBHandler.find(collectionName, Document())
      .map(_.headOption.getOrElse{
        val document = Document("_id" -> new ObjectId(), "text" -> "Mera Kuber merchant terms and conditions")
        MongoDBHandler.insertOne(collectionName, document)
        document
      })
      .map{doc => Json.obj("document_id" -> doc.getObjectId("_id").toString, "text" -> doc.getString("text"))
      }
  }
}
