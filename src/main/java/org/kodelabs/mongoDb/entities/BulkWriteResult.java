package org.kodelabs.mongoDb.entities;

import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class BulkWriteResult {
  public long deletedCount;
  public long insertedCount;
  public long matchedCount;
  public long modifiedCount;
  public List<String> upserts;

  public BulkWriteResult() {}

  public BulkWriteResult(long matchedCount, long modifiedCount) {
    this.matchedCount = matchedCount;
    this.modifiedCount = modifiedCount;
  }

  public static BulkWriteResult from(com.mongodb.bulk.BulkWriteResult mongoBulkWriteResult) {
    BulkWriteResult bulkWriteResult = new BulkWriteResult();
    bulkWriteResult.deletedCount = mongoBulkWriteResult.getDeletedCount();
    bulkWriteResult.insertedCount = mongoBulkWriteResult.getInsertedCount();
    bulkWriteResult.matchedCount = mongoBulkWriteResult.getMatchedCount();
    bulkWriteResult.modifiedCount = mongoBulkWriteResult.getModifiedCount();

    if (mongoBulkWriteResult.getUpserts().isEmpty()) {
      List<String> upserts = new ArrayList<>();
      mongoBulkWriteResult
          .getUpserts()
          .forEach(bulkWriteUpsert -> upserts.add(bulkWriteUpsert.getId().asString().getValue()));
      bulkWriteResult.upserts = upserts;
    }

    return bulkWriteResult;
  }

  @Override
  public String toString() {
    return JsonObject.mapFrom(this).encode();
  }
}
