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

package com.solutechconsulting.media.server.rest;

@SuppressWarnings("squid:S1075")
public final class ResourceDefinitions {

  public static final String MEDIA_RESOURCE_PATH = "/media";
  public static final String MEDIA_STREAM = "/stream";

  public static final String SEARCH_TEXT_PARAMETER = "searchText";

  public static final class Path {

    public static final class Common {

      public static final String ALL_PATH = "/";
      public static final String SEARCH_PATH = "/search";
      public static final String SEARCH_FULL_PATH =
          SEARCH_PATH + "/{" + SEARCH_TEXT_PARAMETER + "}";

      private Common() {
      }
    }

    public static final class Movies {

      public static final String PATH = MEDIA_RESOURCE_PATH + "/movies";
      public static final String STREAM_PATH = MEDIA_RESOURCE_PATH + MEDIA_STREAM + "/movies";

      private Movies() {
      }
    }

    public static final class Audio {

      public static final String PATH = MEDIA_RESOURCE_PATH + "/audio";
      public static final String STREAM_PATH = MEDIA_RESOURCE_PATH + MEDIA_STREAM + "/audio";

      public static final String TRACKS_PATH = "/tracks";
      public static final String ALBUM_TITLE_PARAMETER = "albumTitle";
      public static final String TRACKS_FULL_PATH =
          TRACKS_PATH + "/{" + ALBUM_TITLE_PARAMETER + "}";

      private Audio() {
      }
    }

    public static final class TelevisionShows {

      public static final String PATH = MEDIA_RESOURCE_PATH + "/shows";
      public static final String STREAM_PATH = MEDIA_RESOURCE_PATH + MEDIA_STREAM + "/shows";

      public static final String SERIES_PATH = "/series";
      public static final String SERIES_TITLE_PARAMETER = "seriesTitle";
      public static final String SEASON_PARAMETER = "season";
      public static final String EPISODES_FULL_PATH =
          SERIES_PATH + "/{" + SERIES_TITLE_PARAMETER + "}/{" + SEASON_PARAMETER + "}";
      public static final String SERIES_FULL_PATH =
          SERIES_PATH + "/{" + SERIES_TITLE_PARAMETER + "}";

      private TelevisionShows() {
      }
    }

    private Path() {
    }
  }

  public static final class Stream {

    public static final String END_OF_STREAM_COMMENT = "End of stream.";
    public static final String END_OF_STREAM_MARKER = "END_OF_STREAM";
    public static final String ERROR_COMMENT = "An error has occurred.";
    public static final String ERROR_MARKER = "ERROR_MARKER";

    public static final class Movies {

      public static final String EVENT_NAME = "movie";

      private Movies() {
      }
    }

    public static final class Audio {

      public static final String EVENT_NAME = "audio";

      private Audio() {
      }
    }

    public static final class TelevisionShows {

      public static final String EVENT_NAME = "shows";

      private TelevisionShows() {
      }
    }

    private Stream() {
    }
  }

  private ResourceDefinitions() {
  }
}
