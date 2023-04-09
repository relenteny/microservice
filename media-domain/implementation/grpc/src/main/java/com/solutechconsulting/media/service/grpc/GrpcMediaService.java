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

package com.solutechconsulting.media.service.grpc;

import com.google.protobuf.Empty;
import com.solutechconsulting.media.model.Audio;
import com.solutechconsulting.media.model.ImmutableAudio;
import com.solutechconsulting.media.model.ImmutableMovie;
import com.solutechconsulting.media.model.ImmutableTelevisionShow;
import com.solutechconsulting.media.model.Movie;
import com.solutechconsulting.media.model.TelevisionShow;
import com.solutechconsulting.media.model.protobuf.AudioGrpc;
import com.solutechconsulting.media.model.protobuf.AudioProto;
import com.solutechconsulting.media.model.protobuf.AudioProto.GrpcAudio;
import com.solutechconsulting.media.model.protobuf.CommonProto;
import com.solutechconsulting.media.model.protobuf.MoviesGrpc;
import com.solutechconsulting.media.model.protobuf.MoviesProto;
import com.solutechconsulting.media.model.protobuf.MoviesProto.GrpcMovie;
import com.solutechconsulting.media.model.protobuf.TelevisionShowsGrpc;
import com.solutechconsulting.media.model.protobuf.TelevisionShowsProto;
import com.solutechconsulting.media.model.protobuf.TelevisionShowsProto.GrpcTelevisionShow;
import com.solutechconsulting.media.service.AbstractMediaService;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcClient;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A gRPC {@link com.solutechconsulting.media.service.MediaService} implementation intended for use by server-side or other Java
 * client applications. The implementation leverages the generated Vert.x-based gRPC/protobuf classes available in the
 * com.solutechconsulting.media:domain-protobuf module. The service is configured via <code>quarkus.grpc.clients</code> properties.
 */
@ApplicationScoped
@Alternative
@Priority(Interceptor.Priority.APPLICATION - 10)
@Named(GrpcMediaService.SERVICE_NAME)
public class GrpcMediaService extends AbstractMediaService {

  public static final String SERVICE_NAME = "GrpcMediaService";

  private final Logger logger = LoggerFactory.getLogger(GrpcMediaService.class.getName());

  @Inject
  @GrpcClient("mediaservice")
  Channel channel;

  @Override
  protected Flowable<Movie> doGetMovies() {
    Observable<MoviesProto.GrpcMovie> observable = Observable.create(emitter -> {
      try {
        Empty empty = Empty.getDefaultInstance();
        MoviesGrpc.newStub(channel).get(empty, new StreamObserver<>() {
          @Override
          public void onNext(GrpcMovie grpcMovie) {
            emitter.onNext(grpcMovie);
          }

          @Override
          public void onError(Throwable throwable) {
            logger.error("Error building gRPC doGetMovies stream.", throwable);
            emitter.onError(throwable);
          }

          @Override
          public void onCompleted() {
            logger.debug("gRPC doGetMovies stream complete.");
            emitter.onComplete();
          }
        });
      } catch (Exception e) {
        logger.error("Error building gRPC doGetMovies stream.", e);
        emitter.onError(e);
      }
    });

    return movieEventsToFlowable(observable);
  }

  @Override
  protected Flowable<Movie> doSearchMovies(String movieText) {
    Observable<MoviesProto.GrpcMovie> observable = Observable.create(emitter -> {
      try {
        MoviesGrpc.newStub(channel).search(
            CommonProto.SearchRequest.newBuilder().setSearchText(movieText).build(),
            new StreamObserver<>() {
              @Override
              public void onNext(GrpcMovie grpcMovie) {
                emitter.onNext(grpcMovie);
              }

              @Override
              public void onError(Throwable throwable) {
                logger.error("Error building gRPC doSearchMovies stream.", throwable);
                emitter.onError(throwable);
              }

              @Override
              public void onCompleted() {
                logger.debug("gRPC doSearchMovies stream complete.");
                emitter.onComplete();
              }
            });
      } catch (Exception e) {
        logger.error("Error building gRPC doSearchMovies stream.", e);
        emitter.onError(e);
      }
    });

    return movieEventsToFlowable(observable);
  }

