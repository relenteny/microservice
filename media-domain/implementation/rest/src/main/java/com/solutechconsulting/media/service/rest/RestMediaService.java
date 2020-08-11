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

package com.solutechconsulting.media.service.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solutechconsulting.media.model.Audio;
import com.solutechconsulting.media.model.Media;
import com.solutechconsulting.media.model.Movie;
import com.solutechconsulting.media.model.TelevisionShow;
import com.solutechconsulting.media.service.AbstractMediaService;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptor;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.SseEventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a RESTful {@link com.solutechconsulting.media.service.MediaService} implementation intended for
 * use by server-side or other Java client applications. The services is configured via {@link
 * RestMediaServiceStreamConfiguration}.
 */
@ApplicationScoped
@Alternative
@Priority(Interceptor.Priority.APPLICATION - 10)
@Named(RestMediaService.SERVICE_NAME)
@SuppressWarnings("CdiInjectionPointsInspection")
public class RestMediaService extends AbstractMediaService {

  public static final String SERVICE_NAME = "RestMediaService";

  private final Logger logger = LoggerFactory.getLogger(RestMediaService.class.getName());

  private Client client;
  private WebTarget getMoviesTarget;
  private WebTarget searchMoviesTarget;
  private WebTarget getAudioTarget;
  private WebTarget searchAudioTarget;
  private WebTarget tracksTarget;
  private WebTarget getShowsTarget;
  private WebTarget searchShowsTarget;
  private WebTarget seriesTarget;
  private WebTarget episodesTarget;

  @Inject
  RestMediaServiceUrlConfiguration serviceUrlConfiguration;

  @Inject
  RestMediaServiceCommonConfiguration serviceCommonConfiguration;

  @Inject
  RestMediaServiceMoviesConfiguration serviceMoviesConfiguration;

  @Inject
  RestMediaServiceAudioConfiguration serviceAudioConfiguration;

  @Inject
  RestMediaServiceShowsConfiguration serviceShowsConfiguration;

  @Inject
  RestMediaServiceStreamConfiguration serviceStreamConfiguration;

  @Inject
  ObjectMapper objectMapper;

  @Override
  protected Flowable<Movie> doGetMovies() {
    return getStreamResult(getMoviesTarget, new TypeReference<>() {
    });
  }

  @Override
  protected Flowable<Movie> doSearchMovies(String movieText) {
    return getStreamResult(
        searchMoviesTarget.resolveTemplate(Parameters.Common.SEARCH_TEXT, movieText),
        new TypeReference<>() {
        });
  }

  @Override
  protected Flowable<Audio> doGetAudio() {
    return getStreamResult(getAudioTarget, new TypeReference<>() {
    });
  }

  @Override
  protected Flowable<Audio> doGetAudioTracks(String albumTitle) {
    return getStreamResult(tracksTarget.resolveTemplate(Parameters.Audio.ALBUM_TITLE, albumTitle),
        new TypeReference<>() {
        });
  }

  @Override
  protected Flowable<Audio> doSearchAudio(String audioText) {
    return getStreamResult(
        searchAudioTarget.resolveTemplate(Parameters.Common.SEARCH_TEXT, audioText),
        new TypeReference<>() {
        });
  }

  @Override
  protected Flowable<TelevisionShow> doGetTelevisionShows() {
    return getStreamResult(getShowsTarget, new TypeReference<>() {
    });

  }

  @Override
  protected Flowable<TelevisionShow> doSearchTelevisionShows(String showText) {
    return getStreamResult(
        searchShowsTarget.resolveTemplate(Parameters.Common.SEARCH_TEXT, showText),
        new TypeReference<>() {
        });
  }

  @Override
  protected Flowable<TelevisionShow> doGetEpisodes(String seriesTitle, int season) {
    return getStreamResult(
        episodesTarget.resolveTemplate(Parameters.Shows.SERIES_TITLE, seriesTitle).resolveTemplate(
            Parameters.Shows.SEASON, season), new TypeReference<>() {
        });
  }

  @Override
  protected Flowable<TelevisionShow> doGetSeries(String seriesTitle) {
    return getStreamResult(
        seriesTarget.resolveTemplate(Parameters.Shows.SERIES_TITLE, seriesTitle),
        new TypeReference<>() {
        });
  }

  @Override
  protected String getMetricsPrefix() {
    return RestMediaService.class.getName();
  }

