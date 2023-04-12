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

/**
 * The business service media interface. The service contains methods for interacting with data
 * sources used as media libraries.
 */
public interface MediaService {

  /**
   * Metrics constants defined for use by interface implementations
   */
  class MetricsDefinitions {

    public static final class GetMovies {

      public static final String TIMER_NAME = "getMoviesTimer";
      public static final String TIMER_DESCRIPTION = "Emits all movies stored in the media library.";

      private GetMovies() {
      }
    }

    public static final class SearchMovies {

      public static final String TIMER_NAME = "SearchMoviesTimer";
      public static final String TIMER_DESCRIPTION =
          "Perform a case insensitive text search of movies in the " +
              "media library.";

      private SearchMovies() {
      }
    }

    public static final class GetAudio {

      public static final String TIMER_NAME = "GetAudioTimer";
      public static final String TIMER_DESCRIPTION = "Emits all audio items stored in the media library.";

      private GetAudio() {
      }
    }

    public static final class SearchAudio {

      public static final String TIMER_NAME = "SearchAudioTimer";
      public static final String TIMER_DESCRIPTION =
          "Perform a case insensitive text search of audio items in " +
              "the media library.";

      private SearchAudio() {
      }
    }

    public static final class GetAudioTracks {

      public static final String TIMER_NAME = "GetAudioTracksTimer";
      public static final String TIMER_DESCRIPTION =
          "Given a case insensitive album title, return the " +
              "associated album tracks.";

      private GetAudioTracks() {
      }
    }

    public static final class GetTelevisionShows {

      public static final String TIMER_NAME = "GetTelevisionShows";
      public static final String TIMER_DESCRIPTION = "Emits all television shows stored in the media library.";

      private GetTelevisionShows() {
      }
    }

    public static final class SearchTelevisionShows {

      public static final String TIMER_NAME = "SearchTelevisionShowsTimer";
      public static final String TIMER_DESCRIPTION =
          "Perform a case insensitive text search of television " +
              "shows in the media library.";

      private SearchTelevisionShows() {
      }
    }

    public static final class GetEpisodes {

      public static final String TIMER_NAME = "GetEpisodesTimer";
      public static final String TIMER_DESCRIPTION =
          "Given a series title and season, return the television " +
              "show episodes from the media library.";

      private GetEpisodes() {
      }
    }

    public static final class GetSeries {

      public static final String TIMER_NAME = "GetSeriesTimer";
      public static final String TIMER_DESCRIPTION =
          "Given a series title, return television show episodes for" +
              " the entire series from the media library.";

      private GetSeries() {
      }
    }

    private MetricsDefinitions() {
    }
  }

  /**
   * Emits all movies stored in the media library.
   *
   * @return a back-pressure capable stream of all movies in the media library
   */
  Flowable<Movie> getMovies();

  /**
   * Perform a case insensitive text search of movies in the media library. The service will include
   * the title, summary and tag line attributes of movies in its search.
   *
   * @param movieText the text value used in searching movies
   * @return a back-pressure capable stream of movies matching the search criteria
   */
  Flowable<Movie> searchMovies(String movieText);

  /**
   * Emits all audio items stored in the media library.
   *
   * @return a back-pressure capable stream of all audio items in the media library
   */
  Flowable<Audio> getAudio();

  /**
   * Perform a case insensitive text search of audio items in the media library. The service will
   * include the song and album titles and the album artist(s) in its search.
   *
   * @param audioText the text value used in searching audio
   * @return a back-pressure capable stream of audio items matching the search criteria
   */
  Flowable<Audio> searchAudio(String audioText);

  /**
   * Given a case insensitive album title, return the associated album tracks.
   *
   * @param albumTitle the album title
   * @return a back-pressure capable stream of tracks associated with the album
   */
  Flowable<Audio> getAudioTracks(String albumTitle);

  /**
   * Emits all television shows stored in the media library.
   *
   * @return a back-pressure capable stream of all television shows in the media library
   */
  Flowable<TelevisionShow> getTelevisionShows();

  /**
   * Perform a case insensitive text search of television shows in the media library. The service
   * will include the series and shows titles and show summary in its search.
   *
   * @param showText the text value used in searching television shows
   * @return a back-pressure capable stream of television shows matching the search criteria
   */
  Flowable<TelevisionShow> searchTelevisionShows(String showText);

  /**
   * Given a series title and season, return the television show episodes from the media library.
   * Series title will be a case insensitive search.
   *
   * @param seriesTitle the television show series title
   * @param season      the television show series season number
   * @return a back-pressure capable stream of television shows matching the search criteria
   */
  Flowable<TelevisionShow> getEpisodes(String seriesTitle, int season);

  /**
   * Given a series title, return television show episodes for the entire series from the media
   * library. Series title will be a case insensitive search.
   *
   * @param seriesTitle the television show series title
   * @return a back-pressure capable stream of television shows matching the search criteria
   */
  Flowable<TelevisionShow> getSeries(String seriesTitle);
}
