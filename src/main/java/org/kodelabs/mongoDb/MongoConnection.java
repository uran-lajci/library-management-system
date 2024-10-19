package org.kodelabs.mongoDb;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "database")
public interface MongoConnection {
  @WithName("name")
  String name();
}
