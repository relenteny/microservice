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
import io.quarkus.test.junit.QuarkusTest;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class LoaderTest {

  @Test
  public void testMovieLoader() throws InterruptedException {
    MovieLoader movieLoader = new MovieLoader();

    Flowable<Movie> flowable = movieLoader.loadMovies();

    TestSubscriber<Movie> subscriber = flowable.test();

    subscriber.assertNoErrors();
    subscriber.await().assertComplete();
    List<Movie> movies = subscriber.values();
    assertNotNull(movies);
    assertNotEquals(0, movies.size());

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
  public void testTelevisionShowLoader() throws InterruptedException {
    TelevisionShowLoader showLoader = new TelevisionShowLoader();

    Flowable<TelevisionShow> flowable = showLoader.loadTelevisionShows();

    TestSubscriber<TelevisionShow> subscriber = flowable.test();

    subscriber.assertNoErrors();
    subscriber.await().assertComplete();
    List<TelevisionShow> shows = subscriber.values();
    assertNotNull(shows);
    assertNotEquals(0, shows.size());

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
  public void testLoadAudio() throws InterruptedException {
    AudioLoader audioLoader = new AudioLoader();

    Flowable<Audio> flowable = audioLoader.loadAudio();

    TestSubscriber<Audio> subscriber = flowable.test();

    subscriber.assertNoErrors();
    subscriber.await().assertComplete();
    List<Audio> audioList = subscriber.values();
    assertNotNull(audioList);
    assertNotEquals(0, audioList.size());

    boolean found = false;
    for (Audio audio : audioList) {
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
}
