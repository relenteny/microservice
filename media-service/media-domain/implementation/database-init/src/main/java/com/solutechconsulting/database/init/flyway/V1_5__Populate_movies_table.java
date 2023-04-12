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

import com.solutechconsulting.media.sample.MovieLoader;
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

public class V1_5__Populate_movies_table extends BaseJavaMigration {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void migrate(Context context) throws Exception {
    MovieLoader movieLoader = new MovieLoader();

    Connection connection = context.getConnection();
    String insertStatement = "insert into media.movies (id,title,audience_rating,content_rating,critics_rating,directors,duration,genres,released,roles,studio,summary,tagline,year) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    try (PreparedStatement statement = connection.prepareStatement(insertStatement)) {
      movieLoader.loadMovies().doOnNext(movie -> {
        try {
          statement.setString(1, movie.getId());
          statement.setString(2, movie.getTitle());
          if (movie.getAudienceRating().isPresent()) {
            statement.setDouble(3, movie.getAudienceRating().get());
          } else {
            statement.setNull(3, Types.DOUBLE);
          }
          statement.setString(4, movie.getContentRating());
          if (movie.getCriticsRating().isPresent()) {
            statement.setDouble(5, movie.getCriticsRating().get());
          } else {
            statement.setNull(5, Types.DOUBLE);
          }
          statement.setString(6, movie.getDirectors());
          statement.setLong(7, movie.getDuration().toSeconds());
          statement.setString(8, movie.getGenres());
          if (movie.getReleaseDate().isPresent()) {
            statement.setDate(9,
                new Date(movie.getReleaseDate().get().atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli()));
          } else {
            statement.setNull(9, Types.DATE);
          }
          statement.setString(10, movie.getRoles());
          statement.setString(11, movie.getStudio());
          statement.setString(12, movie.getSummary());
          statement.setString(13, movie.getTagline());
          if (movie.getYear().isPresent()) {
            statement.setInt(14, movie.getYear().get());
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
