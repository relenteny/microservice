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

package com.solutechconsulting.media.sample;

import java.time.Duration;
import java.util.StringTokenizer;

/**
 * Constants for the sample CSV resources.
 */

public final class SampleDefinitions {

  /**
   * Calculate duration from CSV duration columns
   *
   * @param duration the string representation of the duration in hh:mm:ss format.
   * @return the calculated Duration or null if parameter is empty or "N/A"
   */
  public static Duration getDuration(String duration) {
    if (duration != null && !duration.isEmpty() && !duration.equals("N/A")) {
      StringTokenizer tokenizer = new StringTokenizer(duration, ":");
      return Duration.ofHours(Long.parseLong(tokenizer.nextToken())).plus(
          Duration.ofMinutes(Integer.parseInt(tokenizer.nextToken()))).plus(
          Duration.ofSeconds(Integer.parseInt(tokenizer.nextToken())));
    }

    return Duration.ofSeconds(0);
  }

  public static final class Movies {

    public static final String SAMPLE_RESOURCE = "/movies.csv";

    public static final class Columns {

      public static final String MEDIA_ID = "Media ID";
      public static final String TITLE = "Title";
      public static final String STUDIO = "Studio";
      public static final String CONTENT_RATING = "Content Rating";
      public static final String YEAR = "Year";
      public static final String RATING = "Rating";
      public static final String SUMMARY = "Summary";
      public static final String GENRES = "Genres";
      public static final String TAGLINE = "Tagline";
      public static final String RELEASE_DATE = "Release Date";
      public static final String DURATION = "Duration";
      public static final String DIRECTORS = "Directors";
      public static final String ROLES = "Roles";
      public static final String AUDIENCE = "Audience Rating";

      private Columns() {
      }
    }

    private Movies() {
    }

  }

  public static final class TelevisionShow {

    public static final String SAMPLE_RESOURCE = "/tv.csv";

    public static final class Columns {

      public static final String MEDIA_ID = "Media ID";
      public static final String SERIES_TITLE = "Series Title";
      public static final String EPISODE_TITLE = "Episode Title";
      public static final String YEAR = "Year";
      public static final String EPISODE = "Episode";
      public static final String CONTENT_RATING = "Content Rating";
      public static final String SEASON = "Season";
      public static final String RATING = "Rating";
      public static final String SUMMARY = "Summary";
      public static final String STUDIO = "Studio";
      public static final String ORIGINALLY_AIRED = "Originally Aired";
      public static final String DIRECTORS = "Directors";
      public static final String WRITERS = "Writers";
      public static final String DURATION = "Duration";

      private Columns() {
      }
    }

    private TelevisionShow() {
    }
  }

  public static final class Audio {

    public static final String SAMPLE_RESOURCE = "/audio.csv";

    public static final class Columns {

      public static final String MEDIA_ID = "Media ID";
      public static final String TITLE = "Title";
      public static final String ALBUM_ARTIST = "Album Artist";
      public static final String ALBUM = "Album";
      public static final String ARTIST = "Artist";
      public static final String TRACK_NUMBER = "Track No";
      public static final String DURATION = "Duration";
      public static final String YEAR = "Year";

      private Columns() {
      }
    }

    private Audio() {
    }
  }

  private SampleDefinitions() {
  }
}
