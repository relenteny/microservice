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

package com.solutechconsulting.database.init.application;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.InfoResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic class to display results of Flyway migration.
 */
@ApplicationScoped
public class DatabaseInitService {

  @Inject
  private Flyway flyway;

  public void checkMigration() {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    logger.info("Flyway migration complete:");
    InfoResult infoOutput = flyway.info().getInfoResult();
    logger.info("   Flyway version:    {}", infoOutput.flywayVersion);
    logger.info("   Schema version:    {}", infoOutput.schemaVersion);
    logger.info("");
    logger.info("   Migration information:");
    logger.info("");
    infoOutput.migrations.forEach(migrationOutput -> {
      logger.info("      Migration:      {}", migrationOutput.description);
      logger.info("      Version:        {}", migrationOutput.version);
      logger.info("      Location:       {}", migrationOutput.filepath);
      logger.info("      State:          {}", migrationOutput.state);
      logger.info("      Category:       {}", migrationOutput.category);
      logger.info("      DB Users:       {}", migrationOutput.installedBy);
      logger.info("      Install on:     {}", migrationOutput.installedOn);
      logger.info("      Execution time: {}", migrationOutput.executionTime);
      logger.info("      Type:           {}", migrationOutput.type);
      logger.info("");
    });
  }

}
