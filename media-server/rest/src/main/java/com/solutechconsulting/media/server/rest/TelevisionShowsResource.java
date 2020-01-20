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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.annotation.Timed;

/**
 * RESTful resource supporting <b>synchronous</b> television show methods of the media service.
 * While the {@link MediaService} interface specifies streaming results, this resource transforms
 * the stream into single, synchronous responses.
 */
@Path(ResourceDefinitions.Path.TelevisionShows.PATH)
public class TelevisionShowsResource extends AbstractMediaResource {

  private static final String METRICS_PREFIX = "com.solutechconsulting.media.server.rest.TelevisionShowResource";

  /**
   * Return all television shows stored in the media library. See {@link
   * MediaService#getTelevisionShows()}.
   *
   * @return a response containing all television shows in the media library.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(ResourceDefinitions.Path.Common.ALL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.GetTelevisionShows.TIMER_NAME, displayName =
      METRICS_PREFIX + '.'
          + MediaService.MetricsDefinitions.GetTelevisionShows.TIMER_NAME, description =
      "Return all television shows stored in the media library.")
  public Response getTelevisionShows() {
    getLogger().debug("Invoking getTelevisionShows...");
    Response response = createResponse(getMediaService().getTelevisionShows());
    getLogger().debug("getTelevisionShows complete. Response code: {}", response.getStatus());
    return response;
  }

  /**
   * Perform a case insensitive text search of television shows in the media library. The service
   * will include the series and shows titles and show summary in its search. See {@link
   * MediaService#searchTelevisionShows(String)}.
   *
   * @param searchText the text value used in searching television shows
   * @return a response containing television shows matching the search criteria
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(ResourceDefinitions.Path.Common.SEARCH_FULL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.SearchTelevisionShows.TIMER_NAME, displayName =
      METRICS_PREFIX + '.'
          + MediaService.MetricsDefinitions.SearchTelevisionShows.TIMER_NAME, description =
      "Perform a case insensitive text search of television shows in the media library.")
  public Response searchTelevisionShows(
      @PathParam(ResourceDefinitions.SEARCH_TEXT_PARAMETER) String searchText) {
    getLogger().debug("Invoking searchTelevisionShows... Search text: {}", searchText);
    Response response = createResponse(getMediaService().searchTelevisionShows(searchText));
    getLogger().debug("getTelevisionShows complete. Response code: {}", response.getStatus());
    return response;
  }

  /**
   * Given a series title, return television show episodes for the entire series from the media
   * library. Series title will be a case insensitive search. See {@link
   * MediaService#getSeries(String)}.
   *
   * @param seriesTitle the television show series title
   * @return a response containing the television shows matching the search criteria
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(ResourceDefinitions.Path.TelevisionShows.SERIES_FULL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.GetSeries.TIMER_NAME, displayName =
      METRICS_PREFIX + '.' + MediaService.MetricsDefinitions.GetSeries.TIMER_NAME, description =
      "Given a series title, return television show episodes for the entire series from the media library.")
  public Response getSeries(
      @PathParam(ResourceDefinitions.Path.TelevisionShows.SERIES_TITLE_PARAMETER) String seriesTitle) {
    getLogger().debug("Invoking getSeries... Series title: {}", seriesTitle);
    Response response = createResponse(getMediaService().getSeries(seriesTitle));
    getLogger().debug("getSeries complete. Response code: {}", response.getStatus());
    return response;
  }

  /**
   * Given a series title and season, return the television show episodes from the media library.
   * Series title will be a case insensitive search. See {@link MediaService#getEpisodes(String,
   * int)}
   *
   * @param seriesTitle the television show series title
   * @param season      the television show series season number
   * @return a response containing the television shows matching the search criteria
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(ResourceDefinitions.Path.TelevisionShows.EPISODES_FULL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.GetEpisodes.TIMER_NAME, displayName =
      METRICS_PREFIX + '.' + MediaService.MetricsDefinitions.GetEpisodes.TIMER_NAME, description =
      "Given a series title and season, return the television show episodes from the media library.")
  public Response getEpisodes(
      @PathParam(ResourceDefinitions.Path.TelevisionShows.SERIES_TITLE_PARAMETER) String seriesTitle,
      @PathParam(ResourceDefinitions.Path.TelevisionShows.SEASON_PARAMETER) int season) {
    getLogger().debug("Invoking getEpisodes... Series title: {}, Season: {}", seriesTitle, season);
    Response response = createResponse(getMediaService().getEpisodes(seriesTitle, season));
    getLogger().debug("getEpisodes complete. Response code: {}", response.getStatus());
    return response;
  }
}
