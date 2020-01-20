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
 * RESTful resource providing result streams from the television show methods of the {@link
 * MediaService}. Result streams are sent using server-sent events. The event format is outlined
 * here: {@link AbstractMediaStreamResource#sendEvents(Flowable, SseEventSink)}.
 */
@Path(ResourceDefinitions.Path.TelevisionShows.STREAM_PATH)
public class TelevisionShowsStreamResource extends AbstractMediaStreamResource {

  private static final String METRICS_PREFIX = "com.solutechconsulting.media.server.rest" +
      ".TelevisionShowStreamResource";

  /**
   * Emit all television shows in the media library as server-sent events. See {@link
   * MediaService#getTelevisionShows()}.
   *
   * @param sseEventSink the server-sent events sink for the current context
   */
  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Path(ResourceDefinitions.Path.Common.ALL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.GetTelevisionShows.TIMER_NAME, displayName =
      METRICS_PREFIX + '.'
          + MediaService.MetricsDefinitions.GetTelevisionShows.TIMER_NAME, description =
      MediaService.MetricsDefinitions.GetTelevisionShows.TIMER_DESCRIPTION)
  public void getTelevisionShowsStream(@Context SseEventSink sseEventSink) {
    try (SseEventSink eventSink = sseEventSink) {
      getLogger().debug("Invoking getTelevisionShowsStream...");
      sendEvents(getMediaService().getTelevisionShows(), eventSink);
      getLogger().debug("getTelevisionShowsStream complete.");
    }
  }

  /**
   * Perform a case insensitive text search of television shows in the media library and emit the
   * results as server-sent events. The service will include the series and shows titles and show
   * summary in its search. See {@link MediaService#searchTelevisionShows(String)}.
   *
   * @param searchText   the text value used in searching television shows
   * @param sseEventSink the server-sent events sink for the current context
   */
  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Path(ResourceDefinitions.Path.Common.SEARCH_FULL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.SearchTelevisionShows.TIMER_NAME, displayName =
      METRICS_PREFIX + '.'
          + MediaService.MetricsDefinitions.SearchTelevisionShows.TIMER_NAME, description =
      MediaService.MetricsDefinitions.SearchTelevisionShows.TIMER_DESCRIPTION)
  public void searchTelevisionShowsStream(
      @PathParam(ResourceDefinitions.SEARCH_TEXT_PARAMETER) String searchText,
      @Context SseEventSink sseEventSink) {
    try (SseEventSink eventSink = sseEventSink) {
      getLogger().debug("Invoking searchTelevisionShowsStream... Search text: {}", searchText);
      sendEvents(getMediaService().searchTelevisionShows(searchText), eventSink);
      getLogger().debug("searchTelevisionShowsStream complete.");
    }
  }

  /**
   * Given a series title, emit television show episodes for the entire series from the media
   * library as server-sent events. Series title will be a case insensitive search. See {@link
   * MediaService#getSeries(String)}.
   *
   * @param seriesTitle  the television show series title
   * @param sseEventSink the server-sent events sink for the current context
   */
  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Path(ResourceDefinitions.Path.TelevisionShows.SERIES_FULL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.GetSeries.TIMER_NAME, displayName =
      METRICS_PREFIX + '.' + MediaService.MetricsDefinitions.GetSeries.TIMER_NAME, description =
      MediaService.MetricsDefinitions.GetSeries.TIMER_DESCRIPTION)
  public void getSeriesStream(
      @PathParam(ResourceDefinitions.Path.TelevisionShows.SERIES_TITLE_PARAMETER) String seriesTitle,
      @Context SseEventSink sseEventSink) {
    try (SseEventSink eventSink = sseEventSink) {
      getLogger().debug("Invoking getSeriesStream... Series title: {}", seriesTitle);
      sendEvents(getMediaService().getSeries(seriesTitle), eventSink);
      getLogger().debug("getSeriesStream complete.");
    }
  }

  /**
   * Given a series title and season, emit the television show episodes from the media library as
   * server-sent events. Series title will be a case insensitive search. See {@link
   * MediaService#getEpisodes(String, int)}
   *
   * @param seriesTitle  the television show series title
   * @param season       the television show series season number
   * @param sseEventSink the server-sent events sink for the current context
   */
  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Path(ResourceDefinitions.Path.TelevisionShows.EPISODES_FULL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.GetEpisodes.TIMER_NAME, displayName =
      METRICS_PREFIX + '.' + MediaService.MetricsDefinitions.GetEpisodes.TIMER_NAME, description =
      MediaService.MetricsDefinitions.GetEpisodes.TIMER_DESCRIPTION)
  public void getEpisodesStream(
      @PathParam(ResourceDefinitions.Path.TelevisionShows.SERIES_TITLE_PARAMETER) String seriesTitle,
      @PathParam(ResourceDefinitions.Path.TelevisionShows.SEASON_PARAMETER) int season,
      @Context SseEventSink sseEventSink) {
    try (SseEventSink eventSink = sseEventSink) {
      getLogger()
          .debug("Invoking getEpisodesStream... Series title: {}, Season: {}", seriesTitle, season);
      sendEvents(getMediaService().getEpisodes(seriesTitle, season), eventSink);
      getLogger().debug("getEpisodesStream complete.");
    }
  }

  @Override
  protected String getEventName() {
    return ResourceDefinitions.Stream.TelevisionShows.EVENT_NAME;
  }
}
