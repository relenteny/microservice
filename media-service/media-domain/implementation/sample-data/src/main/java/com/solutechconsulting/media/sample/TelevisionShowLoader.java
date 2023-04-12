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

import com.solutechconsulting.media.model.ImmutableTelevisionShow;
import io.reactivex.Flowable;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;

import static com.solutechconsulting.media.sample.SampleDefinitions.TelevisionShow.Columns.*;

/**
 * Loads television show data from a resource-based CSV file into {@link
 * com.solutechconsulting.media.model.TelevisionShow} domain models.
 */
public class TelevisionShowLoader {

  public Flowable<com.solutechconsulting.media.model.TelevisionShow> loadTelevisionShows() {
    try {
      Reader showReader =
          new InputStreamReader(
              getClass().getResourceAsStream(SampleDefinitions.TelevisionShow.SAMPLE_RESOURCE));
      return Flowable.fromIterable(CSVFormat.EXCEL.withFirstRecordAsHeader().parse(showReader))
          .map(record -> {
            ImmutableTelevisionShow.Builder showBuilder = ImmutableTelevisionShow.builder();

            showBuilder.id(record.get(MEDIA_ID)).title(record.get(EPISODE_TITLE)).seriesTitle(
                record.get(SERIES_TITLE)).contentRating(record.get(CONTENT_RATING)).summary(
                record.get(SUMMARY)).studio(record.get(STUDIO)).directors(
                record.get(DIRECTORS)).writers(record.get(WRITERS));

            String csvYear = record.get(YEAR);
            showBuilder.year(getIntValue(csvYear));

            String csvSeason = record.get(SEASON);
            showBuilder.season(getIntValue(csvSeason));

            String csvEpisode = record.get(EPISODE);
            showBuilder.episode(getIntValue(csvEpisode));

            String csvRating = record.get(RATING);
            if (csvRating != null && !csvRating.isEmpty() && !csvRating.equals("N/A")) {
              showBuilder.rating(Double.parseDouble(csvRating));
            }

            String csvOriginallyAired = record.get(ORIGINALLY_AIRED);
            if (csvOriginallyAired != null && !csvOriginallyAired.isEmpty() && !csvOriginallyAired
                .equals("N/A")) {
              showBuilder.originallyAired(LocalDate.parse(csvOriginallyAired));
            }

            showBuilder.duration(SampleDefinitions.getDuration(record.get(DURATION)));

            return showBuilder.build();
          });
    } catch (IOException e) {
      return Flowable.error(e);
    }
  }

  protected int getIntValue(String strValue) {
    if (strValue != null && !strValue.isEmpty() && !strValue.equals("N/A")) {
      return Integer.parseInt(strValue);
    }

    return 0;
  }
}
