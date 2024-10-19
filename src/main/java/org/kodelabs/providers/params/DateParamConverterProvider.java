package org.kodelabs.providers.params;

import org.kodelabs.entities.exceptions.BadRequestException;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Created by Gentrit Gojani on 8/2/18. */
@Provider
public class DateParamConverterProvider implements ParamConverterProvider {

  @SuppressWarnings("unchecked")
  @Override
  public <T> ParamConverter<T> getConverter(
      Class<T> rawType, Type genericType, java.lang.annotation.Annotation[] annotations) {
    if (rawType.isAssignableFrom(Date.class)) {
      return (ParamConverter<T>) new DateParamConverter();
    }
    return null;
  }

  private static class DateParamConverter implements ParamConverter<Date> {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Override
    public Date fromString(String param) {
      try {
        return new SimpleDateFormat(DATE_PATTERN).parse(param.trim());
      } catch (ParseException e) {
        throw new BadRequestException(e.getMessage());
      }
    }

    @Override
    public String toString(Date date) {
      return new SimpleDateFormat(DATE_PATTERN).format(date);
    }
  }
}
