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

package com.solutechconsulting.media.service.mock;

import com.solutechconsulting.media.model.Audio;
import com.solutechconsulting.media.model.Movie;
import com.solutechconsulting.media.model.TelevisionShow;
import com.solutechconsulting.media.service.MediaService;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Timer;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("squid:S2259")
public abstract class MediaServiceTestBase {

  @Inject
  @RegistryType(type = MetricRegistry.Type.APPLICATION)
  MetricRegistry metricRegistry;

  @Test
  public void testGetMovies() throws InterruptedException {
    MediaService mediaService = getMediaService();

    Flowable<Movie> flowable = mediaService.getMovies();
    TestSubscriber<Movie> subscriber = flowable.test();

    subscriber.assertNoErrors();
    subscriber.await().assertComplete();
    List<Movie> movies = subscriber.values();
    assertNotNull(movies);
    assertEquals(269, movies.size());

    Timer timer =
        getMetricByName(
            getServiceClassname() + '.' + MediaService.MetricsDefinitions.GetMovies.TIMER_NAME);
    assertNotNull(timer);
    assertEquals(1, timer.getCount());

    boolean found = false;
    for (Movie movie : movies) {
      if (movie.getId().equals("2588")) {
        found = true;

        assertEquals("Star Trek II: The Wrath of Khan", movie.getTitle());
        assertEquals("Paramount Pictures", movie.getStudio());
        assertEquals("PG", movie.getContentRating());
        movie.getYear().ifPresent(year -> assertEquals(1982, year));
        movie.getCriticsRating().ifPresent(rating -> assertEquals(8.8, rating));
        movie.getAudienceRating().ifPresent(rating -> assertEquals(9, rating));
        assertEquals(
            "Admiral James T. Kirk is feeling old; the prospect of accompanying his old ship the " +
                "Enterprise on a two week cadet cruise is not making him feel any younger. But " +
                "the training cruise becomes a a life or death struggle when Khan escapes from " +
                "years of exile and captures the power of creation itself.", movie.getSummary());
        assertEquals("Sci-Fi - Drama - Action", movie.getGenres());
        assertEquals("At the end of the universe lies the beginning of vengeance.",
            movie.getTagline());
        Duration duration = Duration.ofHours(1)
            .plus(Duration.ofMinutes(53).plus(Duration.ofSeconds(1)));
        assertEquals(duration, movie.getDuration());
        LocalDate releaseDate = LocalDate.of(1982, 6, 3);
        movie.getReleaseDate().ifPresent(released -> assertEquals(releaseDate, released));
        assertEquals("Nicholas Meyer", movie.getDirectors());
        assertEquals("William Shatner - Leonard Nimoy - DeForest Kelley", movie.getRoles());
        break;
      }
    }

    assertTrue(found, "Test movie not found.");
  }

  @Test
  public void testSearchMovies() throws InterruptedException {
    MediaService mediaService = getMediaService();

    Flowable<Movie> flowable = mediaService.searchMovies("star trek");
    TestSubscriber<Movie> subscriber = flowable.test();

    subscriber.assertNoErrors();
    subscriber.await().assertComplete();
    List<Movie> movies = subscriber.values();
    assertNotNull(movies);
    assertEquals(13, movies.size());

    Timer timer =
        getMetricByName(
            getServiceClassname() + '.' + MediaService.MetricsDefinitions.SearchMovies.TIMER_NAME);
    assertNotNull(timer);
    assertEquals(1, timer.getCount());
  }

  @Test
  public void testGetAudio() throws InterruptedException {
    MediaService mediaService = getMediaService();

    Flowable<Audio> flowable = mediaService.getAudio();
    TestSubscriber<Audio> subscriber = flowable.test();

    subscriber.assertNoErrors();
    subscriber.await().assertComplete();
    List<Audio> audioItems = subscriber.values();
    assertNotNull(audioItems);
    assertEquals(3368, audioItems.size());

    Timer timer =
        getMetricByName(
            getServiceClassname() + '.' + MediaService.MetricsDefinitions.GetAudio.TIMER_NAME);
    assertNotNull(timer);
    assertEquals(1, timer.getCount());
    boolean found = false;

    for (Audio audio : audioItems) {
      if (audio.getId().equals("15339")) {
        found = true;

        assertEquals("Money", audio.getTitle());
        assertEquals("Pink Floyd", audio.getAlbumArtist());
        assertEquals("The Dark Side Of The Moon", audio.getAlbum());
        assertEquals(6, audio.getTrackNumber());
        Duration duration = Duration.ofMinutes(6).plus(Duration.ofSeconds(22));
        assertEquals(duration, audio.getDuration());
        break;
      }
    }

    assertTrue(found, "Test audio not found.");
  }

  @Test
  public void testSearchAudio() throws InterruptedException {
    MediaService mediaService = getMediaService();

    Flowable<Audio> flowable = mediaService.searchAudio("pink floyd");
    TestSubscriber<Audio> subscriber = flowable.test();

    subscriber.assertNoErrors();
    subscriber.await().assertComplete();
    List<Audio> audio = subscriber.values();
    assertNotNull(audio);
    assertEquals(145, audio.size());

    Timer timer =
        getMetricByName(
            getServiceClassname() + '.' + MediaService.MetricsDefinitions.SearchAudio.TIMER_NAME);
    assertNotNull(timer);
    assertEquals(1, timer.getCount());
  }

