package org.kodelabs.mongoDb.entities;

import io.vertx.core.json.JsonObject;

/** Created by Gentrit Gojani on 8/3/18. */
public class DeleteResult {
  public long deletedCount;

  public DeleteResult() {}

  public DeleteResult(long deletedCount) {
    this.deletedCount = deletedCount;
  }

  @Override
  public String toString() {
    return JsonObject.mapFrom(this).encode();
  }
}
