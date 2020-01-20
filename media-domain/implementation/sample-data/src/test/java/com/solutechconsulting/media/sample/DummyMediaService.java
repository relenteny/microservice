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
import com.solutechconsulting.media.model.Movie;
import com.solutechconsulting.media.model.TelevisionShow;
import com.solutechconsulting.media.service.AbstractMediaService;
import io.reactivex.Flowable;

import javax.enterprise.context.ApplicationScoped;

/**
 * Dummy class to satisfy model health check injection point.
 */
@ApplicationScoped
public class DummyMediaService extends AbstractMediaService {

  @Override
  protected Flowable<Movie> doGetMovies() {
    return null;
  }

  @Override
  protected Flowable<Movie> doSearchMovies(String movieText) {
    return null;
  }

  @Override
  protected Flowable<Audio> doGetAudio() {
    return null;
  }

  @Override
  protected Flowable<Audio> doGetAudioTracks(String albumTitle) {
    return null;
  }

  @Override
  protected Flowable<Audio> doSearchAudio(String audioText) {
    return null;
  }

  @Override
  protected Flowable<TelevisionShow> doGetTelevisionShows() {
    return null;
  }

  @Override
  protected Flowable<TelevisionShow> doSearchTelevisionShows(String showText) {
    return null;
  }

  @Override
  protected Flowable<TelevisionShow> doGetEpisodes(String seriesTitle, int season) {
    return null;
  }

  @Override
  protected Flowable<TelevisionShow> doGetSeries(String seriesTitle) {
    return null;
  }

  @Override
  protected String getMetricsPrefix() {
    return null;
  }
}
