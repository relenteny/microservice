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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.solutechconsulting.media.model.Audio;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TestAudioResource extends ResourceTestBase {

  private static final String AUDIO_PATH = URL_PREFIX + ResourceDefinitions.Path.Audio.PATH;

  @Test
  public void testGetAudio() throws IOException {
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(AUDIO_PATH);
    List<Audio> audio = getResponseResult(target);
    client.close();
    assertEquals(3368, audio.size());
  }

  @Test
  public void testSearchAudio() throws IOException {
    Client client = ClientBuilder.newClient();
    WebTarget target =
        client.target(
            AUDIO_PATH + ResourceDefinitions.Path.Common.SEARCH_PATH + "/pink floyd");
    List<Audio> audio = getResponseResult(target);
    client.close();
    assertEquals(145, audio.size());
  }

  @Test
  public void testGetAudioTracks() throws IOException {
    Client client = ClientBuilder.newClient();
    WebTarget target =
        client.target(
            AUDIO_PATH + ResourceDefinitions.Path.Audio.TRACKS_PATH + "/aja");
    List<Audio> audio = getResponseResult(target);
    client.close();
    assertEquals(7, audio.size());
  }
}
