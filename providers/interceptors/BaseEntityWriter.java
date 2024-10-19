package org.kodelabs.providers.interceptors;

import com.google.gson.Gson;
import org.jboss.resteasy.reactive.server.spi.ResteasyReactiveResourceInfo;
import org.jboss.resteasy.reactive.server.spi.ServerMessageBodyWriter;
import org.jboss.resteasy.reactive.server.spi.ServerRequestContext;
import org.kodelabs.entities.BaseEntity;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/** Created by Gentrit Gojani on 8/2/18. */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class BaseEntityWriter implements ServerMessageBodyWriter<BaseEntity> {
  @Inject public Gson gson;
  @Inject UriInfo uriInfo;

  @Override
  public boolean isWriteable(
      Class<?> clazz, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return BaseEntity.class.isAssignableFrom(clazz);
  }

  @Override
  public void writeTo(
      BaseEntity entity,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream)
      throws IOException {
    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

    boolean isPost = false;
    String pathValue = null;

    if (annotations != null) {
      for (Annotation annotation : annotations) {

        if (annotation instanceof POST) {
          isPost = true;
        }

        if (annotation instanceof Path) {
          pathValue = ((Path) annotation).value();
        }
      }

      if (isPost & pathValue != null) {
        httpHeaders.add(
            HttpHeaders.LOCATION,
            uriInfo.getPath() + (uriInfo.getPath().endsWith("/") ? "" : "/") + entity._id);
      }
    }
    entityStream.write(gson.toJson(entity, type).getBytes("UTF-8"));
  }

  @Override
  public boolean isWriteable(
      Class<?> type, Type genericType, ResteasyReactiveResourceInfo target, MediaType mediaType) {
    return BaseEntity.class.isAssignableFrom(type);
  }

  @Override
  public void writeResponse(BaseEntity o, Type genericType, ServerRequestContext context)
      throws WebApplicationException, IOException {
    Annotation[] annotations = context.getResteasyReactiveResourceInfo().getAnnotations();
    boolean isPost = false;
    String pathValue = null;

    if (annotations != null) {
      for (Annotation annotation : annotations) {

        if (annotation instanceof POST) {
          isPost = true;
        }

        if (annotation instanceof Path) {
          pathValue = ((Path) annotation).value();
        }
      }

      if (isPost & pathValue != null) {
        context
            .serverResponse()
            .addResponseHeader(
                HttpHeaders.LOCATION,
                uriInfo.getPath() + (uriInfo.getPath().endsWith("/") ? "" : "/") + o._id);
      }
    }
    context.getOrCreateOutputStream().write(gson.toJson(o, genericType).getBytes("UTF-8"));
  }
}
