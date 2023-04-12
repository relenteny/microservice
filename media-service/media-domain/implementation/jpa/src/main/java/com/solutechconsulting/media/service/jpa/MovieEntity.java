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

import com.solutechconsulting.media.model.ImmutableMovie;
import com.solutechconsulting.media.model.Movie;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The entity class for movie media types.
 *
 * @see com.solutechconsulting.media.model.Movie
 */

@Entity
@Table(name = "movies")
public class MovieEntity extends MediaEntity {

  private String studio;
  private String contentRating;
  private Double criticsRating;
  private String summary;
  private LocalDate releaseDate;
  private String genres;
  private String tagline;
  private Duration duration;
  private String directors;
  private String roles;
  private Double audienceRating;

  public MovieEntity() {
  }

  /**
   * Given a {@link Movie}, build an entity.
   *
   * @param movie the movie from which the entity is to be derived.
   */
  public MovieEntity(Movie movie) {
    setId(movie.getId());
    setTitle(movie.getTitle());
    setStudio(movie.getStudio());
    movie.getYear().ifPresent(this::setYear);
    movie.getCriticsRating().ifPresent(this::setCriticsRating);
    setSummary(movie.getSummary());
    movie.getReleaseDate().ifPresent(this::setReleaseDate);
    setGenres(movie.getGenres());
    setTagline(movie.getTagline());
    setDuration(movie.getDuration());
    setDirectors(movie.getDirectors());
    setRoles(movie.getRoles());
    movie.getAudienceRating().ifPresent(this::setAudienceRating);
    setContentRating(movie.getContentRating());
  }

  /**
   * Create a {@link Movie} from the current entity.
   *
   * @return a {@link Movie} created from the entity
   */
  @Transient
  public Movie getMovie() {
    return ImmutableMovie.builder().id(getId()).title(getTitle()).studio(getStudio()).year(
        Optional.ofNullable(getYear())).criticsRating(Optional.ofNullable(getCriticsRating()))
        .summary(getSummary())
        .genres(
            getGenres()).tagline(getTagline()).duration(getDuration()).directors(getDirectors())
        .roles(
            getRoles()).audienceRating(Optional.ofNullable(getAudienceRating())).contentRating(
            getContentRating()).releaseDate(Optional.ofNullable(getReleaseDate())).build();
  }

  @Column(name = "studio")
  public String getStudio() {
    return studio;
  }

  public void setStudio(String studio) {
    this.studio = studio;
  }

  @Column(name = "critics_rating")
  public Double getCriticsRating() {
    return criticsRating;
  }

  public void setCriticsRating(Double criticsRating) {
    this.criticsRating = criticsRating;
  }

  @Column(name = "summary", length = 2048)
  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  @Column(name = "released")
  public LocalDate getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }

  @Column(name = "genres")
  public String getGenres() {
    return genres;
  }

  public void setGenres(String genres) {
    this.genres = genres;
  }

  @Column(name = "tagline")
  public String getTagline() {
    return tagline;
  }

  public void setTagline(String tagline) {
    this.tagline = tagline;
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

  @Column(name = "roles")
  public String getRoles() {
    return roles;
  }

  public void setRoles(String roles) {
    this.roles = roles;
  }

  @Column(name = "audience_rating")
  public Double getAudienceRating() {
    return audienceRating;
  }

  public void setAudienceRating(Double audienceRating) {
    this.audienceRating = audienceRating;
  }

  @Column(name = "content_rating")
  public String getContentRating() {
    return contentRating;
  }

  public void setContentRating(String contentRating) {
    this.contentRating = contentRating;
  }
}