  protected Flowable<Movie> movieEventsToFlowable(Observable<MoviesProto.GrpcMovie> observable) {
    return observable.toFlowable(BackpressureStrategy.BUFFER).map(grpcMovie -> {
      ImmutableMovie.Builder builder = ImmutableMovie.builder();
      builder.id(grpcMovie.getId()).title(grpcMovie.getTitle()).studio(grpcMovie.getStudio()).year(
              Optional.of(grpcMovie.getYear())).criticsRating(Optional.of(grpcMovie.getCriticsRating()))
          .summary(
              grpcMovie.getSummary()).genres(
              grpcMovie.getGenres()).tagline(grpcMovie.getTagline()).duration(
              Duration.ofSeconds(grpcMovie.getDuration().getSeconds(),
                  grpcMovie.getDuration().getNanos())).directors(grpcMovie.getDirectors()).roles(
              grpcMovie.getRoles()).audienceRating(Optional.of(grpcMovie.getAudienceRating()))
          .contentRating(
              grpcMovie.getContentRating());

      if (grpcMovie.getReleaseDate() != null) {
        Instant instant = Instant.ofEpochSecond(grpcMovie.getReleaseDate().getSeconds(),
            grpcMovie.getReleaseDate().getNanos());

        builder.releaseDate(instant.atZone(ZoneOffset.UTC).toLocalDate());
      }
      return builder.build();
    });
  }

  @Override
  protected Flowable<Audio> doGetAudio() {
    Observable<AudioProto.GrpcAudio> observable = Observable.create(emitter -> {
      try {
        Empty empty = Empty.getDefaultInstance();
        AudioGrpc.newStub(channel).get(empty, new StreamObserver<>() {
          @Override
          public void onNext(GrpcAudio grpcAudio) {
            emitter.onNext(grpcAudio);
          }

          @Override
          public void onError(Throwable throwable) {
            logger.error("Error building gRPC doGetAudio stream.", throwable);
            emitter.onError(throwable);
          }

          @Override
          public void onCompleted() {
            logger.debug("gRPC doGetAudio stream complete.");
            emitter.onComplete();
          }
        });
      } catch (Exception e) {
        logger.error("Error building gRPC doGetAudio stream.", e);
        emitter.onError(e);
      }
    });

    return audioEventsToFlowable((observable));
  }

  @Override
  protected Flowable<Audio> doGetAudioTracks(String albumTitle) {
    Observable<AudioProto.GrpcAudio> observable = Observable.create(emitter -> {
      try {
        AudioProto.TracksRequest tracksRequest = AudioProto.TracksRequest.newBuilder()
            .setAlbumTitle(
                albumTitle).build();
        AudioGrpc.newStub(channel).tracks(tracksRequest, new StreamObserver<>() {
          @Override
          public void onNext(GrpcAudio grpcAudio) {
            emitter.onNext(grpcAudio);
          }

          @Override
          public void onError(Throwable throwable) {
            logger.error("Error building gRPC doGetAudioTracks stream.", throwable);
            emitter.onError(throwable);
          }

          @Override
          public void onCompleted() {
            logger.debug("gRPC doGetAudioTracks stream complete.");
            emitter.onComplete();
          }
        });
      } catch (Exception e) {
        logger.error("Error building gRPC doGetAudioTracks stream.", e);
        emitter.onError(e);
      }
    });

    return audioEventsToFlowable((observable));
  }

