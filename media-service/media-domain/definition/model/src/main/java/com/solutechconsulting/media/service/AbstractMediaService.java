/*
 *   Copyright 2023, Ray Elenteny
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 *   THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *   FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 *   DEALINGS IN THE SOFTWARE.
 *
 */

package com.solutechconsulting.media.service;

import com.solutechconsulting.media.model.Audio;
import com.solutechconsulting.media.model.Movie;
import com.solutechconsulting.media.model.TelevisionShow;
import io.reactivex.Flowable;
import org.eclipse.microprofile.metrics.*;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * This class exists to coordinate basic cross-cutting concerns for implementations of {@link
 * MediaService}. Understanding that a project is likely to have multiple implementations of the
 * same interface implies certain limitations on how "out-of-the-box" cross-cutting concerns are
 * handled. For example, while metrics annotations can be added to interfaces in Quarkus, those
 * annotations are not processed during runtime CDI injection. This is due to the way Quarkus
 * processes CDI, notably for native runtime capabilities. This class overcomes this issue by
 * providing the same functionality at runtime without violating Quarkus behavior.
 * <p>
 * The actual implementations of the MediaService interface thus implement the abstract "do" methods
 * defined in this class rather than the interface-defined methods. This allows this abstract class
 * to perform any additional cross-cutting functionality on behalf of the runtime and actual
 * implementations.
 */
