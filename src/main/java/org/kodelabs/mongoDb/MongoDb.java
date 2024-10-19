package org.kodelabs.mongoDb;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.mongodb.reactive.ReactiveMongoDatabase;
import io.smallrye.mutiny.Uni;
import org.kodelabs.mongoDb.transactions.MongoSession;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MongoDb {
  private final ReactiveMongoClient reactiveMongoClient;

  @Inject MongoConnection mongoConnection;

  @Inject
  public MongoDb(ReactiveMongoClient reactiveMongoClient) {
    this.reactiveMongoClient = reactiveMongoClient;
  }

  public <TEntity> ReactiveMongoCollection<TEntity> getCollection(
      String collectionName, Class<TEntity> entity) {
    return getMongoDatabase().getCollection(collectionName, entity);
  }

  private ReactiveMongoDatabase getMongoDatabase() {
    return reactiveMongoClient.getDatabase(mongoConnection.name());
  }

  public Uni<MongoSession> startMongoSession() {
    return reactiveMongoClient.startSession().map(MongoSession::new);
  }
}
