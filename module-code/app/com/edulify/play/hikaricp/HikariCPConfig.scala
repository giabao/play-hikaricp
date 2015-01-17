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

import com.zaxxer.hikari.HikariConfig
import play.api.{Configuration, Logger}

import java.io._
import java.util.Properties

import scala.collection.JavaConversions._

object HikariCPConfig {
  lazy val DEFAULT_DATASOURCE_NAME = "default"
  lazy val HIKARI_CP_PROPERTIES_FILE = "hikaricp.properties"

  def getHikariConfig(dbConfig: Configuration) = {
    val file = new File(HIKARI_CP_PROPERTIES_FILE)
    if(file.exists()) new HikariConfig(props(file))
    else new HikariConfig(mapFromPlayConfiguration(dbConfig))
  }

  private def props(file: File): Properties = {
    if (!file.exists()) {
      throw new IllegalStateException(s"Hikari configuration file ${file.getAbsolutePath} doesn't exist.")
    }

    def load(): Properties = {
      Logger.info("Loading Hikari configuration from " + file)

      val props = new Properties()
      val reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))
      try { props.load(reader) } finally { reader.close() }
      props
    }

    logProperties(load())
  }

  private def mapFromPlayConfiguration(dbConfig: Configuration): Properties = {
    Logger.info("Loading Hikari configuration from Play configuration.")

    val configFile = dbConfig.getString("hikaricp.file")
    if(configFile.nonEmpty) {
      Logger.info("Loading from file configured by db.default.hikaricp.file that is " + configFile)
      return props(new File(configFile.get))
    }

    logProperties(new ConfigProperties(dbConfig))
  }

  /** Log the properties that are used, but don't print out the raw password for security-sake */
  private def logProperties(properties: Properties): Properties = {
    Logger.info("Properties: " + properties.map { case (name: String, value: String) =>
      if (name contains "password") {
        "%s=%.1s%s" format(name, value, value.substring(value.length).padTo(value.length - 1, "*").mkString)
      } else "%s=%s" format(name, value)
    }.mkString(", "))
    properties
  }

  private object ConfigProperties {
    implicit class LongOptionOps(val o: Option[Long]) extends AnyVal {
      def *(x: Long): Option[Long] = o.map(_ * x)
    }
    /** Keep track of the required fields which if not set cause the startup to fail */
    private val playRequired = Set("driver", "url", "user", "password")
  }

  private class ConfigProperties(config: Configuration) extends Properties {
    import ConfigProperties._

    private def set(key: String, playKey: String): Unit = config.getString(playKey).foreach(setProperty(key, _))
    private def set(key: String, o: Option[Long]): Unit = o.foreach(v => setProperty(key, v.toString))

    playRequired
      .find(config.getString(_).isEmpty)
      .foreach(key => throw config.reportError("Play Config", s"Required property not found: '$key'"))

    set("driverClassName", "driver")
    set("jdbcUrl",         "url")
    set("username",        "user")
    set("password",        "password")

    set("autoCommit", "defaultAutoCommit")

    set("connectionTimeout", config.getMilliseconds("connectionTimeout")
      .orElse(config.getLong("connectionTimeoutInMs")))

    set("idleTimeout", config.getMilliseconds("idleMaxAge")
      .orElse(config.getLong("idleMaxAgeInSeconds") * 1000)
      .orElse(config.getLong("idleMaxAgeInMinutes") * 60000))

    set("maxLifetime", config.getMilliseconds("maxConnectionAge")
      .orElse(config.getLong("maxConnectionAgeInSeconds") * 1000)
      .orElse(config.getLong("maxConnectionAgeInMinutes") * 60000))

    //If your driver supports JDBC4 we strongly recommend not setting this property
    set("connectionTestQuery", "connectionTestStatement")

    {
      val partitionCount = config.getLong("partitionCount").getOrElse(1L)
      set("minimumIdle", config.getInt("minConnectionsPerPartition").map(_ * partitionCount))
      set("maximumPoolSize", config.getInt("maxConnectionsPerPartition").map(_ * partitionCount))
    }

    set("leakDetectionThreshold", config.getLong("closeConnectionWatchTimeoutInMs")
      .orElse(config.getMilliseconds("closeConnectionWatchTimeout")))

    set("catalog",             "defaultCatalog")
    set("transactionIsolation", "defaultTransactionIsolation")
    set("readOnly",             "defaultReadOnly")

    set("registerMbeans",       "statisticsEnabled")
    set("connectionInitSql",    "initSQL")
  }
}
