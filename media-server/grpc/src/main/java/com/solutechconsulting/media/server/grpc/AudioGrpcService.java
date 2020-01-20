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
import com.solutechconsulting.media.model.protobuf.CommonProto;
import com.solutechconsulting.media.service.MediaService;
import io.reactivex.Flowable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.streams.Pump;
import io.vertx.grpc.GrpcWriteStream;
import io.vertx.reactivex.FlowableHelper;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetadataBuilder;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the gRPC implementation of the {@link MediaService} audio methods as defined in {@link
 * AudioGrpc}. The class extends and leverages the generated Vert.x base gRPC class.
 */
public class AudioGrpcService extends AudioGrpc.AudioVertxImplBase {

  private static final Logger logger = LoggerFactory.getLogger(AudioGrpcService.class.getName());

  private Vertx vertx;

  private MediaService mediaService;
  private MetricRegistry metricRegistry;

  private Timer getAudioTimer;
  private Timer searchAudioTimer;
  private Timer getAudioTracksTimer;

  public AudioGrpcService(Vertx vertx, MediaService mediaService, MetricRegistry metricRegistry) {
    this.mediaService = mediaService;
    this.metricRegistry = metricRegistry;
    this.vertx = vertx;

    initializeMetrics();
  }

  @Override
  public void get(Empty request, GrpcWriteStream<AudioProto.GrpcAudio> response) {
    vertx.executeBlocking(promise -> {
      logger.debug("Invoking get...");
      streamAudioResults(mediaService.getAudio(), response, getAudioTimer.time());
      logger.debug("get stream has started.");
    }, AsyncResult::succeeded);
  }

  @Override
  public void search(CommonProto.SearchRequest request,
      GrpcWriteStream<AudioProto.GrpcAudio> response) {
    vertx.executeBlocking(promise -> {
      logger.debug("Invoking search... Search text: {}", request.getSearchText());
      streamAudioResults(mediaService.searchAudio(request.getSearchText()), response,
          searchAudioTimer.time());
      logger.debug("search stream has started.");
    }, AsyncResult::succeeded);
  }

  @Override
  public void tracks(AudioProto.TracksRequest request,
      GrpcWriteStream<AudioProto.GrpcAudio> response) {
    vertx.executeBlocking(promise -> {
      logger.debug("Invoking tracks... Album title: {}", request.getAlbumTitle());
      streamAudioResults(mediaService.getAudioTracks(request.getAlbumTitle()), response,
          getAudioTracksTimer.time());
      logger.debug("tracks stream has started.");
    }, AsyncResult::succeeded);
  }

  protected void streamAudioResults(Flowable<Audio> flowable,
      GrpcWriteStream<AudioProto.GrpcAudio> response,
      Timer.Context timerContext) {
    Pump pump =
        Pump.pump(FlowableHelper.toReadStream(flowable.doOnError(throwable -> {
          logger.error("An error occurred. Terminating stream.", throwable);
          timerContext.stop();
          response.end();
        }).doOnComplete(() -> {
          logger.debug("Audio stream complete.");
          timerContext.stop();
          response.end();
        }).map(audio -> {
          AudioProto.GrpcAudio.Builder builder =
              AudioProto.GrpcAudio.newBuilder().setId(audio.getId()).setTitle(
                  audio.getTitle()).setAlbumArtist(audio.getAlbumArtist()).setAlbum(
                  audio.getAlbum()).setTrackNumber(audio.getTrackNumber());

          audio.getArtist().ifPresent(builder::setArtist);

          builder.setDuration(
              Duration.newBuilder().setSeconds(audio.getDuration().getSeconds()).setNanos(
                  audio.getDuration().getNano()));

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
}
