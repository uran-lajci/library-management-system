package org.kodelabs.providers.bean;

import org.jboss.resteasy.reactive.RestQuery;

import javax.ws.rs.DefaultValue;

public class AdditionalParamsWrapper {
  public static final String PAGE = "page";
  public static final String FROM = "from";
  public static final String FROM_TYPE = "fromType";

  @RestQuery public String fields;
  @RestQuery public String q;
  @RestQuery public String asc;
  @RestQuery public String dsc;

  @RestQuery
  @DefaultValue(value = "100")
  public int limit;

  @RestQuery
  @DefaultValue(value = "-1")
  public int page = -1;

  // region currently not used
  @RestQuery public String from;
  @RestQuery public String fromType;
  // endregion

  public AdditionalParamsWrapper() {}

  private AdditionalParamsWrapper(Builder builder) {
    fields = builder.fields;
    q = builder.q;
    asc = builder.asc;
    dsc = builder.dsc;
    limit = builder.limit;
    page = builder.page;
    from = builder.from;
    fromType = builder.fromType;
  }

  public static final class Builder {
    private String fields;
    private String q;
    private String asc;
    private String dsc;
    private int limit;
    private int page;
    private String from;
    private String fromType;

    public Builder() {}

    public Builder fields(String val) {
      fields = val;
      return this;
    }

    public Builder q(String val) {
      q = val;
      return this;
    }

    public Builder asc(String val) {
      asc = val;
      return this;
    }

    public Builder dsc(String val) {
      dsc = val;
      return this;
    }

    public Builder limit(int val) {
      limit = val;
      return this;
    }

    public Builder page(int val) {
      page = val;
      return this;
    }

    public Builder from(String val) {
      from = val;
      return this;
    }

    public Builder fromType(String val) {
      fromType = val;
      return this;
    }

    public AdditionalParamsWrapper build() {
      return new AdditionalParamsWrapper(this);
    }
  }
}
