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

package com.solutechconsulting.media.service.mock.jpa;

import com.solutechconsulting.media.sample.AudioLoader;
import com.solutechconsulting.media.sample.MovieLoader;
import com.solutechconsulting.media.sample.TelevisionShowLoader;
import com.solutechconsulting.media.service.jpa.AudioEntity;
import com.solutechconsulting.media.service.jpa.MovieEntity;
import com.solutechconsulting.media.service.jpa.TelevisionShowEntity;
import io.quarkus.runtime.Startup;
import io.reactivex.schedulers.Schedulers;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@ApplicationScoped
public class SampleDatabaseLoader {

  private final Logger logger = LoggerFactory.getLogger(SampleDatabaseLoader.class.getName());

  @Inject
  EntityManager entityManager;

  @PostConstruct
  public void postLoad() {
    loadDatabase();
  }

  @Transactional
  protected void loadDatabase() {
    logger.info("Loading sample data...");

    // Per the way in which Quarkus interacts with the entity manager work needs to occur off the
    // main thread as it's considered blocking. However, since sample data is being loaded for
    // unit tests, each load call is using blockingSubscribe() to ensure data is loaded before
    // any tests are executed. It is a bit counterintuitive.

    logger.info("Loading movies...");
    MovieLoader movieLoader = new MovieLoader();
    movieLoader.loadMovies().subscribeOn(Schedulers.io())
        .map(MovieEntity::new)
        .doOnNext(movieEntity -> entityManager.persist(movieEntity))
        .doOnComplete(() -> logger.info("Movies loaded."))
        .blockingSubscribe();

    logger.info("Loading audio...");
    AudioLoader audioLoader = new AudioLoader();
    audioLoader.loadAudio().subscribeOn(Schedulers.io())
        .map(AudioEntity::new)
        .doOnNext(audioEntity -> entityManager.persist(audioEntity))
        .doOnComplete(() -> logger.info("Audio loaded."))
        .blockingSubscribe();

    logger.info("Loading television shows...");
    TelevisionShowLoader televisionShowLoader = new TelevisionShowLoader();
    televisionShowLoader.loadTelevisionShows()
        .subscribeOn(Schedulers.io())
        .map(TelevisionShowEntity::new)
        .doOnNext(showEntity -> entityManager.persist(showEntity))
        .doOnComplete(() -> logger.info("Television shows loaded loaded."))
        .blockingSubscribe();

    logger.info("Sample data loaded loaded.");
  }
}
