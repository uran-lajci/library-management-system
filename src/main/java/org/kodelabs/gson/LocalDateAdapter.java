package org.kodelabs.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter {

    public static JsonDeserializer<LocalDate> getDeserialization() {
        return new LocalDateAdapterDeserialization();
    }

    public static JsonSerializer<LocalDate> getSerialization() {
        return new LocalDateAdapterSerialization();
    }

    private static class LocalDateAdapterDeserialization implements JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
        }
    }

    private static class LocalDateAdapterSerialization implements JsonSerializer<LocalDate> {

        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_DATE)); // "yyyy-mm-dd"
        }
    }
}