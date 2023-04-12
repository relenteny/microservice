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

package com.solutechconsulting.database.init.flyway;

import com.solutechconsulting.media.sample.TelevisionShowLoader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.ZoneId;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V1_4__Populate_shows_table extends BaseJavaMigration {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void migrate(Context context) throws Exception {
    TelevisionShowLoader showLoader = new TelevisionShowLoader();

    Connection connection = context.getConnection();
    String insertStatement = "insert into media.tv_shows (id,title,content_rating,directors,duration,episode,originally_aired,rating,season,series_title,studio,summary,writers,year) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    try (PreparedStatement statement = connection.prepareStatement(insertStatement)) {
      showLoader.loadTelevisionShows().doOnNext(show -> {
        try {
          statement.setString(1, show.getId());
          statement.setString(2, show.getTitle());
          statement.setString(3, show.getContentRating());
          statement.setString(4, show.getDirectors());
          statement.setLong(5, show.getDuration().toSeconds());
          statement.setInt(6, show.getEpisode());
          if (show.getOriginallyAired().isPresent()) {
            statement.setDate(7,
                new Date(show.getOriginallyAired().get().atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli()));
          } else {
            statement.setNull(7, Types.DATE);
          }
          if (show.getRating().isPresent()) {
            statement.setDouble(8, show.getRating().get());
          } else {
            statement.setNull(8, Types.DOUBLE);
          }
          statement.setInt(9, show.getSeason());
          statement.setString(10, show.getSeriesTitle());
          statement.setString(11, show.getStudio());
          statement.setString(12, show.getSummary());
          statement.setString(13, show.getWriters());
          if (show.getYear().isPresent()) {
            statement.setInt(14, show.getYear().get());
          } else {
            statement.setNull(14, Types.INTEGER);
          }

          statement.addBatch();
        } catch (SQLException e) {
          logger.error("Exception loading audio table.", e);
        }
      }).subscribe().dispose();
      statement.executeBatch();
    }
  }
}
