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

package com.solutechconsulting.media.service.jpa;

import com.solutechconsulting.media.model.Audio;
import com.solutechconsulting.media.model.ImmutableAudio;
import java.time.Duration;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The entity class for audio media types.
 *
 * @see com.solutechconsulting.media.model.Audio
 */
@Entity
@Table(name = "audio")
public class AudioEntity extends MediaEntity {

  private String albumArtist;
  private String album;
  private String artist;
  private Integer trackNumber;
  private Duration duration;

  public AudioEntity() {
  }

  /**
   * Given an {@link Audio} media item, build an entity.
   *
   * @param audio the audio media item from which the entity is to be derived.
   */
  public AudioEntity(Audio audio) {
    setId(audio.getId());
    setTitle(audio.getTitle());
    setAlbumArtist(audio.getAlbumArtist());
    setAlbum(audio.getAlbum());
    audio.getArtist().ifPresent(this::setArtist);
    setTrackNumber(audio.getTrackNumber());
    setDuration(audio.getDuration());
    audio.getYear().ifPresent(this::setYear);
  }

  /**
   * Create an {@link Audio} media item from the current entity.
   *
   * @return an {@link Audio} media item created from the entity
   */
  @Transient
  public Audio getAudio() {
    return ImmutableAudio.builder().id(getId()).title(getTitle()).albumArtist(getAlbumArtist())
        .album(
            getAlbum()).trackNumber(getTrackNumber()).duration(getDuration())
        .year(Optional.ofNullable(getYear())).artist(
            Optional.ofNullable(getArtist())).build();
  }

  @Column(name = "album_artist")
  public String getAlbumArtist() {
    return albumArtist;
  }

  public void setAlbumArtist(String albumArtist) {
    this.albumArtist = albumArtist;
  }

  @Column(name = "album")
  public String getAlbum() {
    return album;
  }

  public void setAlbum(String album) {
    this.album = album;
  }

  @Column(name = "artist")
  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  @Column(name = "track_number")
  public Integer getTrackNumber() {
    return trackNumber;
  }

  public void setTrackNumber(Integer trackNumber) {
    this.trackNumber = trackNumber;
  }

  @Column(name = "duration")
  public Duration getDuration() {
    return duration;
  }

  public void setDuration(Duration duration) {
    this.duration = duration;
  }
}
