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

import com.solutechconsulting.media.model.Media;
import io.reactivex.Flowable;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

/**
 * AbstractStreamResource supports asynchronous RESTful media service endpoints.
 */
public abstract class AbstractMediaStreamResource extends AbstractMediaResource {

  private OutboundSseEvent.Builder outboundEventBuilder;

  /**
   * Send server-side events to callers. Given a flowable as returned from the {@link
   * com.solutechconsulting.media.service.MediaService}, map the resultant {@link Media} to a
   * JSON-encoded server-side event. The stream is terminate with a designated event. The format of
   * the events are as follows:
   * <br>
   * <code>
   * comment: "The media item title"<br> name: "The media type"<br> id: "The media item id"<br>
   * data: "The media item serialized as JSON"<br>
   * </code>
   * <br>
   * Upon termination of the stream, and end of steam event will be sent. The end of stream event
   * format is as follows:
   * <br>
   * <code>
   * name: "The media type"<br> comment: End of stream.<br> id: END_OF_STREAM<br>
   * </code>
   * <br>
   * If an error occurs, an error event will be sent using the following format:
   * <code>
   * name: "The media type"<br> comment: An error has occurred.<br> id: ERROR_MARKER<br> data: The
   * error/exception text<br>
   * </code>
   *
   * @param flowable  a flowable returned from the {@link com.solutechconsulting.media.service.MediaService}
   *                  methods
   * @param eventSink the event sink to which the event data is to be sent
   * @param <T>       the type of Media object being processed
   */
  protected <T extends Media> void sendEvents(Flowable<T> flowable, SseEventSink eventSink) {
    flowable.map(media -> outboundEventBuilder
        .name(getEventName())
        .id(media.getId())
        .comment(media.getTitle())
        .mediaType(MediaType.APPLICATION_JSON_TYPE)
        .data(this.getClass(), media)
        .build()).subscribe(eventSink::send,
        throwable -> {
          getLogger().error("An exception has occurred while streaming events.",
              throwable);
          eventSink.send(outboundEventBuilder
              .name(getEventName())
              .mediaType(
                  MediaType.TEXT_PLAIN_TYPE)
              .comment(ResourceDefinitions.Stream.ERROR_COMMENT)
              .id(ResourceDefinitions.Stream.ERROR_MARKER)
              .data(throwable.toString())
              .build());

        }, () -> eventSink.send(outboundEventBuilder
            .name(getEventName())
            .comment(
                ResourceDefinitions.Stream.END_OF_STREAM_COMMENT)
            .id(ResourceDefinitions.Stream.END_OF_STREAM_MARKER)
            .build()).thenAccept(o -> eventSink.close())).dispose();
  }

  protected abstract String getEventName();

  @Context
  public void setSse(Sse sse) {
    this.outboundEventBuilder = sse.newEventBuilder();
  }
}
