package org.kodelabs.providers.filters;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

public class ResponseFilter {

  @ServerResponseFilter()
  public Uni<Void> filter(
      ContainerResponseContext responseContext, ContainerRequestContext containerRequestContext) {
    return Uni.createFrom().voidItem();
  }
}
