package org.kodelabs.entities;

import org.bson.types.ObjectId;

import java.util.Calendar;
import java.util.Date;

public class BaseEntity {
  public static final transient String FIELD_ID = "_id";
  public static final transient String FIELD_CREATED_AT = "createdAt";
  public static final transient String FIELD_UPDATED_AT = "updatedAt";
  public static final transient String FIELD_CUSTOM_GENERATED = "customGenerated";
  public String _id;
  public Date createdAt;
  public Date updatedAt;
  private transient boolean customGenerated;

  public BaseEntity(String id) {
    this._id = id;
  }

  public BaseEntity() {}

  public void generateId() { // generate Id if its not _generated for insert
    if (!customGenerated) {
      customGenerated = true;
      this._id = new ObjectId().toString();
    }
  }

  public void generateId(String id) { // generate Id for insert
    customGenerated = true;
    this._id = id;
  }

  public void addDates() {
    try {
      updatedAt = Calendar.getInstance().getTime();
      ObjectId objectId = new ObjectId(_id);
      createdAt = objectId.getDate();
    } catch (Exception e) {
      if (updatedAt == null) {
        updatedAt = Calendar.getInstance().getTime();
      }
      if (createdAt == null) {
        createdAt = Calendar.getInstance().getTime();
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseEntity that = (BaseEntity) o;
    return _id != null ? _id.equals(that._id) : that._id == null;
  }

  @Override
  public int hashCode() {
    return _id != null ? _id.hashCode() : 0;
  }
}
