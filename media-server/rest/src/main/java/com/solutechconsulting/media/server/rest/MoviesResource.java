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
 * RESTful resource supporting <b>synchronous</b> movie methods of the media service. While the
 * {@link MediaService} interface specifies streaming results, this resource transforms the stream
 * into single, synchronous responses.
 */
@Path(ResourceDefinitions.Path.Movies.PATH)
public class MoviesResource extends AbstractMediaResource {

  private static final String METRICS_PREFIX = "com.solutechconsulting.media.server.rest.MoviesResource";

  /**
   * Return all movies stored in the media library. See {@link MediaService#getMovies()}.
   *
   * @return a response containing all movies in the media library.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(ResourceDefinitions.Path.Common.ALL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.GetMovies.TIMER_NAME, displayName =
      METRICS_PREFIX + '.' + MediaService.MetricsDefinitions.GetMovies.TIMER_NAME, description =
      "Return all " +
          "movies in the media library.")
  public Response getMovies() {
    getLogger().debug("Invoking getMovies...");
    Response response = createResponse(getMediaService().getMovies());
    getLogger().debug("getMovies complete. Response code: {}", response.getStatus());
    return response;
  }

  /**
   * Perform a case insensitive text search of movies in the media library. The service will include
   * the title, summary and tag line attributes of movies in its search. See {@link
   * MediaService#searchMovies(String)}.
   *
   * @param searchText the text value used in searching movies
   * @return a response containing movies matching the search criteria
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(ResourceDefinitions.Path.Common.SEARCH_FULL_PATH)
  @Timed(name = MediaService.MetricsDefinitions.SearchMovies.TIMER_NAME, displayName =
      METRICS_PREFIX + '.'
          + MediaService.MetricsDefinitions.SearchMovies.TIMER_NAME, description = "")
  public Response searchMovies(
      @PathParam(ResourceDefinitions.SEARCH_TEXT_PARAMETER) String searchText) {
    getLogger().debug("Invoking searchMovies... Search text: {}", searchText);
    Response response = createResponse(getMediaService().searchMovies(searchText));
    getLogger().debug("searchMovies complete. Response code: {}", response.getStatus());
    return response;
  }
}
