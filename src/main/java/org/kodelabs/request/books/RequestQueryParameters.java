package org.kodelabs.request.books;

import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Filters.lt;

public class RequestQueryParameters {
    @QueryParam("startDate")
    Date startDate;

    @QueryParam("endDate")
    Date endDate;

    @QueryParam("status")
    String status;

    @QueryParam("userId")
    String userId;

    public Bson toBson() {
        List<Bson> filters = new ArrayList<>();

        if (userId != null) {
            Bson filterByUserId = eq(Request.FIELD_USER_ID, this.userId);
            filters.add(filterByUserId);
        }
        if (status != null) {
            Bson filterTitle = eq(Request.FIELD_STATUS, this.status);
            filters.add(filterTitle);
        }
        if (startDate != null && endDate != null) {
            Bson filterStartDate = gt(Request.FIELD_START_DATE, this.startDate);
            Bson filterEndDate = lt(Request.FIELD_END_DATE, this.endDate);
            Bson filterStartEndDate = and(filterStartDate, filterEndDate);
            filters.add(filterStartEndDate);
        }
        if (startDate != null && endDate == null) {
            Bson filterStartDate = gt(Request.FIELD_START_DATE, this.startDate);
            filters.add(filterStartDate);
        }
        if (startDate == null && endDate != null) {
            Bson filterEndDate = lt(Request.FIELD_END_DATE, this.endDate);
            filters.add(filterEndDate);
        }

        return Updates.combine(filters);
    }
}
