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

package com.solutechconsulting.media.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

/**
 * The Movie interface represents a movie in the media library.
 */

@Value.Immutable
@JsonSerialize(as = ImmutableMovie.class)
@JsonDeserialize(as = ImmutableMovie.class)
public interface Movie extends Media {

  /**
   * Return the movie production studio.
   *
   * @return the movie production studio
   */
  String getStudio();

  /**
   * Return the movie content rating (e.g. G, PG-13, R, etc).
   *
   * @return the movie content rating
   */
  String getContentRating();

  /**
   * Return the movie year released.
   *
   * @return the movie year released
   */
  Optional<Integer> getYear();

  /**
   * Return the movie critics' rating.
   *
   * @return the movie critics' rating
   */
  Optional<Double> getCriticsRating();

  /**
   * Return the movie summary.
   *
   * @return the movie summary
   */
  String getSummary();

  /**
   * Return the movie date released.
   *
   * @return the movie date released
   */
  Optional<LocalDate> getReleaseDate();

  /**
   * Return the movie genres (e.g. Action, Adventure, Mystery, Sci-Fi, etc).
   *
   * @return the movie genres
   */
  String getGenres();

  /**
   * Return the movie tag line.
   *
   * @return the movie tag line
   */
  String getTagline();

  /**
   * Return the movie duration.
   *
   * @return the movie duration
   */
  Duration getDuration();

  /**
   * Return the movie directors.
   *
   * @return the movie directors
   */
  String getDirectors();

  /**
   * Return the movie actors.
   *
   * @return the movie actors
   */
  String getRoles();

  /**
   * Return the movie audience rating.
   *
   * @return the movie audience rating
   */
  Optional<Double> getAudienceRating();
}
