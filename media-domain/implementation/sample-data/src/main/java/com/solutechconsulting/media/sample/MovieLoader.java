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

package com.solutechconsulting.media.sample;

import com.solutechconsulting.media.model.ImmutableMovie;
import com.solutechconsulting.media.model.Movie;
import io.reactivex.Flowable;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;

import static com.solutechconsulting.media.sample.SampleDefinitions.Movies.Columns.*;

/**
 * Loads movie data from a resource-based CSV file into {@link Movie} domain models.
 */
public class MovieLoader {

  public Flowable<Movie> loadMovies() {
    try {
      Reader movieReader =
          new InputStreamReader(
              getClass().getResourceAsStream(SampleDefinitions.Movies.SAMPLE_RESOURCE));
      return Flowable.fromIterable(CSVFormat.EXCEL.withFirstRecordAsHeader().parse(movieReader))
          .map(record -> {
            ImmutableMovie.Builder movieBuilder = ImmutableMovie.builder();

            movieBuilder.id(record.get(MEDIA_ID)).title(record.get(TITLE))
                .studio(record.get(STUDIO)).contentRating(
                record.get(CONTENT_RATING)).summary(
                record.get(SUMMARY)).genres(
                record.get(GENRES)).tagline(record.get(TAGLINE)).directors(record.get(DIRECTORS))
                .roles(
                    record.get(ROLES));

            String csvYear = record.get(YEAR);
            if (csvYear != null && !csvYear.isEmpty() && !csvYear.equals("N/A")) {
              movieBuilder.year(Integer.parseInt(csvYear));
            }

            String csvCriticsRating = record.get(RATING);
            if (csvCriticsRating != null && !csvCriticsRating.isEmpty() && !csvCriticsRating
                .equals("N/A")) {
              movieBuilder.criticsRating(Double.parseDouble(csvCriticsRating));
            }

            String csvAudienceRating = record.get(AUDIENCE);
            if (csvAudienceRating != null && !csvAudienceRating.isEmpty() && !csvAudienceRating
                .equals("N/A")) {
              movieBuilder.audienceRating(Double.parseDouble(csvAudienceRating));
            }

            String csvReleaseDate = record.get(RELEASE_DATE);
            if (csvReleaseDate != null && !csvReleaseDate.isEmpty() && !csvReleaseDate
                .equals("N/A")) {
              movieBuilder.releaseDate(LocalDate.parse(csvReleaseDate));
            }

            movieBuilder.duration(SampleDefinitions.getDuration(record.get(DURATION)));

            return movieBuilder.build();
          });
    } catch (IOException e) {
      return Flowable.error(e);
    }
  }
}
