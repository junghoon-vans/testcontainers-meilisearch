package io.vanslog.testcontainers.meilisearch;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Container for Meilisearch
 *
 * @since 1.0
 * @author Junghoon Ban
 */
public class MeilisearchContainer extends GenericContainer<MeilisearchContainer> {

  private static final int MEILISEARCH_DEFAULT_PORT = 7700;
  private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("getmeili/meilisearch");
  private static final String DEFAULT_IMAGE_TAG = "v1.3.4";

  /**
   * Create a Meilisearch container with default settings
   */
  public MeilisearchContainer() {
    this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_IMAGE_TAG));
  }

  /**
   * Create a Meilisearch container with a specific version
   * @param dockerImageName The docker image name to use
   */
  public MeilisearchContainer(DockerImageName dockerImageName) {
    super(dockerImageName);
    this.addExposedPort(MEILISEARCH_DEFAULT_PORT);
  }

  /**
   * Configure master key
   * @param masterKey A master key to use
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withMasterKey(String masterKey) {
    this.addEnv("MEILI_MASTER_KEY", masterKey);
    return self();
  }
}
