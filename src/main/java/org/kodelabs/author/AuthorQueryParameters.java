package org.kodelabs.author;

import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Filters.eq;

public class AuthorQueryParameters {
    @QueryParam("born")
    Date born;

    @QueryParam("died")
    Date died;

    @QueryParam("placeBorn")
    String placeBorn;

    @QueryParam("placeDied")
    String placeDied;

    @QueryParam("nationality")
    Nationality nationality;

    @QueryParam("currentProfession")
    String currentProfession;

    public Bson toBson() {
        List<Bson> filters = new ArrayList<>();

        if (placeBorn != null) {
            Bson filterPlaceBorn = eq(Author.FIELD_PLACE_BORN, placeBorn);
            filters.add(filterPlaceBorn);
        }
        if (placeDied != null) {
            Bson filterPlaceDied = eq(Author.FIELD_PLACE_DIED, placeDied);
            filters.add(filterPlaceDied);
        }
        if (born != null && died != null) {
            Bson filterBorn = gt(Author.FIELD_BORN, born);
            Bson filterDied = lt(Author.FIELD_DIED, died);
            Bson filterStartEndDate = and(filterBorn, filterDied);
            filters.add(filterStartEndDate);
        }
        if (nationality != null) {
            Bson filterNationality = eq(Author.FIELD_NATIONALITY, nationality.name());
            filters.add(filterNationality);
        }
        if (currentProfession != null) {
            Bson filterCurrentProfession = eq(Author.FIELD_CURRENT_PROFESSION, currentProfession);
            filters.add(filterCurrentProfession);
        }

        return Updates.combine(filters);
    }
}
