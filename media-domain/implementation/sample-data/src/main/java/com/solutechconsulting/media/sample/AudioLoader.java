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

import com.solutechconsulting.media.model.Audio;
import com.solutechconsulting.media.model.ImmutableAudio;
import io.reactivex.Flowable;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static com.solutechconsulting.media.sample.SampleDefinitions.Audio.Columns.*;

/**
 * Loads audio data from a resource-based CSV file into {@link Audio} domain models.
 */
public class AudioLoader {

  public Flowable<Audio> loadAudio() {
    try {
      Reader audioReader =
          new InputStreamReader(
              getClass().getResourceAsStream(SampleDefinitions.Audio.SAMPLE_RESOURCE));
      return Flowable.fromIterable(CSVFormat.EXCEL.withFirstRecordAsHeader().parse(audioReader))
          .map(record -> {
            ImmutableAudio.Builder audioBuilder = ImmutableAudio.builder();

            audioBuilder.id(record.get(MEDIA_ID)).title(record.get(TITLE)).albumArtist(
                record.get(ALBUM_ARTIST)).album(
                record.get(ALBUM)).trackNumber(Integer.parseInt(record.get(TRACK_NUMBER)));

            String csvYear = record.get(YEAR);
            if (csvYear != null && !csvYear.isEmpty() && !csvYear.equals("N/A")) {
              audioBuilder.year(Integer.parseInt(csvYear));
            }

            String csvAlbumArtist = record.get(ALBUM_ARTIST);
            String csvArtist = record.get(ARTIST);
            if (csvArtist != null && !csvArtist.equals("N/A") && !csvArtist
                .equals(csvAlbumArtist)) {
              audioBuilder.artist(csvArtist);
            }

            audioBuilder.duration(SampleDefinitions.getDuration(record.get(DURATION)));

            return audioBuilder.build();
          });
    } catch (IOException e) {
      return Flowable.error(e);
    }
  }
}