  @Test
  public void testGetAudioTracks() throws InterruptedException {
    MediaService mediaService = getMediaService();

    Flowable<Audio> flowable = mediaService.getAudioTracks("aja");
    TestSubscriber<Audio> subscriber = flowable.test();

    subscriber.assertNoErrors();
    subscriber.await().assertComplete();
    List<Audio> audio = subscriber.values();
    assertNotNull(audio);
    assertEquals(7, audio.size());

    Timer timer =
        getMetricByName(
            getServiceClassname() + '.'
                + MediaService.MetricsDefinitions.GetAudioTracks.TIMER_NAME);
    assertNotNull(timer);
    assertEquals(1, timer.getCount());
  }

  @Test
  public void testGetTelevisionShows() throws InterruptedException {
    MediaService mediaService = getMediaService();

    Flowable<TelevisionShow> flowable = mediaService.getTelevisionShows();
    TestSubscriber<TelevisionShow> subscriber = flowable.test();

    subscriber.assertNoErrors();
    subscriber.await().assertComplete();
    List<TelevisionShow> shows = subscriber.values();
    assertNotNull(shows);
    assertEquals(2937, shows.size());

    Timer timer =
        getMetricByName(
            getServiceClassname() + '.'
                + MediaService.MetricsDefinitions.GetTelevisionShows.TIMER_NAME);
    assertNotNull(timer);
    assertEquals(1, timer.getCount());

    boolean found = false;
    for (TelevisionShow show : shows) {
      if (show.getId().equals("24079")) {
        found = true;

        assertEquals("The Locomotive Manipulation", show.getTitle());
        assertEquals("The Big Bang Theory", show.getSeriesTitle());
        assertEquals("TV-14", show.getContentRating());
        show.getYear().ifPresent(year -> assertEquals(2014, year));
        show.getRating().ifPresent(rating -> assertEquals(7.8, rating));
        assertEquals(7, show.getSeason());
        assertEquals(15, show.getEpisode());
        assertEquals(
            "Sheldon and Amy go on a trip to wine country with Howard and Bernadette; Penny and " +
                "Leonard rush Raj's dog to the vet", show.getSummary());
        assertEquals("CBS", show.getStudio());
        Duration duration = Duration.ofMinutes(20).plus(Duration.ofSeconds(1));
        assertEquals(duration, show.getDuration());
        LocalDate originallyAired = LocalDate.of(2014, 2, 6);
        show.getOriginallyAired().ifPresent(aired -> assertEquals(originallyAired, aired));
        assertEquals("Mark Cendrowski", show.getDirectors());
        assertEquals("Jim Reynolds - Steve Holland", show.getWriters());
        break;
      }
    }

    assertTrue(found, "Test television show not found.");
  }

  @Test
  public void testSearchTelevisionShows() throws InterruptedException {
    MediaService mediaService = getMediaService();

    Flowable<TelevisionShow> flowable = mediaService.searchTelevisionShows("hawkeye");
    TestSubscriber<TelevisionShow> subscriber = flowable.test();

    subscriber.assertNoErrors();
    subscriber.await().assertComplete();
    List<TelevisionShow> shows = subscriber.values();
    assertNotNull(shows);
    assertEquals(153, shows.size());

    Timer timer =
        getMetricByName(
            getServiceClassname() + '.'
                + MediaService.MetricsDefinitions.SearchTelevisionShows.TIMER_NAME);
    assertNotNull(timer);
    assertEquals(1, timer.getCount());
  }

  @Test
  public void testGetEpisodes() throws InterruptedException {
    MediaService mediaService = getMediaService();

    Flowable<TelevisionShow> flowable = mediaService.getEpisodes("doc martin", 3);
    TestSubscriber<TelevisionShow> subscriber = flowable.test();

    subscriber.assertNoErrors();
    subscriber.await().assertComplete();
    List<TelevisionShow> shows = subscriber.values();
    assertNotNull(shows);
    assertEquals(7, shows.size());

    Timer timer =
        getMetricByName(
            getServiceClassname() + '.' + MediaService.MetricsDefinitions.GetEpisodes.TIMER_NAME);
    assertNotNull(timer);
    assertEquals(1, timer.getCount());
  }

  @Test
  public void testGetSeries() throws InterruptedException {
    MediaService mediaService = getMediaService();

    Flowable<TelevisionShow> flowable = mediaService.getSeries("batman");
    TestSubscriber<TelevisionShow> subscriber = flowable.test();

    subscriber.assertNoErrors();
    subscriber.await().assertComplete();
    List<TelevisionShow> shows = subscriber.values();
    assertNotNull(shows);
    assertEquals(120, shows.size());

    Timer timer =
        getMetricByName(
            getServiceClassname() + '.' + MediaService.MetricsDefinitions.GetSeries.TIMER_NAME);
    assertNotNull(timer);
    assertEquals(1, timer.getCount());
  }

  @SuppressWarnings("unchecked")
  private <T> T getMetricByName(String metricName) {
    Map<MetricID, Metric> metrics = metricRegistry.getMetrics();

    for (Map.Entry<MetricID, Metric> entry : metrics.entrySet()) {
      if (entry.getKey().getName().equals(metricName)) {
        return (T) entry.getValue();
      }
    }

    return null;
  }

  protected abstract MediaService getMediaService();

  protected abstract String getServiceClassname();
}
