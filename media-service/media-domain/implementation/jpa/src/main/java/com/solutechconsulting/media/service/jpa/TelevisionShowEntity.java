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

import com.solutechconsulting.media.model.ImmutableTelevisionShow;
import com.solutechconsulting.media.model.TelevisionShow;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The entity class for television show media types.
 *
 * @see com.solutechconsulting.media.model.TelevisionShow
 */
@Entity
@Table(name = "tv_shows")
public class TelevisionShowEntity extends MediaEntity {

  private String seriesTitle;
  private Integer season;
  private Integer episode;
  private String contentRating;
  private String summary;
  private Double rating;
  private String studio;
  private LocalDate originallyAired;
  private Duration duration;
  private String directors;
  private String writers;

  public TelevisionShowEntity() {
  }

  /**
   * Given a {@link TelevisionShow}, build an entity.
   *
   * @param televisionShow the television show from which the entity is to be derived.
   */
  public TelevisionShowEntity(TelevisionShow televisionShow) {
    setId(televisionShow.getId());
    setTitle(televisionShow.getTitle());
    setSeriesTitle(televisionShow.getSeriesTitle());
    televisionShow.getYear().ifPresent(this::setYear);
    setSeason(televisionShow.getSeason());
    setEpisode(televisionShow.getEpisode());
    setContentRating(televisionShow.getContentRating());
    setSummary(televisionShow.getSummary());
    televisionShow.getRating().ifPresent(this::setRating);
    setStudio(televisionShow.getStudio());
    televisionShow.getOriginallyAired().ifPresent(this::setOriginallyAired);
    setDuration(televisionShow.getDuration());
    setDirectors(televisionShow.getDirectors());
    setWriters(televisionShow.getWriters());
  }

  /**
   * Create a {@link TelevisionShow} from the current entity.
   *
   * @return a {@link TelevisionShow} from the entity
   */
  @Transient
  public TelevisionShow getTelevisionShow() {
    return ImmutableTelevisionShow.builder().id(getId()).title(getTitle())
        .seriesTitle(getSeriesTitle()).season(
            getSeason()).episode(getEpisode()).contentRating(getContentRating())
        .summary(getSummary()).studio(
            getStudio()).duration(getDuration()).directors(getDirectors()).writers(getWriters())
        .year(
            Optional.ofNullable(getYear())).rating(Optional.ofNullable(getRating()))
        .originallyAired(
            Optional.ofNullable(getOriginallyAired())).build();
  }

  @Column(name = "series_title")
  public String getSeriesTitle() {
    return seriesTitle;
  }

  public void setSeriesTitle(String seriesTitle) {
    this.seriesTitle = seriesTitle;
  }

  @Column(name = "season")
  public Integer getSeason() {
    return season;
  }

  public void setSeason(Integer season) {
    this.season = season;
  }

  @Column(name = "episode")
  public Integer getEpisode() {
    return episode;
  }

  public void setEpisode(Integer episode) {
    this.episode = episode;
  }

  @Column(name = "content_rating")
  public String getContentRating() {
    return contentRating;
  }

  public void setContentRating(String contentRating) {
    this.contentRating = contentRating;
  }

  @Column(name = "summary", length = 8192)
  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  @Column(name = "rating")
  public Double getRating() {
    return rating;
  }

  public void setRating(Double rating) {
    this.rating = rating;
  }

  @Column(name = "studio")
  public String getStudio() {
    return studio;
  }

  public void setStudio(String studio) {
    this.studio = studio;
  }

  @Column(name = "originally_aired")
  public LocalDate getOriginallyAired() {
    return originallyAired;
  }

  public void setOriginallyAired(LocalDate originallyAired) {
    this.originallyAired = originallyAired;
  }

  @Column(name = "duration")
  public Duration getDuration() {
    return duration;
  }

  public void setDuration(Duration duration) {
    this.duration = duration;
  }

  @Column(name = "directors")
  public String getDirectors() {
    return directors;
  }

  public void setDirectors(String directors) {
    this.directors = directors;
  }

  @Column(name = "writers")
  public String getWriters() {
    return writers;
  }

  public void setWriters(String writers) {
    this.writers = writers;
  }
}
