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

import com.google.protobuf.Duration;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import com.solutechconsulting.media.model.TelevisionShow;
import com.solutechconsulting.media.model.protobuf.CommonProto;
import com.solutechconsulting.media.model.protobuf.TelevisionShowsGrpc;
import com.solutechconsulting.media.model.protobuf.TelevisionShowsProto;
import com.solutechconsulting.media.service.MediaService;
import io.reactivex.Flowable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.streams.Pump;
import io.vertx.grpc.GrpcWriteStream;
import io.vertx.reactivex.FlowableHelper;
import java.time.Instant;
import java.time.ZoneOffset;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetadataBuilder;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the gRPC implementation of the {@link MediaService} television shoes methods as defined
 * in {@link com.solutechconsulting.media.model.protobuf.TelevisionShowsGrpc}. The class extends and
 * leverages the generated Vert.x base gRPC class.
 */
public class TelevisionShowsGrpcService extends TelevisionShowsGrpc.TelevisionShowsVertxImplBase {

  private static final Logger logger = LoggerFactory
      .getLogger(TelevisionShowsGrpcService.class.getName());

  private Vertx vertx;

  private MediaService mediaService;
  private MetricRegistry metricRegistry;

  private Timer getTelevisionShowsTimer;
  private Timer searchTelevisionShowsTimer;
  private Timer getEpisodesTimer;
  private Timer getSeriesTimer;

  public TelevisionShowsGrpcService(Vertx vertx, MediaService mediaService,
      MetricRegistry metricRegistry) {
    this.mediaService = mediaService;
    this.metricRegistry = metricRegistry;
    this.vertx = vertx;

    initializeMetrics();
  }

  @Override
  public void get(Empty request,
      GrpcWriteStream<TelevisionShowsProto.GrpcTelevisionShow> response) {
    vertx.executeBlocking(promise -> {
      logger.debug("Invoking get...");
      streamTelevisionShowResults(mediaService.getTelevisionShows(), response,
          getTelevisionShowsTimer.time());
      logger.debug("get stream has started.");
    }, AsyncResult::succeeded);
  }

  @Override
  public void search(CommonProto.SearchRequest request,
      GrpcWriteStream<TelevisionShowsProto.GrpcTelevisionShow> response) {
    vertx.executeBlocking(promise -> {
      logger.debug("Invoking search... Search text: {}", request.getSearchText());
      streamTelevisionShowResults(mediaService.searchTelevisionShows(request.getSearchText()),
          response,
          searchTelevisionShowsTimer.time());
      logger.debug("search stream has started.");
    }, AsyncResult::succeeded);
  }

  @Override
  public void episodes(TelevisionShowsProto.EpisodesRequest request,
      GrpcWriteStream<TelevisionShowsProto.GrpcTelevisionShow> response) {
    vertx.executeBlocking(promise -> {
      logger.debug("Invoking episodes... Series: {}, Episode: {}", request.getSeriesTitle(),
          request.getSeason());
      streamTelevisionShowResults(
          mediaService.getEpisodes(request.getSeriesTitle(), request.getSeason()),
          response,
          getEpisodesTimer.time());
      logger.debug("episodes stream has started.");
    }, AsyncResult::succeeded);
  }

  @Override
  public void series(TelevisionShowsProto.SeriesRequest request,
      GrpcWriteStream<TelevisionShowsProto.GrpcTelevisionShow> response) {
    vertx.executeBlocking(promise -> {
      logger.debug("Invoking series... Series: {}", request.getSeriesTitle());
      streamTelevisionShowResults(mediaService.searchTelevisionShows(request.getSeriesTitle()),
          response,
          getSeriesTimer.time());
      logger.debug("series stream has started.");
    }, AsyncResult::succeeded);
  }

  protected void streamTelevisionShowResults(Flowable<TelevisionShow> flowable,
      GrpcWriteStream<TelevisionShowsProto.GrpcTelevisionShow> response,
      Timer.Context timerContext) {
    Pump pump =
        Pump.pump(FlowableHelper.toReadStream(flowable.doOnError(throwable -> {
          logger.error("An error occurred. Terminating stream.", throwable);
          timerContext.stop();
          response.end();
        }).doOnComplete(() -> {
          logger.debug("Television shows stream complete.");
          timerContext.stop();
          response.end();
        }).map(televisionShow -> {
          TelevisionShowsProto.GrpcTelevisionShow.Builder builder =
              TelevisionShowsProto.GrpcTelevisionShow.newBuilder().setId(televisionShow.getId())
                  .setTitle(
                      televisionShow.getTitle()).setSeriesTitle(
                  televisionShow.getSeriesTitle()).setSeason(
                  televisionShow.getSeason()).setEpisode(
                  televisionShow.getEpisode()).setContentRating(
                  televisionShow.getContentRating()).setSummary(
                  televisionShow.getSummary()).setStudio(televisionShow.getStudio()).setDirectors(
                  televisionShow.getDirectors()).setWriters(televisionShow.getWriters());

          televisionShow.getRating().ifPresent(builder::setRating);
          televisionShow.getYear().ifPresent(builder::setYear);

          televisionShow.getOriginallyAired().ifPresent(aired -> {
            Instant instant = aired.atStartOfDay().toInstant(ZoneOffset.UTC);
            builder.setOriginallyAired(
                Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(
                    instant.getNano()));
          });

          builder.setDuration(
              Duration.newBuilder().setSeconds(televisionShow.getDuration().getSeconds()).setNanos(
                  televisionShow.getDuration().getNano()));

          return builder.build();
        })), response);
    pump.start();
  }

  /**
   * Creates implementation specific metrics. This pattern supports establishing common metrics
   * across any implementation of MediaService choosing to extend from this abstract class. It
   * provides consistency in metrics naming and documentation.
   */
  public void initializeMetrics() {
    logger.debug("Initializing service metrics...");

    String metricsPrefix = TelevisionShowsGrpcService.class.getName();
    String name =
        metricsPrefix + '.' + MediaService.MetricsDefinitions.GetTelevisionShows.TIMER_NAME;
    Metadata metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(
            MediaService.MetricsDefinitions.GetTelevisionShows.TIMER_DESCRIPTION).build();

    getTelevisionShowsTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MediaService.MetricsDefinitions.SearchTelevisionShows.TIMER_NAME;
    metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(
            MediaService.MetricsDefinitions.SearchTelevisionShows.TIMER_DESCRIPTION).build();

    searchTelevisionShowsTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MediaService.MetricsDefinitions.GetEpisodes.TIMER_NAME;
    metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(
            MediaService.MetricsDefinitions.GetEpisodes.TIMER_DESCRIPTION).build();

    getEpisodesTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MediaService.MetricsDefinitions.GetSeries.TIMER_NAME;
    metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(
            MediaService.MetricsDefinitions.GetSeries.TIMER_DESCRIPTION).build();

    getSeriesTimer = metricRegistry.timer(metadata);

    logger.debug("Service metrics initialized.");
  }
}
