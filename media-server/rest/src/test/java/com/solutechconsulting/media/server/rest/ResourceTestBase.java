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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solutechconsulting.media.model.Media;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.inject.Inject;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.SseEventSource;
import org.junit.jupiter.api.Assertions;

public class ResourceTestBase {

  protected static final String URL_PREFIX = "http://localhost:8081";

  @Inject
  ObjectMapper objectMapper;

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  protected <T> List<T> getResponseResult(WebTarget target) throws IOException {
    Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE);
    try (Response response = invocationBuilder.get()) {
      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      assertTrue(response.hasEntity());
      return getObjectMapper()
          .readValue(response.readEntity(InputStream.class), new TypeReference<List<T>>() {
          });
    }
  }

  protected <T extends Media> List<T> getStreamResult(WebTarget target,
      TypeReference<T> typeReference) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    List<T> media = new ArrayList<>();

    try (SseEventSource eventSource = SseEventSource.target(target).build()) {
      eventSource.register(sseEvent -> {
        try {
          if (sseEvent.getId().equals(ResourceDefinitions.Stream.END_OF_STREAM_MARKER)) {
            latch.countDown();
          } else {
            media.add(getObjectMapper().readValue(sseEvent.readData(), typeReference));
          }
        } catch (IOException e) {
          fail(e);
        }
      }, Assertions::fail);

      eventSource.open();
      latch.await();
    }

    return media;
  }
}
