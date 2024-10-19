package org.kodelabs.utils;

import io.vertx.core.json.JsonArray;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public abstract class CommonUtils {

  public static Field findField(Class<?> clazz, String fieldName) {
    Class<?> current = clazz;
    do {
      try {
        return current.getDeclaredField(fieldName);
      } catch (Exception ignored) {
      }
    } while ((current = current.getSuperclass()) != null);
    return null;
  }

  public static boolean isNull(Object value) {
    return value == null;
  }

  public static boolean isBlank(String value) {
    return isNull(value) || value.trim().length() == 0;
  }

  public static boolean isBlank(Iterable<?> list) {
    return isNull(list) || !list.iterator().hasNext();
  }

  public static boolean isBlank(Collection<?> collection) {
    return isNull(collection) || collection.size() == 0;
  }

  public static boolean isBlank(Map map) {
    return isNull(map) || map.size() == 0;
  }

  public static boolean isBlank(Object[] list) {
    return isNull(list) || list.length == 0;
  }

  public static boolean isBlank(JsonArray permissions) {
    return isNull(permissions) || permissions.size() == 0;
  }

  public static boolean notNull(Object... values) {
    boolean notNull = true;
    for (Object value : values) if (value == null) notNull = false;
    return notNull;
  }

  public static boolean hasNulls(Object... values) {
    if (values == null) {
      return true;
    }
    for (Object value : values) {
      if (value == null) {
        return true;
      }
    }
    return false;
  }

  public static boolean nonNull(Object... values) {
    return !hasNulls(values);
  }

  public static boolean notBlank(String value) {
    return notNull(value) && value.trim().length() > 0;
  }

  public static String ifBlankDefault(String value, String defaultValue) {
    return isBlank(value) ? defaultValue : value;
  }

  public static String ifBlankEmpty(String value) {
    return isBlank(value) ? "" : value;
  }

  public static boolean notBlank(Collection<?> collection) {
    return notNull(collection) && collection.size() > 0;
  }

  public static boolean notBlank(Map collection) {
    return notNull(collection) && collection.size() > 0;
  }

  public static boolean notBlank(JsonArray permissions) {
    return notNull(permissions) && permissions.size() > 0;
  }

  public static boolean notBlank(Object[] array) {
    return notNull(array) && array.length > 0;
  }

  public static boolean equalLists(List<String> one, List<String> two) {
    if (one == null && two == null) {
      return true;
    }
    if (one == null || two == null || one.size() != two.size()) {
      return false;
    }
    // to avoid messing the order of the lists we will use a copy
    one = new ArrayList<>(one);
    two = new ArrayList<>(two);
    Collections.sort(one);
    Collections.sort(two);
    return one.equals(two);
  }

  public static int parseUnixTimeInSec(String unixTimeInSec) {
    return unixTimeInSec == null ? 0 : Integer.parseInt(unixTimeInSec);
  }

  public static int toSeconds(long milliseconds) {
    return (int) (milliseconds / 1000L);
  }

  public static long toMilliseconds(int seconds) {
    return seconds * 1000L;
  }

  public static int toInt(String value, int defaultValue) {
    return value == null ? defaultValue : Integer.parseInt(value);
  }

  public static float toFloat(String value, float defaultValue) {
    return value == null ? defaultValue : Float.parseFloat(value);
  }

  private static Set<Field> getAllFields(Class type) {
    Set<Field> fields = new HashSet<>();
    for (Field field : type.getDeclaredFields()) {
      field.setAccessible(true);
      if (!Modifier.isNative(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
        fields.add(field);
      }
    }
    if (type.getSuperclass() != null && type != Object.class) {
      fields.addAll(getAllFields(type.getSuperclass()));
    }
    return fields;
  }

  public static Set<String> getAllFieldsWithAnnotation(Class modelClass, Class annotationClass) {
    Set<String> set = new HashSet<>();
    Set<Field> fieldSet = getAllFields(modelClass);
    for (Field field : fieldSet) {
      if (field.isAnnotationPresent(annotationClass)) {
        Set<String> indexedFields;
        if ((List.class).isAssignableFrom(field.getType())) {
          ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
          Class<?> aClass = (Class<?>) stringListType.getActualTypeArguments()[0];
          indexedFields = getAllFieldsWithAnnotation(aClass, annotationClass);
        } else {
          indexedFields = getAllFieldsWithAnnotation(field.getType(), annotationClass);
        }
        set.add(field.getName());
        if (indexedFields.size() == 0) {
          // set.add(field.getName());
        } else {
          indexedFields.forEach(
              indexedField -> set.add(String.join(".", field.getName(), indexedField)));
        }
      }
    }
    return set;
  }
}
