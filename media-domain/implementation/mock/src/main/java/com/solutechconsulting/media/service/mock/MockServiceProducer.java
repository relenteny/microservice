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

package com.solutechconsulting.media.service.mock;

import com.solutechconsulting.media.sample.AudioLoader;
import com.solutechconsulting.media.sample.MovieLoader;
import com.solutechconsulting.media.sample.TelevisionShowLoader;
import com.solutechconsulting.media.service.MediaService;
import com.solutechconsulting.media.service.jpa.AudioEntity;
import com.solutechconsulting.media.service.jpa.JpaMediaService;
import com.solutechconsulting.media.service.jpa.MovieEntity;
import com.solutechconsulting.media.service.jpa.TelevisionShowEntity;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
@Alternative
@Priority(Interceptor.Priority.APPLICATION + 10)
public class MockServiceProducer {

  private Logger logger = LoggerFactory.getLogger(MockServiceProducer.class.getName());

  @ConfigProperty(name = "mediaservice.mock.type", defaultValue = "simple")
  String serviceType;

  @Inject
  EntityManager entityManager;

  @Inject
  @Named(JpaMediaService.SERVICE_NAME)
  MediaService jpaMediaService;

  @Inject
  @Named(MockMediaService.SERVICE_NAME)
  MediaService mockMediaService;

  @Produces
  @ApplicationScoped
  public MediaService getMediaService() {
    if (serviceType.equals("jpa")) {
      loadDatabase();
      return jpaMediaService;
    }

    return mockMediaService;
  }

  @Transactional
  protected void loadDatabase() {
    logger.info("Loading sample data...");

    logger.info("Loading movies...");
    MovieLoader movieLoader = new MovieLoader();
    movieLoader.loadMovies().map(MovieEntity::new).subscribe(entityManager::persist).dispose();
    logger.info("Movies loaded.");

    logger.info("Loading audio...");
    AudioLoader audioLoader = new AudioLoader();
    audioLoader.loadAudio().map(AudioEntity::new).subscribe(entityManager::persist).dispose();
    logger.info("Audio loaded.");

    logger.info("Loading television shows...");
    TelevisionShowLoader televisionShowLoader = new TelevisionShowLoader();
    televisionShowLoader.loadTelevisionShows().map(TelevisionShowEntity::new).
        subscribe(entityManager::persist).dispose();
    logger.info("Television shows loaded loaded.");

    logger.info("Sample data loaded loaded.");
  }
}
