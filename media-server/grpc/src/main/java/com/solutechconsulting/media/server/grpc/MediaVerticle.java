/*
 * Copyright 2020, Ray Elenteny
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.solutechconsulting.media.server.grpc;

import com.solutechconsulting.media.service.MediaService;
import io.vertx.core.Promise;
import io.vertx.grpc.VertxServerBuilder;
import io.vertx.reactivex.core.AbstractVerticle;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic verticle that instantiates a Vert.x gRPC server and attaches the {@link
 * MoviesGrpcService}, {@link AudioGrpcService}, and {@link TelevisionShowsGrpcService} services to
 * the server. The server is configured through application.properties via {@link
 * MediaVerticleConfiguration}.
 */
@ApplicationScoped
public class MediaVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(MediaVerticle.class.getName());

  private MetricRegistry metricRegistry;

  @Inject
  MediaService mediaService;

  @Inject
  MediaVerticleConfiguration verticleConfiguration;

  /**
   * Start and register the gRPC services.
   *
   * @param startPromise set to complete when the verticle has started
   * @throws Exception if an error occurs during startup.
   */
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    logger.info("Starting media verticle on host {} and port {}.", verticleConfiguration.getHost(),
        verticleConfiguration.getPort());
    VertxServerBuilder
        .forAddress(getVertx(), verticleConfiguration.getHost(), verticleConfiguration.getPort())
        .addService(new MoviesGrpcService(getVertx(), mediaService, metricRegistry))
        .addService(new AudioGrpcService(getVertx(), mediaService, metricRegistry))
        .addService(new TelevisionShowsGrpcService(getVertx(), mediaService, metricRegistry))
        .build()
        .start(event -> {
          startPromise.complete();
          logger.info("Media verticle started.");
        });
  }

  Optional<Integer> getInstances() {
    return verticleConfiguration.getInstances();
  }

  /**
   * Inject the registry for use by the services. The metric registry is available at the post
   * construct lifecycle event. The registry is passed to each service implementation to register
   * and log metrics.
   *
   * @param metricRegistry the application metric registry
   */
  @PostConstruct
  @Inject
  public void initialize(
      @RegistryType(type = MetricRegistry.Type.APPLICATION) MetricRegistry metricRegistry) {
    this.metricRegistry = metricRegistry;
  }
}
