package io.vanslog.testcontainers.meilisearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

/**
 * Container for Meilisearch
 *
 * @since 1.0
 * @author Junghoon Ban
 */
public class MeilisearchContainer extends GenericContainer<MeilisearchContainer> {

  Logger log = LoggerFactory.getLogger(MeilisearchContainer.class);

  private static final int MEILISEARCH_DEFAULT_PORT = 7700;
  private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("getmeili/meilisearch");
  private static final String DEFAULT_IMAGE_TAG = "latest";

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
    this.withLogConsumer(new Slf4jLogConsumer(log));
  }
}
