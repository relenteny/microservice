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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.solutechconsulting.media.model.TelevisionShow;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TestTelevisionShowsResource extends ResourceTestBase {

  private static final String TELEVISION_SHOWS_PATH =
      URL_PREFIX + ResourceDefinitions.Path.TelevisionShows.PATH;

  @Test
  public void testGetTelevisionShows() throws IOException {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(TELEVISION_SHOWS_PATH);
    List<TelevisionShow> shows = getResponseResult(target);
    client.close();
    assertEquals(2937, shows.size());
  }

  @Test
  public void testSearchTelevisionShows() throws IOException {
    Client client = ClientBuilder.newClient();
    WebTarget target =
        client.target(
            TELEVISION_SHOWS_PATH + ResourceDefinitions.Path.Common.SEARCH_PATH + "/hawkeye");
    List<TelevisionShow> shows = getResponseResult(target);
    client.close();
    assertEquals(153, shows.size());
  }

  @Test
  public void testGetSeries() throws IOException {
    Client client = ClientBuilder.newClient();
    WebTarget target =
        client.target(
            TELEVISION_SHOWS_PATH + ResourceDefinitions.Path.TelevisionShows.SERIES_PATH
                + "/batman");
    List<TelevisionShow> shows = getResponseResult(target);
    client.close();
    assertEquals(120, shows.size());
  }

  @Test
  public void testGetEpisodes() throws IOException {
    Client client = ClientBuilder.newClient();
    WebTarget target =
        client.target(
            TELEVISION_SHOWS_PATH + ResourceDefinitions.Path.TelevisionShows.SERIES_PATH + "/doc " +
                "martin/3/");
    List<TelevisionShow> shows = getResponseResult(target);
    client.close();
    assertEquals(7, shows.size());
  }
}
