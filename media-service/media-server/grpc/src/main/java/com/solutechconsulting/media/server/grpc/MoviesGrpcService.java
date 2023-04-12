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

package com.solutechconsulting.media.server.grpc;

import com.google.protobuf.Duration;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import com.solutechconsulting.media.model.Movie;
import com.solutechconsulting.media.model.protobuf.CommonProto;
import com.solutechconsulting.media.model.protobuf.MoviesGrpc;
import com.solutechconsulting.media.model.protobuf.MoviesProto;
import com.solutechconsulting.media.model.protobuf.MoviesProto.GrpcMovie;
import com.solutechconsulting.media.model.protobuf.MutinyMoviesGrpc;
import com.solutechconsulting.media.service.MediaService;
import io.quarkus.grpc.GrpcService;
import io.reactivex.Flowable;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.converters.multi.MultiRxConverters;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetadataBuilder;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.Timer;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the gRPC implementation of the {@link MediaService} movies methods as defined in {@link MoviesGrpc}. The class extends
 * and leverages the generated Mutiny base gRPC class.
 */
@GrpcService
public class MoviesGrpcService extends MutinyMoviesGrpc.MoviesImplBase {

  private static final Logger logger = LoggerFactory.getLogger(MoviesGrpcService.class.getName());

  private final ExecutorService executorService = Executors.newCachedThreadPool();

  @Inject
  MediaService mediaService;
  private MetricRegistry metricRegistry;

  private Timer getMoviesTimer;
  private Timer searchMoviesTimer;

  @Override
  @Transactional
  public Multi<GrpcMovie> get(Empty request) {
    logger.debug("Invoking get...");
    return convertMovieResults(mediaService.getMovies(), getMoviesTimer.time())
        .runSubscriptionOn(executorService);
  }

  @Override
  @Transactional
  public Multi<GrpcMovie> search(CommonProto.SearchRequest request) {
    logger.debug("Invoking search... Search text: {}", request.getSearchText());
    return convertMovieResults(mediaService.searchMovies(request.getSearchText()),
        searchMoviesTimer.time()).runSubscriptionOn(executorService);
  }

  protected Multi<GrpcMovie> convertMovieResults(Flowable<Movie> flowable,
      Timer.Context timerContext) {

    return Multi.createFrom()
        .converter(MultiRxConverters.fromFlowable(), flowable.map(
            this::mapMovie).doOnError(throwable -> {
          logger.error("An error occurred. Terminating stream.", throwable);
          timerContext.stop();
        }).doOnComplete(() -> {
              logger.debug("Movies stream complete.");
              timerContext.stop();
            }
        ));
  }

  protected GrpcMovie mapMovie(Movie movie) {
    MoviesProto.GrpcMovie.Builder builder =
        MoviesProto.GrpcMovie.newBuilder().setId(movie.getId()).setTitle(
            movie.getTitle()).setStudio(movie.getStudio()).setContentRating(
            movie.getContentRating()).setGenres(movie.getGenres()).setTagline(
            movie.getTagline()).setSummary(movie.getSummary()).setDirectors(
            movie.getDirectors()).setRoles(movie.getRoles());
    movie.getCriticsRating().ifPresent(builder::setCriticsRating);
    movie.getAudienceRating().ifPresent(builder::setAudienceRating);
    movie.getYear().ifPresent(builder::setYear);

    movie.getReleaseDate().ifPresent(releaseDate -> {
      Instant instant = releaseDate.atStartOfDay().toInstant(ZoneOffset.UTC);
      builder.setReleaseDate(
          Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(
              instant.getNano()));
    });

    builder.setDuration(
        Duration.newBuilder().setSeconds(movie.getDuration().getSeconds()).setNanos(
            movie.getDuration().getNano()));

    return builder.build();
  }

  /**
   * Creates implementation specific metrics. This pattern supports establishing common metrics across any implementation of
   * MediaService choosing to extend from this abstract class. It provides consistency in metrics naming and documentation.
   */
  public void initializeMetrics() {
    logger.debug("Initializing service metrics...");

    String metricsPrefix = MoviesGrpcService.class.getName();
    String name = metricsPrefix + '.' + MediaService.MetricsDefinitions.GetMovies.TIMER_NAME;
    Metadata metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(
            MediaService.MetricsDefinitions.GetMovies.TIMER_DESCRIPTION).build();

    getMoviesTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MediaService.MetricsDefinitions.SearchMovies.TIMER_NAME;
    metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(
            MediaService.MetricsDefinitions.SearchMovies.TIMER_DESCRIPTION).build();

    searchMoviesTimer = metricRegistry.timer(metadata);

    logger.debug("Service metrics initialized.");
  }

  @Inject
  public void setMetricRegistry(@RegistryType(type = MetricRegistry.Type.APPLICATION) MetricRegistry metricRegistry) {
    this.metricRegistry = metricRegistry;
  }

  @PostConstruct
  public void initialize() {
    initializeMetrics();
  }
}
