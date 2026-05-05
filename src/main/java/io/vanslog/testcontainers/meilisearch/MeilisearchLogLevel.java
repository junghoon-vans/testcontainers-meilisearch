package io.vanslog.testcontainers.meilisearch;

/**
 * Log levels supported by Meilisearch.
 */
public enum MeilisearchLogLevel {
  OFF,
  ERROR,
  WARN,
  INFO,
  DEBUG,
  TRACE;

  /**
   * Get the Meilisearch configuration value.
   * @return The log level value
   */
  public String getValue() {
    return name();
  }
}
