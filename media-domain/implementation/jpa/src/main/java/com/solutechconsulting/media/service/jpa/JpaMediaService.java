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

package com.solutechconsulting.media.service.jpa;

import com.solutechconsulting.media.model.Audio;
import com.solutechconsulting.media.model.Movie;
import com.solutechconsulting.media.model.TelevisionShow;
import com.solutechconsulting.media.service.AbstractMediaService;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import java.util.stream.Stream;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@ActivateRequestContext
@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@Named(JpaMediaService.SERVICE_NAME)
public class JpaMediaService extends AbstractMediaService {

  public static final String SERVICE_NAME = "JpaMediaService";

  private final Logger logger = LoggerFactory.getLogger(JpaMediaService.class.getName());

  @Inject
  EntityManager entityManager;

  @Override
  @Transactional
  protected Flowable<Movie> doGetMovies() {
    return movieQueryToFlowable("SELECT m FROM MovieEntity m");
  }

  @Override
  @Transactional
  protected Flowable<Movie> doSearchMovies(String movieText) {
    String lcMovieText = "'%" + movieText.toLowerCase() + "%'";
    String queryString =
        "SELECT m FROM MovieEntity m WHERE lower(m.title) LIKE " + lcMovieText + " OR lower(m" +
            ".tagline) LIKE " + lcMovieText + " OR lower(m.summary) LIKE " + lcMovieText;

    logger.debug(queryString);

    return movieQueryToFlowable(queryString);
  }

  protected Flowable<Movie> movieQueryToFlowable(
      String queryString) {
    Observable<MovieEntity> observable = Observable.create(emitter -> {
      try {
        Stream<MovieEntity> showEntityStream = entityManager.createQuery(queryString,
            MovieEntity.class).getResultStream();
        showEntityStream.forEach(emitter::onNext);
        showEntityStream.close();
        emitter.onComplete();
      } catch (Exception e) {
        logger.error("Error building movie stream.", e);
        emitter.onError(e);
      }
    });

    return observable.toFlowable(BackpressureStrategy.BUFFER)
        .map(MovieEntity::getMovie);
  }

  @Override
  @Transactional
  protected Flowable<Audio> doGetAudio() {
    return audioQueryToFlowable("SELECT a FROM AudioEntity a");
  }

  @Override
  @Transactional
  protected Flowable<Audio> doSearchAudio(String audioText) {
    String lcAudioText = "'%" + audioText.toLowerCase() + "%'";
    String queryString =
        "SELECT a FROM AudioEntity a WHERE lower(a.title) LIKE " + lcAudioText
            + " OR lower(a.album) LIKE "
            + lcAudioText + " OR lower(a.albumArtist) LIKE " + lcAudioText
            + " OR lower(a.artist) LIKE "
            + lcAudioText;

    logger.debug(queryString);

    return audioQueryToFlowable(queryString);
  }

  @Override
  @Transactional
  protected Flowable<Audio> doGetAudioTracks(String albumTitle) {
    String lcAlbumTitle = albumTitle.toLowerCase();
    String queryString =
        "SELECT a FROM AudioEntity a WHERE lower(a.album) = '" + lcAlbumTitle + "'";

    logger.debug(queryString);

    return audioQueryToFlowable(queryString);
  }

  protected Flowable<Audio> audioQueryToFlowable(
      String queryString) {
    Observable<AudioEntity> observable = Observable.create(emitter -> {
      try {
        Stream<AudioEntity> showEntityStream = entityManager.createQuery(queryString,
            AudioEntity.class).getResultStream();
        showEntityStream.forEach(emitter::onNext);
        showEntityStream.close();
        emitter.onComplete();
      } catch (Exception e) {
        logger.error("Error building audio stream.", e);
        emitter.onError(e);
      }
    });

    return observable.toFlowable(BackpressureStrategy.BUFFER)
        .map(AudioEntity::getAudio);
  }

  @Override
  @Transactional
  protected Flowable<TelevisionShow> doGetTelevisionShows() {
    return showQueryToFlowable("SELECT s FROM TelevisionShowEntity s");
  }

  @Override
  @Transactional
  protected Flowable<TelevisionShow> doSearchTelevisionShows(String showText) {
    String lcShowText = "'%" + showText.toLowerCase() + "%'";
    String queryString =
        "SELECT s FROM TelevisionShowEntity s WHERE lower(s.title) LIKE " + lcShowText + "" +
            " OR lower(s.seriesTitle) LIKE " + lcShowText + " OR lower(s.summary) LIKE "
            + lcShowText;

    logger.debug(queryString);

    return showQueryToFlowable(queryString);
  }

  @Override
  @Transactional
  protected Flowable<TelevisionShow> doGetEpisodes(String seriesTitle, int season) {
    String queryString =
        "SELECT s FROM TelevisionShowEntity s WHERE lower(s.seriesTitle) = '" + seriesTitle
            .toLowerCase()
            + "' AND s.season = " + season;

    logger.debug(queryString);

    return showQueryToFlowable(queryString);
  }

  @Override
  @Transactional
  protected Flowable<TelevisionShow> doGetSeries(String seriesTitle) {
    String queryString =
        "SELECT s FROM TelevisionShowEntity s WHERE lower(s.seriesTitle) = '" + seriesTitle
            .toLowerCase() + "'";

    logger.debug(queryString);

    return showQueryToFlowable(queryString);
  }

  protected Flowable<TelevisionShow> showQueryToFlowable(
      String queryString) {
    Observable<TelevisionShowEntity> observable = Observable.create(emitter -> {
      try {
        Stream<TelevisionShowEntity> showEntityStream = entityManager.createQuery(queryString,
            TelevisionShowEntity.class).getResultStream();
        showEntityStream.forEach(emitter::onNext);
        showEntityStream.close();
        emitter.onComplete();
      } catch (Exception e) {
        logger.error("Error building television show stream.", e);
        emitter.onError(e);
      }
    });

    return observable.toFlowable(BackpressureStrategy.BUFFER)
        .map(TelevisionShowEntity::getTelevisionShow);
  }

  @Override
  protected String getMetricsPrefix() {
    return JpaMediaService.class.getName();
  }
}
