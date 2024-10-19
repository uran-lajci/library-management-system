package org.kodelabs.mongoDb.mutiny;

import com.mongodb.MongoCommandException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.*;
import io.quarkus.mongodb.AggregateOptions;
import io.quarkus.mongodb.FindOptions;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.runtime.ExecutorRecorder;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.bson.conversions.Bson;
import org.kodelabs.entities.BaseEntity;
import org.kodelabs.entities.exceptions.BaseException;
import org.kodelabs.mongoDb.entities.*;
import org.kodelabs.mongoDb.transactions.MongoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Updates.combine;

/** Created by Gentrit Gojani on 6/10/20. */
public class Db {
  private static final Logger logger = LoggerFactory.getLogger(Db.class.getName());

  // region Aggregate
  public static <T> Uni<List<T>> aggregate(
      ReactiveMongoCollection<T> collection, List<? extends Bson> pipeline) {

    return collection.aggregate(pipeline).emitOn(ExecutorRecorder.getCurrent()).collect().asList();
  }

  public static String $(String field) {
    return "$" + field;
  }

  public static String $$(String s) {
    return "$$" + s;
  }

  public static String $(String fieldName, String secondField) {
    return "$" + fieldName + "." + secondField;
  }

  public static <T, D> Uni<List<D>> aggregate(
      ReactiveMongoCollection<T> collection, List<? extends Bson> pipeline, Class<D> clazz) {

    return collection
        .aggregate(pipeline, clazz)
        .emitOn(ExecutorRecorder.getCurrent())
        .collectItems()
        .asList();
  }

  public static <T> Uni<List<T>> aggregate(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      List<? extends Bson> pipeline) {

    return collection
        .aggregate(mongoSession.unWrap(), pipeline)
        .emitOn(ExecutorRecorder.getCurrent())
        .collectItems()
        .asList();
  }

  public static <T, D> Uni<List<D>> aggregate(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      List<? extends Bson> pipeline,
      Class<D> clazz) {

    return collection
        .aggregate(mongoSession.unWrap(), pipeline, clazz)
        .emitOn(ExecutorRecorder.getCurrent())
        .collectItems()
        .asList();
  }

