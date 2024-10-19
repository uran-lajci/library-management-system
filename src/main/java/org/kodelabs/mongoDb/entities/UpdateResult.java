package org.kodelabs.mongoDb.entities;

import io.vertx.core.json.JsonObject;

public class UpdateResult {
  public long matchedCount;
  public long modifiedCount;
  public String upsertedId;

  public UpdateResult() {}

  public UpdateResult(long matchedCount, long modifiedCount) {
    this.matchedCount = matchedCount;
    this.modifiedCount = modifiedCount;
  }

  public UpdateResult(long matchedCount, long modifiedCount, String upsertedId) {
    this.matchedCount = matchedCount;
    this.modifiedCount = modifiedCount;
    this.upsertedId = upsertedId;
  }

  public static UpdateResult from(com.mongodb.client.result.UpdateResult mongoUpdateResult) {
    UpdateResult updateResult = new UpdateResult();

    updateResult.matchedCount = mongoUpdateResult.getMatchedCount();
    updateResult.modifiedCount = mongoUpdateResult.getModifiedCount();

    if (mongoUpdateResult.getUpsertedId() != null
        && mongoUpdateResult.getUpsertedId().asString() != null) {
      updateResult.upsertedId = mongoUpdateResult.getUpsertedId().asString().getValue();
    }

    return updateResult;
  }

  @Override
  public String toString() {
    return JsonObject.mapFrom(this).encode();
  }
}
