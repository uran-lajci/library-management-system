package org.kodelabs.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.kodelabs.utils.DateUtils;

import javax.inject.Singleton;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Created by Gentrit Gojani on 2019-04-01. */
public class GsonConfig {
    private static final TypeAdapter<Boolean> booleanAsIntAdapter =
            new TypeAdapter<Boolean>() {
                @Override
                public void write(JsonWriter out, Boolean value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value);
                    }
                }

                @Override
                public Boolean read(JsonReader in) throws IOException {
                    JsonToken peek = in.peek();
                    switch (peek) {
                        case BOOLEAN:
                            return in.nextBoolean();
                        case NULL:
                            in.nextNull();
                            return null;
                        case NUMBER:
                            return in.nextInt() != 0;
                        case STRING:
                            return Boolean.parseBoolean(in.nextString());
                        default:
                            throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
                    }
                }
            };

    @Produces
    @Singleton
    public static Gson get() {
        return new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .registerTypeAdapter(LocalDate.class, LocalDateAdapter.getSerialization())
                .registerTypeAdapter(LocalDate.class, LocalDateAdapter.getDeserialization())
                .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
                .registerTypeAdapter(LocalDateTime.class, LocalDateTimeAdapter.getSerialization())
                .registerTypeAdapter(LocalDateTime.class, LocalDateTimeAdapter.getDeserialization())
                .setDateFormat(DateUtils.getDefaultDateFormat())
                //.setLenient() // shtim i setLenient ne true
                .create();
    }
}