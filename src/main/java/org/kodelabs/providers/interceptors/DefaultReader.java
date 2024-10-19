package org.kodelabs.providers.interceptors;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.jboss.resteasy.reactive.server.spi.ResteasyReactiveResourceInfo;
import org.jboss.resteasy.reactive.server.spi.ServerMessageBodyReader;
import org.jboss.resteasy.reactive.server.spi.ServerRequestContext;
import org.kodelabs.entities.exceptions.BaseException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/** Created by Gentrit Gojani on 8/2/18. */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class DefaultReader<T> implements ServerMessageBodyReader<T> {
  @Inject public Gson gson;

  @Override
  public boolean isReadable(
      Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    String mainType = mediaType.getType();
    String subType = mediaType.getSubtype();
    return mediaType.getType().equals(MediaType.APPLICATION_JSON)
        || String.format("%s/%s", mainType, subType).equals(MediaType.APPLICATION_JSON);
  }

  @Override
  public T readFrom(
      Class<T> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders,
      InputStream entityStream)
      throws IOException, WebApplicationException {
    String json = new String(entityStream.readAllBytes(), StandardCharsets.UTF_8);

    T entity;
    try {
      entity = gson.fromJson(json, genericType);
    } catch (Exception e) {
      if (e instanceof JsonSyntaxException) throw new BaseException(400, e.getMessage());
      throw e;
    }

    return entity;
  }

  @Override
  public boolean isReadable(
      Class<?> type,
      Type genericType,
      ResteasyReactiveResourceInfo lazyMethod,
      MediaType mediaType) {
    String mainType = mediaType.getType();
    String subType = mediaType.getSubtype();
    return mediaType.getType().equals(MediaType.APPLICATION_JSON)
        || String.format("%s/%s", mainType, subType).equals(MediaType.APPLICATION_JSON);
  }

  @Override
  public T readFrom(
      Class<T> type, Type genericType, MediaType mediaType, ServerRequestContext context)
      throws WebApplicationException, IOException {
    String json = new String(context.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

    T entity;
    try {
      entity = gson.fromJson(json, genericType);
    } catch (Exception e) {
      if (e instanceof JsonSyntaxException) throw new BaseException(400, e.getMessage());
      throw e;
    }

    return entity;
  }
}
