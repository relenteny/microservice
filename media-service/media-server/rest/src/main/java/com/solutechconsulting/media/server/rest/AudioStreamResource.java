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
 * RESTful resource providing result streams from the audio item methods of the {@link
 * MediaService}. Result streams are sent using server-sent events. The event format is outlined
 * here: {@link AbstractMediaStreamResource#sendEvents(Flowable, SseEventSink)}.
 */
@Path(ResourceDefinitions.Path.Audio.STREAM_PATH)
public class AudioStreamResource extends AbstractMediaStreamResource {

  private static final String METRICS_PREFIX = "com.solutechconsulting.media.server.rest.AudioStreamResource";

  /**
   * Emit all audio items in the media library as server-sent events. See {@link
   * MediaService#getAudio()}.
   *
   * @param sseEventSink the server-sent events sink for the current context
   */
  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Path(ResourceDefinitions.Path.Common.ALL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.GetAudio.TIMER_NAME, displayName =
      METRICS_PREFIX + '.' + MediaService.MetricsDefinitions.GetAudio.TIMER_NAME, description =
      MediaService.MetricsDefinitions.GetAudio.TIMER_DESCRIPTION)
  public void getAudioStream(@Context SseEventSink sseEventSink) {
    try (SseEventSink eventSink = sseEventSink) {
      getLogger().debug("Invoking getAudioStream...");
      sendEvents(getMediaService().getAudio(), eventSink);
      getLogger().debug("getAudioStream complete.");
    }
  }

  /**
   * Perform a case insensitive text search of audio items in the media library and emit the results
   * as server-sent events. The service will include the song and album titles and the album
   * artist(s) in its search. See {@link MediaService#searchAudio(String)}.
   *
   * @param searchText   the text value used in searching audio
   * @param sseEventSink the server-sent events sink for the current context
   */
  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Path(ResourceDefinitions.Path.Common.SEARCH_FULL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.SearchAudio.TIMER_NAME, displayName =
      METRICS_PREFIX + '.' + MediaService.MetricsDefinitions.SearchAudio.TIMER_NAME, description =
      MediaService.MetricsDefinitions.SearchAudio.TIMER_DESCRIPTION)
  public void searchAudioStream(
      @PathParam(ResourceDefinitions.SEARCH_TEXT_PARAMETER) String searchText,
      @Context SseEventSink sseEventSink) {
    try (SseEventSink eventSink = sseEventSink) {
      getLogger().debug("Invoking searchAudioStream... Search text: {}", searchText);
      sendEvents(getMediaService().searchAudio(searchText), eventSink);
      getLogger().debug("searchAudioStream complete.");
    }
  }

  /**
   * Given a case-insensitive album title, emit the associated album tracks as server-sent events.
   * See {@link MediaService#getAudioTracks(String)}.
   *
   * @param albumTitle   the album title
   * @param sseEventSink the server-sent events sink for the current context
   */
  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  @Path(ResourceDefinitions.Path.Audio.TRACKS_FULL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.GetAudioTracks.TIMER_NAME, displayName =
      METRICS_PREFIX + '.'
          + MediaService.MetricsDefinitions.GetAudioTracks.TIMER_NAME, description =
      MediaService.MetricsDefinitions.GetAudioTracks.TIMER_DESCRIPTION)
  public void getAudioTracksStream(
      @PathParam(ResourceDefinitions.Path.Audio.ALBUM_TITLE_PARAMETER) String albumTitle,
      @Context SseEventSink sseEventSink) {
    try (SseEventSink eventSink = sseEventSink) {
      getLogger().debug("Invoking getAudioTracksStream... Album title: {}", albumTitle);
      sendEvents(getMediaService().getAudioTracks(albumTitle), eventSink);
      getLogger().debug("getAudioTracksStream complete.");
    }
  }

  @Override
  protected String getEventName() {
    return ResourceDefinitions.Stream.Audio.EVENT_NAME;
  }
}
