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
import com.solutechconsulting.media.model.TelevisionShow;
import com.solutechconsulting.media.model.protobuf.CommonProto;
import com.solutechconsulting.media.model.protobuf.MutinyTelevisionShowsGrpc;
import com.solutechconsulting.media.model.protobuf.TelevisionShowsProto;
import com.solutechconsulting.media.model.protobuf.TelevisionShowsProto.GrpcTelevisionShow;
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
 * Provides the gRPC implementation of the {@link MediaService} television shoes methods as defined in
 * {@link com.solutechconsulting.media.model.protobuf.TelevisionShowsGrpc}. The class extends and leverages the generated Mutiny
 * base gRPC class.
 */
@GrpcService
public class TelevisionShowsGrpcService extends MutinyTelevisionShowsGrpc.TelevisionShowsImplBase {

  private static final Logger logger = LoggerFactory.getLogger(TelevisionShowsGrpcService.class.getName());

  @Inject
  MediaService mediaService;
  private MetricRegistry metricRegistry;

  private Timer getTelevisionShowsTimer;
  private Timer searchTelevisionShowsTimer;
  private Timer getEpisodesTimer;
  private Timer getSeriesTimer;

  private final ExecutorService executorService = Executors.newCachedThreadPool();

  @Override
  @Transactional
  public Multi<GrpcTelevisionShow> get(Empty request) {
    logger.debug("Invoking get...");
    return convertTelevisionShowResults(mediaService.getTelevisionShows(), getTelevisionShowsTimer.time()).runSubscriptionOn(
        executorService);
  }

  @Override
  @Transactional
  public Multi<GrpcTelevisionShow> search(CommonProto.SearchRequest request) {
    logger.debug("Invoking search... Search text: {}", request.getSearchText());
    return convertTelevisionShowResults(mediaService.searchTelevisionShows(request.getSearchText()),
        searchTelevisionShowsTimer.time()).runSubscriptionOn(executorService);
  }

  @Override
  @Transactional
  public Multi<GrpcTelevisionShow> episodes(TelevisionShowsProto.EpisodesRequest request) {
    logger.debug("Invoking episodes... Series: {}, Episode: {}", request.getSeriesTitle(), request.getSeason());
    return convertTelevisionShowResults(mediaService.getEpisodes(request.getSeriesTitle(), request.getSeason()),
        getEpisodesTimer.time()).runSubscriptionOn(executorService);
  }

  @Override
  @Transactional
  public Multi<GrpcTelevisionShow> series(TelevisionShowsProto.SeriesRequest request) {
    logger.debug("Invoking series... Series: {}", request.getSeriesTitle());
    return convertTelevisionShowResults(mediaService.getSeries(request.getSeriesTitle()), getSeriesTimer.time()).runSubscriptionOn(
        executorService);
  }

  protected Multi<GrpcTelevisionShow> convertTelevisionShowResults(Flowable<TelevisionShow> flowable, Timer.Context timerContext) {
    return Multi.createFrom()
        .converter(MultiRxConverters.fromFlowable(), flowable.map(this::mapTelevisionShow).doOnError(throwable -> {
          logger.error("An error occurred. Terminating stream.", throwable);
          timerContext.stop();
        }).doOnComplete(() -> {
          logger.debug("Television shows stream complete.");
          timerContext.stop();
        })).runSubscriptionOn(executorService);
  }

  protected GrpcTelevisionShow mapTelevisionShow(TelevisionShow televisionShow) {
    TelevisionShowsProto.GrpcTelevisionShow.Builder builder = TelevisionShowsProto.GrpcTelevisionShow.newBuilder()
        .setId(televisionShow.getId()).setTitle(televisionShow.getTitle()).setSeriesTitle(televisionShow.getSeriesTitle())
        .setSeason(televisionShow.getSeason()).setEpisode(televisionShow.getEpisode())
        .setContentRating(televisionShow.getContentRating()).setSummary(televisionShow.getSummary())
        .setStudio(televisionShow.getStudio()).setDirectors(televisionShow.getDirectors()).setWriters(televisionShow.getWriters());

    televisionShow.getRating().ifPresent(builder::setRating);
    televisionShow.getYear().ifPresent(builder::setYear);

    televisionShow.getOriginallyAired().ifPresent(aired -> {
      Instant instant = aired.atStartOfDay().toInstant(ZoneOffset.UTC);
      builder.setOriginallyAired(Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()));
    });

    builder.setDuration(Duration.newBuilder().setSeconds(televisionShow.getDuration().getSeconds())
        .setNanos(televisionShow.getDuration().getNano()));

    return builder.build();
  }

  /**
   * Creates implementation specific metrics. This pattern supports establishing common metrics across any implementation of
   * MediaService choosing to extend from this abstract class. It provides consistency in metrics naming and documentation.
   */
  public void initializeMetrics() {
    logger.debug("Initializing service metrics...");

    String metricsPrefix = TelevisionShowsGrpcService.class.getName();
    String name = metricsPrefix + '.' + MediaService.MetricsDefinitions.GetTelevisionShows.TIMER_NAME;
    Metadata metadata = new MetadataBuilder().withName(name).withDisplayName(name).withType(MetricType.TIMER)
        .withDescription(MediaService.MetricsDefinitions.GetTelevisionShows.TIMER_DESCRIPTION).build();

    getTelevisionShowsTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MediaService.MetricsDefinitions.SearchTelevisionShows.TIMER_NAME;
    metadata = new MetadataBuilder().withName(name).withDisplayName(name).withType(MetricType.TIMER)
        .withDescription(MediaService.MetricsDefinitions.SearchTelevisionShows.TIMER_DESCRIPTION).build();

    searchTelevisionShowsTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MediaService.MetricsDefinitions.GetEpisodes.TIMER_NAME;
    metadata = new MetadataBuilder().withName(name).withDisplayName(name).withType(MetricType.TIMER)
        .withDescription(MediaService.MetricsDefinitions.GetEpisodes.TIMER_DESCRIPTION).build();

    getEpisodesTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MediaService.MetricsDefinitions.GetSeries.TIMER_NAME;
    metadata = new MetadataBuilder().withName(name).withDisplayName(name).withType(MetricType.TIMER)
        .withDescription(MediaService.MetricsDefinitions.GetSeries.TIMER_DESCRIPTION).build();

    getSeriesTimer = metricRegistry.timer(metadata);

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
