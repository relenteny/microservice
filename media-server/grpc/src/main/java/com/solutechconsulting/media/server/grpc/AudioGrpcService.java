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
import com.solutechconsulting.media.model.Audio;
import com.solutechconsulting.media.model.protobuf.AudioGrpc;
import com.solutechconsulting.media.model.protobuf.AudioProto;
import com.solutechconsulting.media.model.protobuf.AudioProto.GrpcAudio;
import com.solutechconsulting.media.model.protobuf.CommonProto;
import com.solutechconsulting.media.model.protobuf.MutinyAudioGrpc;
import com.solutechconsulting.media.service.MediaService;
import io.quarkus.grpc.GrpcService;
import io.reactivex.Flowable;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.converters.multi.MultiRxConverters;
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
 * Provides the gRPC implementation of the {@link MediaService} audio methods as defined in {@link AudioGrpc}. The class extends and
 * leverages the generated Mutiny base gRPC class.
 */
@GrpcService
public class AudioGrpcService extends MutinyAudioGrpc.AudioImplBase {

  private static final Logger logger = LoggerFactory.getLogger(AudioGrpcService.class.getName());

  private final ExecutorService executorService = Executors.newCachedThreadPool();

  @Inject
  MediaService mediaService;
  private MetricRegistry metricRegistry;

  private Timer getAudioTimer;
  private Timer searchAudioTimer;
  private Timer getAudioTracksTimer;

  @Override
  @Transactional
  public Multi<GrpcAudio> get(Empty request) {
    logger.debug("Invoking get...");
    return convertAudioResults(mediaService.getAudio(), getAudioTimer.time())
        .runSubscriptionOn(executorService);
  }

  @Override
  @Transactional
  public Multi<GrpcAudio> search(CommonProto.SearchRequest request) {
    logger.debug("Invoking search... Search text: {}", request.getSearchText());
    return convertAudioResults(mediaService.searchAudio(request.getSearchText()),
        searchAudioTimer.time()).runSubscriptionOn(executorService);
  }

  @Override
  @Transactional
  public Multi<GrpcAudio> tracks(AudioProto.TracksRequest request) {
    logger.debug("Invoking tracks... Album title: {}", request.getAlbumTitle());
    return convertAudioResults(mediaService.getAudioTracks(request.getAlbumTitle()),
        getAudioTracksTimer.time()).runSubscriptionOn(executorService);
  }

  protected Multi<GrpcAudio> convertAudioResults(Flowable<Audio> flowable,
      Timer.Context timerContext) {

    return Multi.createFrom()
        .converter(MultiRxConverters.fromFlowable(), flowable.map(
            this::mapAudio).doOnError(throwable -> {
          logger.error("An error occurred. Terminating stream.", throwable);
          timerContext.stop();
        }).doOnComplete(() -> {
              logger.debug("Audio stream complete.");
              timerContext.stop();
            }
        ));
  }

  protected GrpcAudio mapAudio(Audio audio) {
    AudioProto.GrpcAudio.Builder builder =
        AudioProto.GrpcAudio.newBuilder().setId(audio.getId()).setTitle(
            audio.getTitle()).setAlbumArtist(audio.getAlbumArtist()).setAlbum(
            audio.getAlbum()).setTrackNumber(audio.getTrackNumber());

    audio.getArtist().ifPresent(builder::setArtist);

    builder.setDuration(
        Duration.newBuilder().setSeconds(audio.getDuration().getSeconds()).setNanos(
            audio.getDuration().getNano()));

    return builder.build();
  }

  /**
   * Creates implementation specific metrics. This pattern supports establishing common metrics across any implementation of
   * MediaService choosing to extend from this abstract class. It provides consistency in metrics naming and documentation.
   */
  public void initializeMetrics() {
    logger.debug("Initializing service metrics...");

    String metricsPrefix = AudioGrpcService.class.getName();
    String name = metricsPrefix + '.' + MediaService.MetricsDefinitions.GetAudio.TIMER_NAME;
    Metadata metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(
            MediaService.MetricsDefinitions.GetAudio.TIMER_DESCRIPTION).build();

    getAudioTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MediaService.MetricsDefinitions.SearchAudio.TIMER_NAME;
    metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(
            MediaService.MetricsDefinitions.SearchAudio.TIMER_DESCRIPTION).build();

    searchAudioTimer = metricRegistry.timer(metadata);

    name = metricsPrefix + '.' + MediaService.MetricsDefinitions.GetAudioTracks.TIMER_NAME;
    metadata =
        new MetadataBuilder().withName(name).withDisplayName(name).withType(
            MetricType.TIMER).withDescription(
            MediaService.MetricsDefinitions.GetAudioTracks.TIMER_DESCRIPTION).build();

    getAudioTracksTimer = metricRegistry.timer(metadata);

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
