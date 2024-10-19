package org.kodelabs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class Producers {
  @Produces
  @Dependent
  Logger createLogger(InjectionPoint injectionPoint) {
    return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
  }
}
