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

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.Vertx;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Currently, Quarkus doesn't directly support gRPC. However, given the Quarkus uses Vert.x as a
 * core technology, Vert.x support for gRPC is available. Using the Quarkus-defined AppLifecycleBean
 * pattern to manage process lifecycle events, this class instantiates the {@link MediaVerticle},
 * which in turn starts the gRPC services.
 */
@ApplicationScoped
public class ApplicationLifecycleBean {

  private static final Logger logger = LoggerFactory
      .getLogger(ApplicationLifecycleBean.class.getName());

  @Inject
  Vertx vertx;
  @Inject
  MediaVerticle mediaVerticle;

  /**
   * Instantiate, configure and start the {@link MediaVerticle}.
   *
   * @param startupEvent not used in this method
   */
  void onStart(@Observes StartupEvent startupEvent) {
    logger.info("The application is starting...");

    DeploymentOptions deploymentOptions = new DeploymentOptions();
    Optional<Integer> instances = mediaVerticle.getInstances();
    if (instances.isPresent()) {
      deploymentOptions.setInstances(instances.get());
    } else {
      vertx.deployVerticle(mediaVerticle);
    }
  }

  void onStop(@Observes ShutdownEvent shutdownEvent) {
    logger.info("The application is stopping...");
  }
}
