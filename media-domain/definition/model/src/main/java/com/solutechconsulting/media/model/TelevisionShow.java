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

package com.solutechconsulting.media.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

/**
 * The TelevisionShow interface represents a single television show that may or may not be a part of
 * a series.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableTelevisionShow.class)
@JsonDeserialize(as = ImmutableTelevisionShow.class)
public interface TelevisionShow extends Media {

  /**
   * Return the television show series title.
   *
   * @return the television show series title
   */
  String getSeriesTitle();

  /**
   * Return the television show year first shown.
   *
   * @return the television show year first shown
   */
  Optional<Integer> getYear();

  /**
   * Return the television show series season number.
   *
   * @return the television show series season number
   */
  int getSeason();

  /**
   * Return the television show series episode number.
   *
   * @return the television show series episode number
   */
  int getEpisode();

  /**
   * Return the television show content rating (e.g. TV-G, TV-14, TV-MA, etc).
   *
   * @return the television show content rating
   */
  String getContentRating();

  /**
   * Return the television show summary.
   *
   * @return the television show summary
   */
  String getSummary();

  /**
   * Return the television show viewer rating.
   *
   * @return the television show viewer rating
   */
  Optional<Double> getRating();

  /**
   * Return the television show production studio.
   *
   * @return the television show production studio
   */
  String getStudio();

  /**
   * Return the television show date originally aired.
   *
   * @return the television show date originally aired
   */
  Optional<LocalDate> getOriginallyAired();

  /**
   * Return the television show duration.
   *
   * @return the television show duration
   */
  Duration getDuration();

  /**
   * Return the television show directors.
   *
   * @return the television show directors
   */
  String getDirectors();

  /**
   * Return the television show writers.
   *
   * @return the television show writers
   */
  String getWriters();
}
