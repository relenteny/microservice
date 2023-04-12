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

import com.solutechconsulting.media.sample.AudioLoader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class V1_3__Populate_audio_table extends BaseJavaMigration {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void migrate(Context context) throws Exception {
    AudioLoader audioLoader = new AudioLoader();

    Connection connection = context.getConnection();
    String insertStatement = "insert into media.audio (id,title,album,album_artist,artist,duration,track_number,year) values (?,?,?,?,?,?,?,?)";

    try (PreparedStatement statement = connection.prepareStatement(insertStatement)) {
      audioLoader.loadAudio().doOnNext(audio -> {
        try {
          statement.setString(1, audio.getId());
          statement.setString(2, audio.getTitle());
          statement.setString(3, audio.getAlbum());
          statement.setString(4, audio.getAlbumArtist());
          if (audio.getArtist().isPresent()) {
            statement.setString(5, audio.getArtist().get());
          } else {
            statement.setNull(5, Types.VARCHAR);
          }
          statement.setLong(6, audio.getDuration().toSeconds());
          statement.setInt(7, audio.getTrackNumber());
          if (audio.getYear().isPresent()) {
            statement.setInt(8, audio.getYear().get());
          } else {
            statement.setNull(8, Types.INTEGER);
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