public abstract class AbstractMediaService implements MediaService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

  private Timer getMoviesTimer;
  private Timer searchMoviesTimer;

  private Timer getAudioTimer;
  private Timer searchAudioTimer;
  private Timer getAudioTracksTimer;

  private Timer getTelevisionShowsTimer;
  private Timer searchTelevisionShowsTimer;
  private Timer getEpisodesTimer;
  private Timer getSeriesTimer;
  private MetricRegistry metricRegistry;

  @Inject
  public void setMetricRegistry(@RegistryType(type = MetricRegistry.Type.APPLICATION) MetricRegistry metricRegistry) {
    this.metricRegistry = metricRegistry;
  }

  @Override
  public Flowable<Movie> getMovies() {
    try {
      logger.debug("Invoking getMovies...");
      Flowable<Movie> flowable = getMoviesTimer.time(this::doGetMovies);
      logger.debug("getMovies complete.");
      return flowable;
    } catch (Exception e) {
      logger.error("Error in getMovies.", e);
      return Flowable.error(e);
    }
  }

  @Override
  public Flowable<Movie> searchMovies(String movieText) {
    try {
      logger.debug("Invoking searchMovies... Movie text: {}", movieText);
      Flowable<Movie> flowable = searchMoviesTimer.time(() -> doSearchMovies(movieText));
      logger.debug("searchMovies complete.");
      return flowable;
    } catch (Exception e) {
      logger.error("Error in searchMovies.", e);
      return Flowable.error(e);
    }
  }

  @Override
  public Flowable<Audio> getAudio() {
    try {
      logger.debug("Invoking getAudio...");
      Flowable<Audio> flowable = getAudioTimer.time(this::doGetAudio);
      logger.debug("getAudio complete.");
      return flowable;
    } catch (Exception e) {
      logger.error("Error in getAudio.", e);
      return Flowable.error(e);
    }
  }

  @Override
  public Flowable<Audio> searchAudio(String audioText) {
    try {
      logger.debug("Invoking searchAudio... Audio text: {}", audioText);
      Flowable<Audio> flowable = searchAudioTimer.time(() -> doSearchAudio(audioText));
      logger.debug("searchAudio complete.");
      return flowable;
    } catch (Exception e) {
      logger.error("Error in searchAudio.", e);
      return Flowable.error(e);
    }
  }

  @Override
  public Flowable<Audio> getAudioTracks(String albumTitle) {
    try {
      logger.debug("Invoking getAudioTracks... Album title: {}", albumTitle);
      Flowable<Audio> flowable = getAudioTracksTimer.time(() -> doGetAudioTracks(albumTitle));
      logger.debug("getAudioTracks complete.");
      return flowable;
    } catch (Exception e) {
      logger.error("Error in getAudioTracks.", e);
      return Flowable.error(e);
    }
  }

  @Override
  public Flowable<TelevisionShow> getTelevisionShows() {
    try {
      logger.debug("Invoking getTelevisionShows...");
      Flowable<TelevisionShow> flowable = getTelevisionShowsTimer.time(this::doGetTelevisionShows);
      logger.debug("getTelevisionShows complete.");
      return flowable;
    } catch (Exception e) {
      logger.error("Error in getTelevisionShows.", e);
      return Flowable.error(e);
    }

  }

  @Override
  public Flowable<TelevisionShow> searchTelevisionShows(String showText) {
    try {
      logger.debug("Invoking searchTelevisionShows... Show text: {}", showText);
      Flowable<TelevisionShow> flowable =
          searchTelevisionShowsTimer.time(() -> doSearchTelevisionShows(showText));
      logger.debug("searchTelevisionShows complete.");
      return flowable;
    } catch (Exception e) {
      logger.error("Error in searchTelevisionShows.", e);
      return Flowable.error(e);
    }
  }

  @Override
  public Flowable<TelevisionShow> getEpisodes(String seriesTitle, int season) {
    try {
      logger.debug("Invoking getEpisodes... Series title: {}, Season: {}", seriesTitle, season);
      Flowable<TelevisionShow> flowable = getEpisodesTimer
          .time(() -> doGetEpisodes(seriesTitle, season));
      logger.debug("getEpisodes complete.");
      return flowable;
    } catch (Exception e) {
      logger.error("Error in getEpisodes.", e);
      return Flowable.error(e);
    }
  }

  @Override
  public Flowable<TelevisionShow> getSeries(String seriesTitle) {
    try {
      logger.debug("Invoking getSeries... Series title: {}", seriesTitle);
      Flowable<TelevisionShow> flowable = getSeriesTimer.time(() -> doGetSeries(seriesTitle));
      logger.debug("getSeries complete.");
      return flowable;
    } catch (Exception e) {
      logger.error("Error in getSeries.", e);
      return Flowable.error(e);
    }
  }

  protected abstract Flowable<Movie> doGetMovies();

  protected abstract Flowable<Movie> doSearchMovies(String movieText);

  protected abstract Flowable<Audio> doGetAudio();

  protected abstract Flowable<Audio> doGetAudioTracks(String albumTitle);

  protected abstract Flowable<Audio> doSearchAudio(String audioText);

  protected abstract Flowable<TelevisionShow> doGetTelevisionShows();

  protected abstract Flowable<TelevisionShow> doSearchTelevisionShows(String showText);

  protected abstract Flowable<TelevisionShow> doGetEpisodes(String seriesTitle, int season);

  protected abstract Flowable<TelevisionShow> doGetSeries(String seriesTitle);

  protected abstract String getMetricsPrefix();

  /**
   * Creates implementation specific metrics. This pattern supports establishing common metrics
   * across any implementation of MediaService choosing to extend from this abstract class. It
   * provides consistency in metrics naming and documentation.
   */
  @PostConstruct
  public void initialize() {
    logger.debug("Initializing service metrics...");

    String metricsPrefix = getMetricsPrefix();
    String name = metricsPrefix + '.' + MetricsDefinitions.GetMovies.TIMER_NAME;
    Metadata metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(MetricsDefinitions.GetMovies.TIMER_DESCRIPTION)
            .build();

    getMoviesTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MetricsDefinitions.SearchMovies.TIMER_NAME;
    metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(MetricsDefinitions.SearchMovies.TIMER_DESCRIPTION)
            .build();

    searchMoviesTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MetricsDefinitions.GetAudio.TIMER_NAME;
    metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(MetricsDefinitions.GetAudio.TIMER_DESCRIPTION)
            .build();

    getAudioTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MetricsDefinitions.SearchAudio.TIMER_NAME;
    metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(MetricsDefinitions.SearchAudio.TIMER_DESCRIPTION)
            .build();

    searchAudioTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MetricsDefinitions.GetAudioTracks.TIMER_NAME;
    metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(MetricsDefinitions.GetAudioTracks.TIMER_DESCRIPTION)
            .build();

    getAudioTracksTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MetricsDefinitions.GetTelevisionShows.TIMER_NAME;
    metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(
            MetricsDefinitions.GetTelevisionShows.TIMER_DESCRIPTION).build();

    getTelevisionShowsTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MetricsDefinitions.SearchTelevisionShows.TIMER_NAME;
    metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(
            MetricsDefinitions.SearchTelevisionShows.TIMER_DESCRIPTION).build();

    searchTelevisionShowsTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MetricsDefinitions.GetEpisodes.TIMER_NAME;
    metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(MetricsDefinitions.GetEpisodes.TIMER_DESCRIPTION)
            .build();

    getEpisodesTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MetricsDefinitions.GetSeries.TIMER_NAME;
    metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(MetricsDefinitions.GetSeries.TIMER_DESCRIPTION)
            .build();

    getSeriesTimer = metricRegistry.timer(metadata);

    logger.debug("Service metrics initialized.");
  }
}
