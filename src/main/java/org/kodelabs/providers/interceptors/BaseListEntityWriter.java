package org.kodelabs.providers.interceptors;

import com.google.gson.Gson;
import org.jboss.resteasy.reactive.common.util.types.ParameterizedTypeImpl;
import org.jboss.resteasy.reactive.server.spi.ResteasyReactiveResourceInfo;
import org.jboss.resteasy.reactive.server.spi.ServerMessageBodyWriter;
import org.jboss.resteasy.reactive.server.spi.ServerRequestContext;
import org.kodelabs.entities.BaseEntity;
import org.kodelabs.providers.interceptors.responses.ListResponse;
import org.kodelabs.utils.CommonUtils;

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
import java.util.List;
/** Created by Gentrit Gojani on 8/2/18. */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class BaseListEntityWriter implements ServerMessageBodyWriter<List<? extends BaseEntity>> {
  @Inject public Gson gson;

  @Override
  public boolean isWriteable(
      Class<?> clazz, Type genericType, Annotation[] annotations, MediaType mediaType) {
    if (genericType instanceof ParameterizedTypeImpl) {
      ParameterizedTypeImpl resteasyParameterizedType = (ParameterizedTypeImpl) genericType;
      Type[] actualTypeArguments = resteasyParameterizedType.getActualTypeArguments();

      if (CommonUtils.notBlank(actualTypeArguments)) { // check if list is list of _BaseEntity
        return BaseEntity.class.isAssignableFrom(
            (Class<?>) ((ParameterizedTypeImpl) genericType).getActualTypeArguments()[0]);
      }
    }
    return false;
  }

  @Override
  public void writeTo(
      List<? extends BaseEntity> entities,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream)
      throws IOException {
    entityStream.write(
        gson.toJson(new ListResponse<>(entities), ListResponse.class)
            .getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public boolean isWriteable(
      Class<?> type, Type genericType, ResteasyReactiveResourceInfo target, MediaType mediaType) {
    if (genericType instanceof ParameterizedTypeImpl) {
      ParameterizedTypeImpl resteasyParameterizedType = (ParameterizedTypeImpl) genericType;
      Type[] actualTypeArguments = resteasyParameterizedType.getActualTypeArguments();

      if (CommonUtils.notBlank(actualTypeArguments)) { // check if list is list of _BaseEntity
        return BaseEntity.class.isAssignableFrom(
            (Class<?>) ((ParameterizedTypeImpl) genericType).getActualTypeArguments()[0]);
      }
    }
    return false;
  }

  @Override
  public void writeResponse(
      List<? extends BaseEntity> o, Type genericType, ServerRequestContext context)
      throws WebApplicationException, IOException {
    context
        .getOrCreateOutputStream()
        .write(
            gson.toJson(new ListResponse<>(o), ListResponse.class)
                .getBytes(StandardCharsets.UTF_8));
  }
}
