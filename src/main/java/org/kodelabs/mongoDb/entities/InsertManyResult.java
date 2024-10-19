package org.kodelabs.mongoDb.entities;

import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class InsertManyResult {
  // A map of the index of the inserted document to the id of the inserted document
  public Map<Integer, String> insertedIds;

  public InsertManyResult() {}

  public InsertManyResult(Map<Integer, String> insertedIds) {
    this.insertedIds = insertedIds;
  }

  public static InsertManyResult from(
      com.mongodb.client.result.InsertManyResult mongoInsertManyResult) {
    InsertManyResult insertManyResult = new InsertManyResult();
    insertManyResult.insertedIds = new HashMap<>();

    if (mongoInsertManyResult.getInsertedIds() != null) {
      mongoInsertManyResult
          .getInsertedIds()
          .forEach(
              (integer, bsonValue) -> {
                if (bsonValue != null && bsonValue.asString() != null) {
                  insertManyResult.insertedIds.put(integer, bsonValue.asString().getValue());
                }
              });
    }

    return insertManyResult;
  }

  @Override
  public String toString() {
    return JsonObject.mapFrom(this).encode();
  }
}
