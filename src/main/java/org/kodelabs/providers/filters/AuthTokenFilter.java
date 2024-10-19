package org.kodelabs.providers.filters;

import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
// TODO: 4/21/2021 - by @shqiprimbunjaku: this will be in conflict since it uses authorization
// header

public class AuthTokenFilter {

  @ServerRequestFilter()
  public Uni<Void> filter(ContainerRequestContext requestContext, ResourceInfo resourceInfo) {
    return Uni.createFrom().voidItem();
  }
}
