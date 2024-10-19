package org.kodelabs.providers.filters.custom;

import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.kodelabs.providers.filters.FilterPriorities;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.UriInfo;

public class AuthenticationRequestFilter {

  @ServerRequestFilter(priority = FilterPriorities.AUTHENTICATION)
  public Uni<Void> filter(
      ContainerRequestContext requestContext,
      RoutingContext routingContext,
      UriInfo uriInfo,
      HttpServerRequest httpServerRequest) {
    return Uni.createFrom().voidItem();
  }
}
