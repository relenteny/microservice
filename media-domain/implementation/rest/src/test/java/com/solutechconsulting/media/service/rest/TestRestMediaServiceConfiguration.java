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

package com.solutechconsulting.media.service.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class TestRestMediaServiceConfiguration {

  @Inject
  RestMediaServiceUrlConfiguration serviceUrlConfiguration;

  @Inject
  RestMediaServiceCommonConfiguration serviceCommonConfiguration;

  @Inject
  RestMediaServiceMoviesConfiguration serviceMoviesConfiguration;

  @Inject
  RestMediaServiceAudioConfiguration serviceAudioConfiguration;

  @Inject
  RestMediaServiceShowsConfiguration serviceShowsConfiguration;

  @Inject
  RestMediaServiceStreamConfiguration serviceStreamConfiguration;

  @Test
  public void testUrlConfiguration() {
    assertEquals("http", serviceUrlConfiguration.getProtocol());
    assertEquals("localhost", serviceUrlConfiguration.getHost());
    assertEquals(8888, serviceUrlConfiguration.getPort());
  }

  @Test
  public void testCommonConfiguration() {
    assertEquals("/media", serviceCommonConfiguration.getMediaRoot());
    assertEquals("/sch", serviceCommonConfiguration.getSearch());
    assertEquals("/stream", serviceCommonConfiguration.getStream());
  }

  @Test
  public void testMoviesConfiguration() {
    assertEquals("/movies", serviceMoviesConfiguration.getBase());
  }

  @Test
  public void testAudioConfiguration() {
    assertEquals("/audio", serviceAudioConfiguration.getBase());
    assertEquals("/trx", serviceAudioConfiguration.getTracks());
  }

  @Test
  public void testShowsConfiguration() {
    assertEquals("/tv", serviceShowsConfiguration.getBase());
    assertEquals("/series", serviceShowsConfiguration.getSeries());
  }

  @Test
  public void testStreamConfiguration() {
    assertEquals("EOS", serviceStreamConfiguration.getEndOfStreamMarker());
    assertEquals("ERROR_MARKER", serviceStreamConfiguration.getErrorMarker());
  }
}