  @Override
  protected Flowable<Audio> doSearchAudio(String audioText) {
    Observable<AudioProto.GrpcAudio> observable = Observable.create(emitter -> {
      try {
        CommonProto.SearchRequest searchRequest =
            CommonProto.SearchRequest.newBuilder().setSearchText(audioText).build();
        AudioGrpc.newStub(channel).search(searchRequest, new StreamObserver<>() {
          @Override
          public void onNext(GrpcAudio grpcAudio) {
            emitter.onNext(grpcAudio);
          }

          @Override
          public void onError(Throwable throwable) {
            logger.error("Error building doSearchAudio stream.", throwable);
            emitter.onError(throwable);
          }

          @Override
          public void onCompleted() {
            logger.debug("gRPC doSearchAudio stream complete.");
            emitter.onComplete();
          }
        });
      } catch (Exception e) {
        logger.error("Error building doSearchAudio stream.", e);
        emitter.onError(e);
      }
    });

    return audioEventsToFlowable((observable));
  }

  protected Flowable<Audio> audioEventsToFlowable(Observable<AudioProto.GrpcAudio> observable) {
    return observable.toFlowable(BackpressureStrategy.BUFFER)
        .map(grpcAudio -> ImmutableAudio.builder()
            .id(grpcAudio.getId()).title(grpcAudio.getTitle())
            .albumArtist(grpcAudio.getAlbumArtist()).album(
                grpcAudio.getAlbum()).artist(Optional.of(grpcAudio.getArtist()))
            .trackNumber(grpcAudio.getTrackNumber()).duration(
                Duration.ofSeconds(grpcAudio.getDuration().getSeconds(),
                    grpcAudio.getDuration().getNanos())).year(
                Optional.of(grpcAudio.getYear())).build());
  }

  @Override
  protected Flowable<TelevisionShow> doGetTelevisionShows() {
    Observable<TelevisionShowsProto.GrpcTelevisionShow> observable = Observable.create(emitter -> {
      try {
        Empty empty = Empty.getDefaultInstance();
        TelevisionShowsGrpc.newStub(channel).get(empty, new StreamObserver<>() {
          @Override
          public void onNext(GrpcTelevisionShow grpcTelevisionShow) {
            emitter.onNext(grpcTelevisionShow);
          }

          @Override
          public void onError(Throwable throwable) {
            logger.error("Error building gRPC doGetTelevisionShows stream.", throwable);
            emitter.onError(throwable);
          }

          @Override
          public void onCompleted() {
            logger.debug("gRPC doGetTelevisionShows stream complete.");
            emitter.onComplete();
          }
        });
      } catch (Exception e) {
        logger.error("Error building gRPC doGetTelevisionShows stream.", e);
        emitter.onError(e);
      }
    });

    return televisionShowEventsToFlowable(observable);
  }

  @Override
  protected Flowable<TelevisionShow> doSearchTelevisionShows(String showText) {
    Observable<TelevisionShowsProto.GrpcTelevisionShow> observable = Observable.create(emitter -> {
      try {
        CommonProto.SearchRequest searchRequest =
            CommonProto.SearchRequest.newBuilder().setSearchText(showText).build();
        TelevisionShowsGrpc.newStub(channel).search(searchRequest,
            new StreamObserver<>() {
              @Override
              public void onNext(GrpcTelevisionShow grpcTelevisionShow) {
                emitter.onNext(grpcTelevisionShow);
              }

              @Override
              public void onError(Throwable throwable) {
                logger.error("Error building doSearchTelevisionShows stream.", throwable);
                emitter.onError(throwable);
              }

              @Override
              public void onCompleted() {
                logger.debug("gRPC doSearchTelevisionShows stream complete.");
                emitter.onComplete();
              }
            });
      } catch (Exception e) {
        logger.error("Error building doSearchTelevisionShows stream.", e);
        emitter.onError(e);
      }
    });

    return televisionShowEventsToFlowable((observable));
  }

