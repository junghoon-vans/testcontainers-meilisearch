package io.vanslog.testcontainers.meilisearch;

/**
 * Environment modes supported by Meilisearch.
 */
public enum MeilisearchEnvMode {
  DEVELOPMENT("development"),
  PRODUCTION("production");

  private final String value;

  MeilisearchEnvMode(String value) {
    this.value = value;
  }

  /**
   * Get the Meilisearch configuration value.
   * @return The environment mode value
   */
  public String getValue() {
    return value;
  }
}