  public static <T> Uni<List<T>> aggregate(
      ReactiveMongoCollection<T> collection,
      List<? extends Bson> pipeline,
      AggregateOptions options) {

    return collection
        .aggregate(pipeline, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .collectItems()
        .asList();
  }

  public static <T, D> Uni<List<D>> aggregate(
      ReactiveMongoCollection<T> collection,
      List<? extends Bson> pipeline,
      Class<D> clazz,
      AggregateOptions options) {

    return collection
        .aggregate(pipeline, clazz, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .collectItems()
        .asList();
  }

  public static <T> Uni<List<T>> aggregate(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      List<? extends Bson> pipeline,
      AggregateOptions options) {

    return collection
        .aggregate(mongoSession.unWrap(), pipeline, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .collectItems()
        .asList();
  }

  public static <T, D> Uni<List<D>> aggregate(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      List<? extends Bson> pipeline,
      Class<D> clazz,
      AggregateOptions options) {

    return collection
        .aggregate(mongoSession.unWrap(), pipeline, clazz, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .collectItems()
        .asList();
  }
  // endregion

  // region Bulk Write
  public static <T> Uni<BulkWriteResult> bulkWrite(
      ReactiveMongoCollection<T> collection, List<? extends WriteModel<? extends T>> requests) {

    return collection
        .bulkWrite(requests)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(BulkWriteResult::from);
  }

  public static <T> Uni<BulkWriteResult> bulkWrite(
      ReactiveMongoCollection<T> collection,
      List<? extends WriteModel<? extends T>> requests,
      BulkWriteOptions options) {

    return collection
        .bulkWrite(requests, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(BulkWriteResult::from);
  }

  public static <T> Uni<BulkWriteResult> bulkWrite(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      List<? extends WriteModel<? extends T>> requests) {

    return collection
        .bulkWrite(mongoSession.unWrap(), requests)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(BulkWriteResult::from);
  }

  public static <T> Uni<BulkWriteResult> bulkWrite(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      List<? extends WriteModel<? extends T>> requests,
      BulkWriteOptions options) {

    return collection
        .bulkWrite(mongoSession.unWrap(), requests, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(BulkWriteResult::from);
  }
  // endregion

  // region Count
  public static <T> Uni<Long> countDocuments(ReactiveMongoCollection<T> collection) {

    return collection.countDocuments().emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T> Uni<Long> countDocuments(ReactiveMongoCollection<T> collection, Bson filter) {

    return collection.countDocuments(filter).emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T> Uni<Long> countDocuments(
      ReactiveMongoCollection<T> collection, Bson filter, CountOptions options) {

    return collection.countDocuments(filter, options).emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T> Uni<Long> countDocuments(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession) {

    return collection.countDocuments(mongoSession.unWrap()).emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T> Uni<Long> countDocuments(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, Bson filter) {

    return collection
        .countDocuments(mongoSession.unWrap(), filter)
        .emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T> Uni<Long> countDocuments(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      Bson filter,
      CountOptions options) {

    return collection
        .countDocuments(mongoSession.unWrap(), filter, options)
        .emitOn(ExecutorRecorder.getCurrent());
  }

  // endregion

  // region Delete One
  public static <T> Uni<DeleteResult> deleteOne(
      ReactiveMongoCollection<T> collection, Bson filter) {

    return collection
        .deleteOne(filter)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(deleteResult -> new DeleteResult(deleteResult.getDeletedCount()));
  }

  public static <T> Uni<DeleteResult> deleteOne(
      ReactiveMongoCollection<T> collection, Bson filter, DeleteOptions options) {

    return collection
        .deleteOne(filter, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(deleteResult -> new DeleteResult(deleteResult.getDeletedCount()));
  }

  public static <T> Uni<DeleteResult> deleteOne(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, Bson filter) {

    return collection
        .deleteOne(mongoSession.unWrap(), filter)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(deleteResult -> new DeleteResult(deleteResult.getDeletedCount()));
  }

  public static <T> Uni<DeleteResult> deleteOne(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      Bson filter,
      DeleteOptions options) {

    return collection
        .deleteOne(mongoSession.unWrap(), filter, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(deleteResult -> new DeleteResult(deleteResult.getDeletedCount()));
  }
  // endregion

  // region Delete Many
  public static <T> Uni<DeleteResult> deleteMany(
      ReactiveMongoCollection<T> collection, Bson filter) {

    return collection
        .deleteMany(filter)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(deleteResult -> new DeleteResult(deleteResult.getDeletedCount()));
  }

  public static <T> Uni<DeleteResult> deleteMany(
      ReactiveMongoCollection<T> collection, Bson filter, DeleteOptions options) {

    return collection
        .deleteMany(filter, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(deleteResult -> new DeleteResult(deleteResult.getDeletedCount()));
  }

  public static <T> Uni<DeleteResult> deleteMany(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, Bson filter) {

    return collection
        .deleteMany(mongoSession.unWrap(), filter)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(deleteResult -> new DeleteResult(deleteResult.getDeletedCount()));
  }

  public static <T> Uni<DeleteResult> deleteMany(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      Bson filter,
      DeleteOptions options) {

    return collection
        .deleteMany(mongoSession.unWrap(), filter, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(deleteResult -> new DeleteResult(deleteResult.getDeletedCount()));
  }
  // endregion

  // region Find One
  public static <T> Uni<T> findOne(ReactiveMongoCollection<T> collection, FindOptions options) {
    options.limit(1);

    return collection
        .find(options)
        .emitOn(ExecutorRecorder.getCurrent())
        .toUni()
        .onItem()
        .ifNull()
        .failWith(new NotFoundException());
  }

  public static <T, D> Uni<D> findOne(
      ReactiveMongoCollection<T> collection, Class<D> clazz, FindOptions options) {

    options.limit(1);
    return collection
        .find(clazz, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .toUni()
        .onItem()
        .ifNull()
        .failWith(new NotFoundException());
  }

  public static <T> Uni<T> findOne(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, FindOptions options) {

    options.limit(1);
    return collection
        .find(mongoSession.unWrap(), options)
        .emitOn(ExecutorRecorder.getCurrent())
        .toUni()
        .onItem()
        .ifNull()
        .failWith(new NotFoundException());
  }

  public static <T, D> Uni<D> findOne(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      Class<D> clazz,
      FindOptions options) {

    options.limit(1);
    return collection
        .find(mongoSession.unWrap(), clazz, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .toUni()
        .onItem()
        .ifNull()
        .failWith(new NotFoundException());
  }
  // endregion

  // region Find Many
  public static <T> Uni<List<T>> findMany(ReactiveMongoCollection<T> collection) {

    return collection.find().emitOn(ExecutorRecorder.getCurrent()).collectItems().asList();
  }

  public static <T> Multi<T> findManyFlowable(
      ReactiveMongoCollection<T> collection, FindOptions findOptions) {

    return collection.find(findOptions).emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T, D> Multi<D> findManyFlowable(
      ReactiveMongoCollection<T> collection, FindOptions findOptions, Class<D> clazz) {
    return collection.find(clazz, findOptions).emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T, D> Uni<List<D>> findMany(
      ReactiveMongoCollection<T> collection, Class<D> clazz) {

    return collection.find(clazz).emitOn(ExecutorRecorder.getCurrent()).collectItems().asList();
  }

  public static <T> Uni<List<T>> findMany(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession) {

    return collection
        .find(mongoSession.unWrap())
        .emitOn(ExecutorRecorder.getCurrent())
        .collectItems()
        .asList();
  }

  public static <T, D> Uni<List<D>> findMany(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, Class<D> clazz) {

    return collection
        .find(mongoSession.unWrap(), clazz)
        .emitOn(ExecutorRecorder.getCurrent())
        .collectItems()
        .asList();
  }

  public static <T> Uni<List<T>> findMany(
      ReactiveMongoCollection<T> collection, FindOptions options) {
    return collection.find(options).emitOn(ExecutorRecorder.getCurrent()).collectItems().asList();
  }

  public static <T, D> Uni<List<D>> findMany(
      ReactiveMongoCollection<T> collection, Class<D> clazz, FindOptions options) {

    return collection
        .find(clazz, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .collectItems()
        .asList();
  }

  public static <T> Uni<List<T>> findMany(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, FindOptions options) {

    return collection
        .find(mongoSession.unWrap(), options)
        .emitOn(ExecutorRecorder.getCurrent())
        .collectItems()
        .asList();
  }

  public static <T, D> Uni<List<D>> findMany(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      Class<D> clazz,
      FindOptions options) {

    return collection
        .find(mongoSession.unWrap(), clazz, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .collectItems()
        .asList();
  }
  // endregion

  // region Find Many Mapped
  public static <T extends BaseEntity> Uni<List<String>> mapToEntityIds(Uni<List<T>> uni) {
    return uni.onItem()
        .transform(
            entities -> {
              ArrayList<String> result = new ArrayList<>(entities.size());
              entities.forEach(
                  entity -> {
                    result.add(entity._id);
                  });
              return result;
            });
  }

  public static <T extends BaseEntity> Uni<HashMap<String, T>> mapToHashMap(Uni<List<T>> uni) {
    return uni.onItem()
        .transform(
            entities -> {
              HashMap<String, T> result = new HashMap<>(entities.size());
              entities.forEach(
                  entity -> {
                    result.put(entity._id, entity);
                  });

              return result;
            });
  }
  // endregion

  // region Find And Update
  public static <T extends BaseEntity> Uni<T> findOneAndUpdate(
      ReactiveMongoCollection<T> collection, Bson filter, Bson update) {

    update =
        Updates.combine(
            Updates.currentDate(T.FIELD_UPDATED_AT),
            Updates.setOnInsert(T.FIELD_CREATED_AT, Calendar.getInstance().getTime()),
            update);
    return collection.findOneAndUpdate(filter, update).emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T extends BaseEntity> Uni<T> findOneAndUpdate(
      ReactiveMongoCollection<T> collection,
      Bson filter,
      Bson update,
      FindOneAndUpdateOptions options) {

    update =
        Updates.combine(
            Updates.currentDate(T.FIELD_UPDATED_AT),
            Updates.setOnInsert(T.FIELD_CREATED_AT, Calendar.getInstance().getTime()),
            update);
    return collection
        .findOneAndUpdate(filter, update, options)
        .emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T extends BaseEntity> Uni<T> findOneAndUpdate(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, Bson filter, Bson update) {

    update =
        Updates.combine(
            Updates.currentDate(T.FIELD_UPDATED_AT),
            Updates.setOnInsert(T.FIELD_CREATED_AT, Calendar.getInstance().getTime()),
            update);
    return collection
        .findOneAndUpdate(mongoSession.unWrap(), filter, update)
        .emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T extends BaseEntity> Uni<T> findOneAndUpdate(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      Bson filter,
      Bson update,
      FindOneAndUpdateOptions options) {

    update =
        Updates.combine(
            Updates.currentDate(T.FIELD_UPDATED_AT),
            Updates.setOnInsert(T.FIELD_CREATED_AT, Calendar.getInstance().getTime()),
            update);
    return collection
        .findOneAndUpdate(mongoSession.unWrap(), filter, update, options)
        .emitOn(ExecutorRecorder.getCurrent());
  }
  // endregion

  // region Find And Replace
  public static <T extends BaseEntity> Uni<T> findOneAndReplace(
      ReactiveMongoCollection<T> collection, Bson filter, T entity) {

    entity.addDates();
    return collection.findOneAndReplace(filter, entity).emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T extends BaseEntity> Uni<T> findOneAndReplace(
      ReactiveMongoCollection<T> collection,
      Bson filter,
      T entity,
      FindOneAndReplaceOptions options) {

    entity.addDates();
    return collection
        .findOneAndReplace(filter, entity, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onFailure()
        .transform(
            throwable -> {
              if (throwable instanceof MongoCommandException
                  && ((MongoCommandException) throwable).getCode() == 11000) {
                return new Throwable("Key is already in use. Please choose a different one.");
              }
              return throwable;
            });
  }

  public static <T extends BaseEntity> Uni<T> findOneAndReplace(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, Bson filter, T entity) {

    entity.addDates();
    return collection
        .findOneAndReplace(mongoSession.unWrap(), filter, entity)
        .emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T extends BaseEntity> Uni<T> findOneAndReplace(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      Bson filter,
      T entity,
      FindOneAndReplaceOptions options) {

    entity.addDates();
    return collection
        .findOneAndReplace(mongoSession.unWrap(), filter, entity, options)
        .emitOn(ExecutorRecorder.getCurrent());
  }
  // endregion

  // region Find And Delete
  public static <T> Uni<T> findOneAndDelete(ReactiveMongoCollection<T> collection, Bson filter) {

    return collection.findOneAndDelete(filter).emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T> Uni<T> findOneAndDelete(
      ReactiveMongoCollection<T> collection, Bson filter, FindOneAndDeleteOptions options) {

    return collection.findOneAndDelete(filter, options).emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T> Uni<T> findOneAndDelete(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, Bson filter) {

    return collection
        .findOneAndDelete(mongoSession.unWrap(), filter)
        .emitOn(ExecutorRecorder.getCurrent());
  }

  public static <T> Uni<T> findOneAndDelete(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      Bson filter,
      FindOneAndDeleteOptions options) {

    return collection
        .findOneAndDelete(mongoSession.unWrap(), filter, options)
        .emitOn(ExecutorRecorder.getCurrent());
  }
  // endregion

  // region Indexes

  public static <T> void createExpireIndex(
      ReactiveMongoCollection<T> collection, long expireAfterSeconds) {
    collection
        .createIndex(
            Indexes.ascending(BaseEntity.FIELD_CREATED_AT),
            new IndexOptions().expireAfter(expireAfterSeconds, TimeUnit.SECONDS).background(true))
        .emitOn(ExecutorRecorder.getCurrent())
        .subscribe()
        .with(
            strings -> {},
            throwable -> {
              logger.error(throwable.getMessage());
            });
  }

  public static <T> void createGeo2dSphereIndex(
      ReactiveMongoCollection<T> collection, String fieldName) {
    collection
        .createIndex(Indexes.geo2dsphere(fieldName), new IndexOptions().background(true))
        .emitOn(ExecutorRecorder.getCurrent())
        .subscribe()
        .with(
            strings -> {},
            throwable -> {
              logger.error(throwable.getMessage());
            });
  }

  public static <T> Uni<Void> createGeo2dSphereIndexes(
      ReactiveMongoCollection<T> collection, String fieldName) {
    return collection
        .createIndex(Indexes.geo2dsphere(fieldName), new IndexOptions().background(true))
        .flatMap(res -> Uni.createFrom().voidItem());
  }

  // endregion

  // region Insert One
  public static <T extends BaseEntity> Uni<InsertOneResult> insertOne(
      ReactiveMongoCollection<T> collection, T entity) {

    entity.generateId();
    entity.createdAt = Calendar.getInstance().getTime();
    entity.updatedAt = entity.createdAt;

    return collection
        .insertOne(entity)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(InsertOneResult::from);
  }

  public static <T extends BaseEntity> Uni<InsertOneResult> insertOne(
      ReactiveMongoCollection<T> collection, T entity, InsertOneOptions options) {

    entity.generateId();
    entity.createdAt = Calendar.getInstance().getTime();
    entity.updatedAt = entity.createdAt;

    return collection
        .insertOne(entity, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(InsertOneResult::from)
        .onFailure()
        .transform(
            throwable -> {
              if (throwable instanceof MongoWriteException
                  && ((MongoWriteException) throwable).getError().getCode() == 11000) {
                return new BaseException(409, throwable.getMessage());
              }

              return throwable;
            });
  }

  public static <T extends BaseEntity> Uni<InsertOneResult> insertOne(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, T entity) {

    entity.generateId();
    entity.createdAt = Calendar.getInstance().getTime();
    entity.updatedAt = entity.createdAt;

    return collection
        .insertOne(mongoSession.unWrap(), entity)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(InsertOneResult::from)
        .onFailure()
        .transform(
            throwable -> {
              if (throwable instanceof MongoWriteException
                  && ((MongoWriteException) throwable).getError().getCode() == 11000) {
                return new BaseException(409, throwable.getMessage());
              }

              return throwable;
            });
  }

  public static <T extends BaseEntity> Uni<InsertOneResult> insertOne(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      T entity,
      InsertOneOptions options) {

    entity.generateId();
    entity.createdAt = Calendar.getInstance().getTime();
    entity.updatedAt = entity.createdAt;

    return collection
        .insertOne(mongoSession.unWrap(), entity, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(InsertOneResult::from)
        .onFailure()
        .transform(
            throwable -> {
              if (throwable instanceof MongoWriteException
                  && ((MongoWriteException) throwable).getError().getCode() == 11000) {
                return new BaseException(409, throwable.getMessage());
              }

              return throwable;
            });
  }
  // endregion

  // region Insert One and Get
  public static <T extends BaseEntity> Uni<T> insertOneAndGet(
      ReactiveMongoCollection<T> collection, T entity) {

    entity.generateId();
    entity.createdAt = Calendar.getInstance().getTime();
    entity.updatedAt = entity.createdAt;

    Bson filter = Filters.eq("_id", entity._id);
    FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
    options.upsert(true);
    options.returnDocument(ReturnDocument.AFTER);

    return Db.findOneAndReplace(collection, filter, entity, options)
        .onFailure()
        .transform(
            throwable -> {
              if (throwable instanceof MongoCommandException
                  && ((MongoCommandException) throwable).getCode() == 11000) {
                return new BaseException(
                    409, "Key is already in use. Please choose a different one.");
              }
              return throwable;
            });
  }

  public static <T extends BaseEntity> Uni<T> insertOneAndGet(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, T entity) {

    entity.generateId();
    entity.createdAt = Calendar.getInstance().getTime();
    entity.updatedAt = entity.createdAt;

    Bson filter = Filters.eq("_id", entity._id);
    FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
    options.upsert(true);
    options.returnDocument(ReturnDocument.AFTER);

    return Db.findOneAndReplace(collection, mongoSession, filter, entity, options)
        .onFailure()
        .transform(
            throwable -> {
              if (throwable instanceof MongoCommandException
                  && ((MongoCommandException) throwable).getCode() == 11000) {
                return new BaseException(
                    409, "Key is already in use. Please choose a different one.");
              }

              return throwable;
            });
  }
  // endregion

  // region Insert Many
  public static <T extends BaseEntity> Uni<InsertManyResult> insertMany(
      ReactiveMongoCollection<T> collection, List<T> entities) {

    entities.forEach(
        entity -> {
          entity.generateId();
          entity.createdAt = Calendar.getInstance().getTime();
          entity.updatedAt = entity.createdAt;
        });

    return collection
        .insertMany(entities)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(InsertManyResult::from);
  }

  public static <T extends BaseEntity> Uni<InsertManyResult> insertMany(
      ReactiveMongoCollection<T> collection, List<T> entities, InsertManyOptions options) {

    entities.forEach(
        entity -> {
          entity.generateId();
          entity.createdAt = Calendar.getInstance().getTime();
          entity.updatedAt = entity.createdAt;
        });

    return collection
        .insertMany(entities, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(InsertManyResult::from);
  }

  public static <T extends BaseEntity> Uni<InsertManyResult> insertMany(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, List<T> entities) {

    entities.forEach(
        entity -> {
          entity.generateId();
          entity.createdAt = Calendar.getInstance().getTime();
          entity.updatedAt = entity.createdAt;
        });

    return collection
        .insertMany(mongoSession.unWrap(), entities)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(InsertManyResult::from);
  }

  public static <T extends BaseEntity> Uni<InsertManyResult> insertMany(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      List<T> entities,
      InsertManyOptions options) {

    entities.forEach(
        entity -> {
          entity.generateId();
          entity.createdAt = Calendar.getInstance().getTime();
          entity.updatedAt = entity.createdAt;
        });

    return collection
        .insertMany(mongoSession.unWrap(), entities, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(InsertManyResult::from);
  }
  // endregion

  // region Replace One
  public static <T extends BaseEntity> Uni<UpdateResult> replaceOne(
      ReactiveMongoCollection<T> collection, Bson filter, T entity) {

    entity.addDates();
    return collection
        .replaceOne(filter, entity)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }

  public static <T extends BaseEntity> Uni<UpdateResult> replaceOne(
      ReactiveMongoCollection<T> collection, Bson filter, T entity, ReplaceOptions options) {

    entity.addDates();
    return collection
        .replaceOne(filter, entity, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }

  public static <T extends BaseEntity> Uni<UpdateResult> replaceOne(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, Bson filter, T entity) {

    entity.addDates();
    return collection
        .replaceOne(mongoSession.unWrap(), filter, entity)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }

  public static <T extends BaseEntity> Uni<UpdateResult> replaceOne(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      Bson filter,
      T entity,
      ReplaceOptions options) {

    entity.addDates();
    return collection
        .replaceOne(mongoSession.unWrap(), filter, entity, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }
  // endregion

  // region Update One
  public static <T extends BaseEntity> Uni<UpdateResult> updateOne(
      ReactiveMongoCollection<T> collection, Bson filter, Bson update) {

    update =
        Updates.combine(
            Updates.currentDate(T.FIELD_UPDATED_AT),
            Updates.setOnInsert(T.FIELD_CREATED_AT, Calendar.getInstance().getTime()),
            update);
    return collection
        .updateOne(filter, update)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }

  public static <T extends BaseEntity> Uni<UpdateResult> updateOne(
      ReactiveMongoCollection<T> collection, Bson filter, Bson update, UpdateOptions options) {

    update =
        Updates.combine(
            Updates.currentDate(T.FIELD_UPDATED_AT),
            Updates.setOnInsert(T.FIELD_CREATED_AT, Calendar.getInstance().getTime()),
            update);
    return collection
        .updateOne(filter, update, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }

  public static <T extends BaseEntity> Uni<UpdateResult> updateOne(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, Bson filter, Bson update) {

    update =
        Updates.combine(
            Updates.currentDate(T.FIELD_UPDATED_AT),
            Updates.setOnInsert(T.FIELD_CREATED_AT, Calendar.getInstance().getTime()),
            update);
    return collection
        .updateOne(mongoSession.unWrap(), filter, update)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }

  public static <T extends BaseEntity> Uni<UpdateResult> updateOne(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      Bson filter,
      Bson update,
      UpdateOptions options) {

    update =
        Updates.combine(
            Updates.currentDate(T.FIELD_UPDATED_AT),
            Updates.setOnInsert(T.FIELD_CREATED_AT, Calendar.getInstance().getTime()),
            update);
    return collection
        .updateOne(mongoSession.unWrap(), filter, update, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }
  // endregion

  // region Update Many
  public static <T extends BaseEntity> Uni<UpdateResult> updateMany(
      ReactiveMongoCollection<T> collection, Bson filter, Bson update) {

    update =
        Updates.combine(
            Updates.currentDate(T.FIELD_UPDATED_AT),
            Updates.setOnInsert(T.FIELD_CREATED_AT, Calendar.getInstance().getTime()),
            update);
    return collection
        .updateMany(filter, update)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }

  public static <T extends BaseEntity> Uni<UpdateResult> updateMany(
      ReactiveMongoCollection<T> collection, Bson filter, Bson update, UpdateOptions options) {

    update =
        Updates.combine(
            Updates.currentDate(T.FIELD_UPDATED_AT),
            Updates.setOnInsert(T.FIELD_CREATED_AT, Calendar.getInstance().getTime()),
            update);
    return collection
        .updateMany(filter, update, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }

  public static <T extends BaseEntity> Uni<UpdateResult> updateMany(
      ReactiveMongoCollection<T> collection, MongoSession mongoSession, Bson filter, Bson update) {

    update =
        Updates.combine(
            Updates.currentDate(T.FIELD_UPDATED_AT),
            Updates.setOnInsert(T.FIELD_CREATED_AT, Calendar.getInstance().getTime()),
            update);
    return collection
        .updateMany(mongoSession.unWrap(), filter, update)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }

  public static <T extends BaseEntity> Uni<UpdateResult> updateMany(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      Bson filter,
      Bson update,
      UpdateOptions options) {

    update =
        Updates.combine(
            Updates.currentDate(T.FIELD_UPDATED_AT),
            Updates.setOnInsert(T.FIELD_CREATED_AT, Calendar.getInstance().getTime()),
            update);
    return collection
        .updateMany(mongoSession.unWrap(), filter, update, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }
  // endregion

  // region Update Array Push Pull
  public static <T, E extends BaseEntity> Uni<UpdateResult> push(
      ReactiveMongoCollection<E> collection, Bson filter, String fieldName, List<T> values) {

    UpdateOptions options = new UpdateOptions().upsert(true);
    Bson updates =
        combine(
            Updates.pushEach(fieldName, values, new PushOptions().position(0).sort(-1)),
            Updates.currentDate(E.FIELD_UPDATED_AT),
            Updates.setOnInsert(E.FIELD_CREATED_AT, Calendar.getInstance().getTime()));

    return collection
        .updateOne(filter, updates, options)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }

  public static <T> Uni<UpdateResult> pull(
      ReactiveMongoCollection<T> collection, Bson query, String field, String value) {

    Bson updates = Updates.pull(field, value);

    return collection
        .updateMany(query, updates)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }

  public static <T> Uni<UpdateResult> pull(
      ReactiveMongoCollection<T> collection,
      MongoSession mongoSession,
      Bson query,
      String field,
      String value) {

    Bson updates = Updates.pull(field, value);

    return collection
        .updateMany(mongoSession.unWrap(), query, updates)
        .emitOn(ExecutorRecorder.getCurrent())
        .onItem()
        .transform(UpdateResult::from);
  }
  // endregion
}
