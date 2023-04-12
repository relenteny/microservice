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

package com.solutechconsulting.media.server.rest;

import com.solutechconsulting.media.service.MediaService;
import io.reactivex.Flowable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSink;
import org.eclipse.microprofile.metrics.annotation.Timed;

/**
 * RESTful resource providing result streams from the movie methods of the {@link MediaService}.
 * Result streams are sent using server-sent events. The event format is outlined here: {@link
 * AbstractMediaStreamResource#sendEvents(Flowable, SseEventSink)}.
 */
@Path(ResourceDefinitions.Path.Movies.STREAM_PATH)
public class MoviesStreamResource extends AbstractMediaStreamResource {

  private static final String METRICS_PREFIX = "com.solutechconsulting.media.server.rest.MovieStreamResource";

  /**
   * Emit all movies in the media library as server-sent events. See {@link
   * MediaService#getMovies()}.
   *
   * @param sseEventSink the server-sent events sink for the current context
   */
  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Path(ResourceDefinitions.Path.Common.ALL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.GetMovies.TIMER_NAME, displayName =
      METRICS_PREFIX + '.' + MediaService.MetricsDefinitions.GetMovies.TIMER_NAME, description =
      MediaService.MetricsDefinitions.GetMovies.TIMER_DESCRIPTION)
  public void getMoviesStream(@Context SseEventSink sseEventSink) {
    try (SseEventSink eventSink = sseEventSink) {
      getLogger().debug("Invoking getMoviesStream...");
      sendEvents(getMediaService().getMovies(), eventSink);
      getLogger().debug("getMoviesStream complete.");
    }
  }

  /**
   * Perform a case insensitive text search of movies in the media library and emit the results as
   * server-sent events. The service will include the title, summary and tag line attributes of
   * movies in its search. See {@link MediaService#searchMovies(String)}.
   *
   * @param searchText   the text value used in searching movies
   * @param sseEventSink the server-sent events sink for the current context
   */
  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Path(ResourceDefinitions.Path.Common.SEARCH_FULL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.SearchMovies.TIMER_NAME, displayName =
      METRICS_PREFIX + '.' + MediaService.MetricsDefinitions.SearchMovies.TIMER_NAME, description =
      MediaService.MetricsDefinitions.SearchMovies.TIMER_DESCRIPTION)
  public void searchMoviesStream(
      @PathParam(ResourceDefinitions.SEARCH_TEXT_PARAMETER) String searchText,
      @Context SseEventSink sseEventSink) {
    try (SseEventSink eventSink = sseEventSink) {
      getLogger().debug("Invoking searchMoviesStream... Search text: {}", searchText);
      sendEvents(getMediaService().searchMovies(searchText), eventSink);
      getLogger().debug("searchMoviesStream complete.");
    }
  }

  @Override
  protected String getEventName() {
    return ResourceDefinitions.Stream.Movies.EVENT_NAME;
  }
}
