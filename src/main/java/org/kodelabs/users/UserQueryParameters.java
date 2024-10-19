package org.kodelabs.users;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserQueryParameters {
    @QueryParam("role")
    Role role;

    @QueryParam("dateCreated")
    Date dateCreated;

    public Bson toBson() {
        List<Bson> bsonList = new ArrayList<>();

        if (role != null) {
            Bson eq = Filters.eq(User.FIELD_ROLE, role.name());
            bsonList.add(eq);
        }
        if (dateCreated != null) {
            Bson gt = Filters.gt(User.FIELD_DATE_CREATED, dateCreated);
            bsonList.add(gt);
        }

        return Updates.combine(bsonList);
    }
}
