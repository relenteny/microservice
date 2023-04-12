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

package com.solutechconsulting.media.service.mock.basic;

import com.solutechconsulting.media.model.Audio;
import com.solutechconsulting.media.model.Movie;
import com.solutechconsulting.media.model.TelevisionShow;
import com.solutechconsulting.media.sample.AudioLoader;
import com.solutechconsulting.media.sample.MovieLoader;
import com.solutechconsulting.media.sample.TelevisionShowLoader;
import com.solutechconsulting.media.service.AbstractMediaService;
import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;
import javax.interceptor.Interceptor;

/**
 * A simple implementation of the {@link com.solutechconsulting.media.service.MediaService}
 * interface to be used in unit and some integration testing.
 */
@ApplicationScoped
@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@Named(MockMediaService.SERVICE_NAME)
public class MockMediaService extends AbstractMediaService {

  public static final String SERVICE_NAME = "MockMediaService";

  @Override
  protected Flowable<Movie> doGetMovies() {
    return new MovieLoader().loadMovies();
  }

  @Override
  protected Flowable<Movie> doSearchMovies(String movieText) {
    return new MovieLoader().loadMovies().filter(movie -> {
      List<String> fields = new ArrayList<>();
      fields.add(movie.getTagline());
      fields.add(movie.getTitle());
      fields.add(movie.getSummary());
      return textMatch(movieText, fields);
    });
  }

  @Override
  protected Flowable<Audio> doGetAudio() {
    return new AudioLoader().loadAudio();
  }

  @Override
  protected Flowable<Audio> doGetAudioTracks(String albumTitle) {
    String lcAlbumTitle = albumTitle.toLowerCase();

    return new AudioLoader().loadAudio()
        .filter(audio -> audio.getAlbum().equalsIgnoreCase(lcAlbumTitle));
  }

  @Override
  protected Flowable<Audio> doSearchAudio(String audioText) {
    return new AudioLoader().loadAudio().filter(audio -> {
      List<String> fields = new ArrayList<>();
      fields.add(audio.getTitle());
      fields.add(audio.getAlbum());
      audio.getArtist().ifPresent(fields::add);
      fields.add(audio.getAlbumArtist());
      return textMatch(audioText, fields);
    });
  }

  @Override
  protected Flowable<TelevisionShow> doGetTelevisionShows() {
    return new TelevisionShowLoader().loadTelevisionShows();
  }

  @Override
  protected Flowable<TelevisionShow> doSearchTelevisionShows(String showText) {
    return new TelevisionShowLoader().loadTelevisionShows().filter(show -> {
      List<String> fields = new ArrayList<>();
      fields.add(show.getTitle());
      fields.add(show.getSeriesTitle());
      fields.add(show.getSummary());
      return textMatch(showText, fields);
    });
  }

  @Override
  protected Flowable<TelevisionShow> doGetEpisodes(String seriesTitle, int season) {
    return new TelevisionShowLoader().loadTelevisionShows().filter(
        show -> show.getSeason() == season && show.getSeriesTitle().equalsIgnoreCase(seriesTitle));
  }

  @Override
  protected Flowable<TelevisionShow> doGetSeries(String seriesTitle) {
    return new TelevisionShowLoader().loadTelevisionShows().filter(
        show -> show.getSeriesTitle().equalsIgnoreCase(seriesTitle));
  }

  @Override
  protected String getMetricsPrefix() {
    return MockMediaService.class.getName();
  }

  protected boolean textMatch(String searchText, List<String> fields) {
    String lcSearchText = searchText.toLowerCase();
    for (String field : fields) {
      if (field.toLowerCase().contains(lcSearchText)) {
        return true;
      }
    }

    return false;
  }
}
