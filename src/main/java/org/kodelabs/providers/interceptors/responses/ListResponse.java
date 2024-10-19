package org.kodelabs.providers.interceptors.responses;

import java.util.ArrayList;
import java.util.List;

public class ListResponse<T> {

  public static final transient String FIELD_DATA = "data";
  public List<T> data;
  public String nextUrl;
  public Integer pageCount;
  public Long count;
  public Integer currentPage;

  public ListResponse() {
    data = new ArrayList<>();
  }

  public ListResponse(List<T> data) {
    this.data = data;
  }

  public ListResponse(List<T> data, String nextUrl) {
    this.data = data;
    this.nextUrl = nextUrl;
  }

  public ListResponse(List<T> data, String nextUrl, int pageCount, Long count) {
    this.data = data;
    this.nextUrl = nextUrl;
    this.pageCount = pageCount;
    this.count = count;
  }

  public ListResponse(List<T> data, String nextUrl, int pageCount, Long count, int currentPage) {
    this.data = data;
    this.nextUrl = nextUrl;
    this.pageCount = pageCount;
    this.count = count;
    this.currentPage = currentPage;
  }
}