  @Override
  protected Flowable<TelevisionShow> doGetEpisodes(String seriesTitle, int season) {
    Observable<TelevisionShowsProto.GrpcTelevisionShow> observable = Observable.create(emitter -> {
      try {
        TelevisionShowsProto.EpisodesRequest episodesRequest =
            TelevisionShowsProto.EpisodesRequest.newBuilder().setSeriesTitle(seriesTitle).setSeason(
                season).build();
        TelevisionShowsGrpc.newStub(channel).episodes(episodesRequest,
            new StreamObserver<>() {
              @Override
              public void onNext(GrpcTelevisionShow grpcTelevisionShow) {
                emitter.onNext(grpcTelevisionShow);
              }

              @Override
              public void onError(Throwable throwable) {
                logger.error("Error building doGetEpisodes stream.", throwable);
                emitter.onError(throwable);
              }

              @Override
              public void onCompleted() {
                logger.debug("gRPC doGetEpisodes stream complete.");
                emitter.onComplete();
              }
            });
      } catch (Exception e) {
        logger.error("Error building doGetEpisodes stream.", e);
        emitter.onError(e);
      }
    });

    return televisionShowEventsToFlowable((observable));
  }

  @Override
  protected Flowable<TelevisionShow> doGetSeries(String seriesTitle) {
    Observable<TelevisionShowsProto.GrpcTelevisionShow> observable = Observable.create(emitter -> {
      try {
        TelevisionShowsProto.SeriesRequest seriesRequest =
            TelevisionShowsProto.SeriesRequest.newBuilder().setSeriesTitle(seriesTitle).build();
        TelevisionShowsGrpc.newStub(channel).series(seriesRequest,
            new StreamObserver<>() {
              @Override
              public void onNext(GrpcTelevisionShow grpcTelevisionShow) {
                emitter.onNext(grpcTelevisionShow);
              }

              @Override
              public void onError(Throwable throwable) {
                logger.error("Error building doGetSeries stream.", throwable);
                emitter.onError(throwable);
              }

              @Override
              public void onCompleted() {
                logger.debug("gRPC doGetSeries stream complete.");
                emitter.onComplete();
              }
            });
      } catch (Exception e) {
        logger.error("Error building doGetSeries stream.", e);
        emitter.onError(e);
      }
    });

    return televisionShowEventsToFlowable((observable));
  }

  protected Flowable<TelevisionShow> televisionShowEventsToFlowable(
      Observable<TelevisionShowsProto.GrpcTelevisionShow> observable) {
    return observable.toFlowable(BackpressureStrategy.BUFFER).map(grpcTelevisionShow -> {
      ImmutableTelevisionShow.Builder builder = ImmutableTelevisionShow.builder();
      builder.id(grpcTelevisionShow.getId()).title(grpcTelevisionShow.getTitle()).seriesTitle(
          grpcTelevisionShow.getSeriesTitle()).season(grpcTelevisionShow.getSeason()).episode(
          grpcTelevisionShow.getEpisode()).contentRating(
          grpcTelevisionShow.getContentRating()).summary(grpcTelevisionShow.getSummary()).studio(
          grpcTelevisionShow.getStudio()).directors(grpcTelevisionShow.getDirectors()).writers(
          grpcTelevisionShow.getWriters()).duration(
          Duration.ofSeconds(grpcTelevisionShow.getDuration().getSeconds(),
              grpcTelevisionShow.getDuration().getNanos())).year(
          Optional.of(grpcTelevisionShow.getYear())).rating(
          Optional.of(grpcTelevisionShow.getRating())).build();

      if (grpcTelevisionShow.getOriginallyAired() != null) {
        Instant instant = Instant
            .ofEpochSecond(grpcTelevisionShow.getOriginallyAired().getSeconds(),
                grpcTelevisionShow.getOriginallyAired().getNanos());

        builder.originallyAired(instant.atZone(ZoneOffset.UTC).toLocalDate());
      }

      return builder.build();
    });
  }

  @Override
  protected String getMetricsPrefix() {
    return GrpcMediaService.class.getName();
  }
}
