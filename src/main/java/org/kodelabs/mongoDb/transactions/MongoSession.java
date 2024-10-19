package org.kodelabs.mongoDb.transactions;

import com.mongodb.reactivestreams.client.ClientSession;
import org.reactivestreams.Publisher;

/** Created by Gentrit Gojani on 9/1/20. */
public class MongoSession {
  private final ClientSession session;

  public MongoSession(ClientSession clientSession) {
    this.session = clientSession;
  }

  public ClientSession unWrap() {
    return session;
  }

  void startTransaction() {
    session.startTransaction();
  }

  Publisher<Void> commitTransaction() {
    return session.commitTransaction();
  }

  Publisher<Void> abortTransaction() {
    return session.abortTransaction();
  }

  void close() {
    session.close();
  }
}
