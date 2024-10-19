package org.kodelabs.pagination;

import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.FindOptions;
import org.bson.conversions.Bson;
import org.kodelabs.entities.exceptions.BadRequestException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.text;

public class PaginationQuery {
    @QueryParam("page")
    @DefaultValue("1")
    private Integer page;

    @QueryParam("limit")
    @DefaultValue("10")
    private Integer limit;

    @QueryParam("search")
    private String search;

    public Bson toBson() {
        List<Bson> bsonList = new ArrayList<>();
        if(search != null) {
            if (search.length() > 0) {
                Bson filterSearch = text(search);
                bsonList.add(filterSearch);
            }
            else {
                throw new BadRequestException("The search value should not be empty");
            }
        }

        return Updates.combine(bsonList);
    }

    public FindOptions toFindOptions() {
        int defaultLimit = 10;

        if (this.page == null && this.limit == null) {
            return new FindOptions().limit(defaultLimit);
        } else if (this.page == null) {
            if (this.limit <= 0)
                throw new BadRequestException("Limit should be greater than 0");
            else
                return new FindOptions().limit(this.limit);
        } else if (this.limit == null) {
            if (this.page <= 0)
                throw new BadRequestException("Page should be greater than 0");
            else
                return new FindOptions().skip((this.page - 1) * defaultLimit).limit(defaultLimit);
        } else {
            if (this.page <= 0 || this.limit <= 0)
                throw new BadRequestException("Page/Limit should be greater than 0");
            else
                return new FindOptions().skip((this.page - 1) * this.limit).limit(this.limit);
        }
    }

    public PaginationQuery() {
    }

    public Integer getPage() {
        return page;
    }

    public Integer getLimit() {
        return limit;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Integer page() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer limit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
