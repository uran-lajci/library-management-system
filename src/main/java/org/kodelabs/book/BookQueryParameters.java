package org.kodelabs.book;

import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.*;

public class BookQueryParameters {
    @QueryParam("genres")
    Set<String> gender;

    @QueryParam("title")
    String title;

    @QueryParam("datePublished")
    Date datePublished;

    @QueryParam("languages")
    Set<String> languages;

    @QueryParam("startDate")
    Date startDate;

    @QueryParam("endDate")
    Date endDate;

    public Bson toBson() {
        List<Bson> filters = new ArrayList<>();

        if (title != null) {
            Bson filterTitle = eq(Book.FIELD_TITLE, title);
            filters.add(filterTitle);
        }
        if (datePublished != null) {
            Bson filterDatePublished = gt(Book.FIELD_DATE_PUBLISHED, datePublished);
            filters.add(filterDatePublished);
        }
        if (gender != null && gender.size() > 0) {
            Bson filterGenders = all(Book.FIELD_GENRES, gender);
            filters.add(filterGenders);
        }
        if (languages != null && languages.size() > 0) {
            Bson filterLanguages = all(Book.FIELD_LANGUAGES, languages);
            filters.add(filterLanguages);
        }
        if (startDate != null && endDate != null) {
            Bson filterStartDate = gt(Book.FIELD_DATE_PUBLISHED, startDate);
            Bson filterEndDate = lt(Book.FIELD_DATE_PUBLISHED, endDate);
            Bson filterStartEndDate = and(filterStartDate, filterEndDate);
            filters.add(filterStartEndDate);
        }

        return Updates.combine(filters);
    }
}
