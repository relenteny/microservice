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
import java.util.Optional;

/**
 * The Audio interface represents a single audio item such as a track on a CD.
 */
@Value.Immutable
@JsonSerialize(as = ImmutableAudio.class)
@JsonDeserialize(as = ImmutableAudio.class)
public interface Audio extends Media {

  /**
   * Return the audio item album artist.
   *
   * @return the album artist
   */
  String getAlbumArtist();

  /**
   * Return the audio item album.
   *
   * @return the album
   */
  String getAlbum();

  /**
   * For compilations, tracks may have artists designated that are separate from the album artist.
   * This method returns the separate artist.
   *
   * @return the track artist
   */
  Optional<String> getArtist();

  /**
   * Return the audio item track number.
   *
   * @return the track number
   */
  int getTrackNumber();

  /**
   * Return the audio item duration.
   *
   * @return the duration
   */
  Duration getDuration();

  /**
   * Return the audio item year published.
   *
   * @return the album artist year published
   */
  Optional<Integer> getYear();
}
