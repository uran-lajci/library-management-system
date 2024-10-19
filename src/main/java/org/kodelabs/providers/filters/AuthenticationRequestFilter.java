package org.kodelabs.providers.filters;

import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerRequest;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.UriInfo;

public class AuthenticationRequestFilter {

  @ServerRequestFilter(priority = FilterPriorities.AUTHENTICATION)
  public Uni<Void> filter(
      ContainerRequestContext requestContext,
      UriInfo uriInfo,
      HttpServerRequest httpServerRequest) {
    return Uni.createFrom().voidItem();
  }
}
