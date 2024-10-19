package org.kodelabs.providers.filters;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;

public class AuthorizationRequestFilter {

  @ServerRequestFilter(priority = FilterPriorities.AUTHORIZATION)
  public Uni<Void> filter(ContainerRequestContext requestContext, ResourceInfo resourceInfo) {
    return Uni.createFrom().voidItem();
  }
}
