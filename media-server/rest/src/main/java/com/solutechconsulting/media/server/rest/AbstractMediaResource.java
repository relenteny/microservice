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
import com.solutechconsulting.media.service.MediaService;
import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractMediaResource contains common and convenience methods used by all {@link MediaService}
 * RESTful resources.
 */
public abstract class AbstractMediaResource {

  private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

  @Inject
  MediaService mediaService;

  /**
   * Given a flowable as returned from the {@link MediaService}, create a {@link Response} to be
   * used with synchronous RESTful endpoints.
   *
   * @param flowable a flowable as returned by the {@link MediaService}
   * @return a response containing an array of all items returned in the flowable or a bad request
   * error if an exception occurs.
   */
  protected Response createResponse(Flowable<? extends Media> flowable) {
    getLogger().debug("Creating response...");
    Response.ResponseBuilder responseBuilder = Response.ok();
    List<Media> movies = new ArrayList<>();

    flowable.subscribe(movies::add,
        throwable -> {
          getLogger().error("Media service exception.", throwable);
          movies.clear();
          responseBuilder.status(Response.Status.BAD_REQUEST.getStatusCode(),
              throwable.getMessage());
        }).dispose();

    if (!movies.isEmpty()) {
      responseBuilder.entity(movies);
    }

    getLogger().debug("Response created.");
    return responseBuilder.build();
  }

  protected MediaService getMediaService() {
    return mediaService;
  }

  protected Logger getLogger() {
    return logger;
  }
}
