package org.kodelabs.pagination;

import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.bson.conversions.Bson;

import java.util.List;

public class PageModel<T> {
    private List<T> data;
    private int itemCount;
    private int page;
    private int pageCount;

    public PageModel() {
    }

    public PageModel(List<T> data, int itemCount, int page, int pageCount) {
        this.data = data;
        this.itemCount = itemCount;
        this.page = page;
        this.pageCount = pageCount;
    }

    public static <T> Uni<PageModel<T>> mapToPageModel(ReactiveMongoCollection<T> collection, Bson bson, PaginationQuery paginationQuery) {
        Uni<Integer> itemCount = collection.countDocuments(Updates.combine(bson, paginationQuery.toBson())).map(Long::intValue);
        Uni<List<T>> list = collection.find(Updates.combine(bson, paginationQuery.toBson()), paginationQuery.toFindOptions()).collect().asList();

        return Uni.combine().all().unis(itemCount, list).combinedWith(
                (integer, list1) ->
                        new PageModel<>(list1, integer, paginationQuery.getPage(),
                                integer % paginationQuery.limit() == 0 ? (integer / paginationQuery.limit()) : (integer / paginationQuery.limit() + 1))
        );
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
