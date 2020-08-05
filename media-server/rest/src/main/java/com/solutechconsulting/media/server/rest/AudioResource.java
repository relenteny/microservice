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

import com.solutechconsulting.media.model.Audio;
import com.solutechconsulting.media.service.MediaService;
import io.smallrye.mutiny.Multi;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.metrics.annotation.Timed;

/**
 * RESTful resource supporting <b>synchronous</b> audio item methods of the media service. While the
 * {@link MediaService} interface specifies streaming results, this resource transforms the stream
 * into single, synchronous responses.
 */
@Path(ResourceDefinitions.Path.Audio.PATH)
public class AudioResource extends AbstractMediaResource<Audio> {

  private static final String METRICS_PREFIX = "com.solutechconsulting.media.server.rest.AudioResource";

  /**
   * Return all audio items in the media library. See {@link MediaService#getAudio()}.
   *
   * @return a response containing all audio items in the media library.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(ResourceDefinitions.Path.Common.ALL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.GetAudio.TIMER_NAME, displayName =
      METRICS_PREFIX + '.' + MediaService.MetricsDefinitions.GetAudio.TIMER_NAME, description =
      MediaService.MetricsDefinitions.GetAudio.TIMER_DESCRIPTION)
  public Multi<Audio> getAudio() {
    getLogger().debug("Invoking getAudio...");
    return createResponse(getMediaService().getAudio());
  }

  /**
   * Perform a case insensitive text search of audio items in the media library. The service will
   * include the song and album titles and the album artist(s) in its search. See {@link
   * MediaService#searchAudio(String)}.
   *
   * @param searchText the text value used in searching audio
   * @return a response containing audio items matching the search criteria
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(ResourceDefinitions.Path.Common.SEARCH_FULL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.SearchAudio.TIMER_NAME, displayName =
      METRICS_PREFIX + '.' + MediaService.MetricsDefinitions.SearchAudio.TIMER_NAME, description =
      MediaService.MetricsDefinitions.SearchAudio.TIMER_DESCRIPTION)
  public Multi<Audio> searchAudio(
      @PathParam(ResourceDefinitions.SEARCH_TEXT_PARAMETER) String searchText) {
    getLogger().debug("Invoking searchAudio... Search text: {}", searchText);
    return createResponse(getMediaService().searchAudio(searchText));
  }

  /**
   * Given a case-insensitive album title, return the associated album tracks. See {@link
   * MediaService#getAudioTracks(String)}.
   *
   * @param albumTitle the album title
   * @return a response containing the tracks associated with the album
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(ResourceDefinitions.Path.Audio.TRACKS_FULL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.GetAudioTracks.TIMER_NAME, displayName =
      METRICS_PREFIX + '.'
          + MediaService.MetricsDefinitions.GetAudioTracks.TIMER_NAME, description =
      MediaService.MetricsDefinitions.GetAudioTracks.TIMER_DESCRIPTION)
  public Multi<Audio> getTracks(
      @PathParam(ResourceDefinitions.Path.Audio.ALBUM_TITLE_PARAMETER) String albumTitle) {
    getLogger().debug("Invoking getTracks... Album title: {}", albumTitle);
    return createResponse(getMediaService().getAudioTracks(albumTitle));
  }
}
