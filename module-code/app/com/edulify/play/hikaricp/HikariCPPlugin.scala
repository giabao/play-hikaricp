/*
 * Copyright 2014 Edulify.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.edulify.play.hikaricp

import java.sql.Connection
import play.api.db.{DBApi, DBPlugin}
import play.api.{Application, Configuration, Mode, Logger}
import scala.util.control.NonFatal

class HikariCPPlugin(app: Application) extends DBPlugin {

  lazy val dbConfig = app.configuration.getConfig("db").getOrElse(Configuration.empty)

  /** plugin is disabled if either configuration is missing or the plugin is explicitly disabled */
  private lazy val isDisabled = app.configuration.getString("hikari.enabled").contains("false") || dbConfig.subKeys.isEmpty
  override def enabled = ! isDisabled

  // should be accessed in onStart first
  private lazy val dbApi: DBApi = new HikariCPDBApi(dbConfig, app.classloader)

  def api: DBApi = dbApi

  override def onStart() {
    play.api.Logger.info("Starting HikariCP connection pool...")
    dbApi.datasources.map { ds =>
        try {
          ds._1.getConnection.close()
          app.mode match {
            case Mode.Test =>
            case mode => Logger.info("database [" + ds._2 + "] connected at " + dbURL(ds._1.getConnection))
          }
        } catch {
          case NonFatal(e) =>
            throw dbConfig.reportError(ds._2 + ".url", "Cannot connect to database [" + ds._2 + "]", Some(e.getCause))
        }
    }
  }

  override def onStop() {
    play.api.Logger.info("Stoping HikariCP connection pool...")
    dbApi.datasources.foreach {
      case (ds, _) => try {
        dbApi.shutdownPool(ds)
      } catch {
        case t: Throwable =>
      }
    }
  }

  private def dbURL(conn: Connection): String = {
    val u = conn.getMetaData.getURL
    conn.close()
    u
  }
}
