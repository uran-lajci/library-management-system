package org.kodelabs.providers.interceptors;

import com.google.gson.Gson;
import org.jboss.resteasy.reactive.server.spi.ResteasyReactiveResourceInfo;
import org.jboss.resteasy.reactive.server.spi.ServerMessageBodyWriter;
import org.jboss.resteasy.reactive.server.spi.ServerRequestContext;
import org.kodelabs.entities.exceptions.BaseException;
import org.kodelabs.providers.interceptors.responses.ErrorResponse;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

/** Created by Gentrit Gojani on 8/2/18. */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class BaseExceptionWriter implements ServerMessageBodyWriter<BaseException> {
  @Inject public Gson gson;

  @Override
  public boolean isWriteable(
      Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return BaseException.class.isAssignableFrom(type);
  }

  @Override
  public void writeTo(
      BaseException entity,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream)
      throws IOException {
    httpHeaders.add(CONTENT_TYPE, MediaType.APPLICATION_JSON);
    entityStream.write(
        gson.toJson(new ErrorResponse(entity), ErrorResponse.class)
            .getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public boolean isWriteable(
      Class<?> type, Type genericType, ResteasyReactiveResourceInfo target, MediaType mediaType) {
    return BaseException.class.isAssignableFrom(type);
  }

  @Override
  public void writeResponse(BaseException o, Type genericType, ServerRequestContext context)
      throws WebApplicationException, IOException {
    context
        .getOrCreateOutputStream()
        .write(
            gson.toJson(new ErrorResponse(o), ErrorResponse.class)
                .getBytes(StandardCharsets.UTF_8));
  }
}
