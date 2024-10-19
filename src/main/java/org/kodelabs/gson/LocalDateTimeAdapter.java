package org.kodelabs.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter {

    public static JsonDeserializer<LocalDateTime> getDeserialization() {
        return new LocalDateTimeAdapterDeserialization();
    }

    public static JsonSerializer<LocalDateTime> getSerialization() {
        return new LocalDateTimeAdapterSerialization();
    }

    private static class LocalDateTimeAdapterDeserialization
            implements JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(
                JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsJsonPrimitive().getAsString());
        }
    }

    private static class LocalDateTimeAdapterSerialization implements JsonSerializer<LocalDateTime> {

        public JsonElement serialize(
                LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_DATE_TIME));
        }
    }
}