package org.kodelabs.mongoDb.entities;

import io.vertx.core.json.JsonObject;

public class InsertOneResult {
  public String insertedId;

  public InsertOneResult() {}

  public InsertOneResult(String insertedId) {
    this.insertedId = insertedId;
  }

  public static InsertOneResult from(
      com.mongodb.client.result.InsertOneResult mongoInsertOneResult) {
    InsertOneResult insertOneResult = new InsertOneResult();

    if (mongoInsertOneResult.getInsertedId() != null
        && mongoInsertOneResult.getInsertedId().asString() != null) {
      insertOneResult.insertedId = mongoInsertOneResult.getInsertedId().asString().getValue();
    }

    return insertOneResult;
  }

  @Override
  public String toString() {
    return JsonObject.mapFrom(this).encode();
  }
}