  protected <T extends Media> Flowable<T> getStreamResult(WebTarget target,
      TypeReference<T> typeReference) {
    SseEventSource eventSource = SseEventSource.target(target).build();
    Observable<T> observable = Observable.create(emitter -> {
      eventSource.register(sseEvent -> {
        if (sseEvent.getId().equals(serviceStreamConfiguration.getEndOfStreamMarker())) {
          eventSource.close();
          emitter.onComplete();
        } else {
          try {
            emitter.onNext(objectMapper.readValue(sseEvent.readData(), typeReference));
          } catch (IOException e) {
            logger.error("Error processing event stream.", e);
            eventSource.close();
            emitter.onError(e);
          }
        }
      });
      eventSource.open();
    });

    return observable.toFlowable(BackpressureStrategy.BUFFER);
  }

  protected StringBuilder getMediaStreamPath() {
    return new StringBuilder().append(serviceUrlConfiguration.getProtocol()).append("://").append(
        serviceUrlConfiguration.getHost()).append(':').append(
        serviceUrlConfiguration.getPort()).append(serviceCommonConfiguration.getMediaRoot()).append(
        serviceCommonConfiguration.getStream());
  }

  protected String getMoviesStreamPath() {
    return getMediaStreamPath().append(serviceMoviesConfiguration.getBase()).toString();
  }

  protected String searchMoviesStreamPath() {
    return getMediaStreamPath().append(serviceMoviesConfiguration.getBase()).append(
        serviceCommonConfiguration.getSearch()).append("/{").append(Parameters.Common.SEARCH_TEXT)
        .append(
            '}').toString();
  }

  protected String getAudioStreamPath() {
    return getMediaStreamPath().append(serviceAudioConfiguration.getBase()).toString();
  }

  protected String searchAudioStreamPath() {
    return getMediaStreamPath().append(serviceAudioConfiguration.getBase()).append(
        serviceCommonConfiguration.getSearch()).append("/{").append(Parameters.Common.SEARCH_TEXT)
        .append(
            '}').toString();
  }

  protected String tracksStreamPath() {
    return getMediaStreamPath().append(serviceAudioConfiguration.getBase()).append(
        serviceAudioConfiguration.getTracks()).append("/{").append(Parameters.Audio.ALBUM_TITLE)
        .append(
            '}').toString();
  }

  protected String getShowsStreamPath() {
    return getMediaStreamPath().append(serviceShowsConfiguration.getBase()).toString();
  }

  protected String searchShowsStreamPath() {
    return getMediaStreamPath().append(serviceShowsConfiguration.getBase()).append(
        serviceCommonConfiguration.getSearch()).append("/{").append(Parameters.Common.SEARCH_TEXT)
        .append(
            '}').toString();
  }

  protected String seriesStreamPath() {
    return getMediaStreamPath().append(serviceShowsConfiguration.getBase()).append(
        serviceShowsConfiguration.getSeries()).append("/{").append(Parameters.Shows.SERIES_TITLE)
        .append(
            '}').toString();
  }

  protected String episodesStreamPath() {
    return getMediaStreamPath().append(serviceShowsConfiguration.getBase()).append(
        serviceShowsConfiguration.getSeries()).append("/{").append(Parameters.Shows.SERIES_TITLE)
        .append(
            '}').append("/{").append(Parameters.Shows.SEASON).append('}').toString();
  }

  @PostConstruct
  protected void buildWebTargets() {
    client = ClientBuilder.newClient();

    getMoviesTarget = client.target(getMoviesStreamPath());
    searchMoviesTarget = client.target(searchMoviesStreamPath());
    getAudioTarget = client.target(getAudioStreamPath());
    searchAudioTarget = client.target(searchAudioStreamPath());
    tracksTarget = client.target(tracksStreamPath());
    getShowsTarget = client.target(getShowsStreamPath());
    searchShowsTarget = client.target(searchShowsStreamPath());
    seriesTarget = client.target(seriesStreamPath());
    episodesTarget = client.target(episodesStreamPath());
  }

  @PreDestroy
  public void cleanup() {
    logger.info("Closing HTTP client.");
    if (client != null) {
      client.close();
      logger.debug("HTTP client closed...");
    }
  }

  protected static final class Parameters {

    public static final class Common {

      public static final String SEARCH_TEXT = "searchText";

      private Common() {
      }
    }

    public static final class Audio {

      public static final String ALBUM_TITLE = "albumTitle";

      private Audio() {
      }
    }

    public static final class Shows {

      public static final String SERIES_TITLE = "seriesTitle";
      public static final String SEASON = "season";

      private Shows() {
      }
    }

    private Parameters() {
    }
  }
}
