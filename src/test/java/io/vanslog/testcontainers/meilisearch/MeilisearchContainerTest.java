package io.vanslog.testcontainers.meilisearch;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link MeilisearchContainer}.
 *
 * @author Junghoon Ban
 */
@Testcontainers
class MeilisearchContainerTest {

  @Container
  private static final MeilisearchContainer meilisearchContainer = new MeilisearchContainer()
      .withMasterKey("masterKey");

  @Test
  void shouldStartMeilisearch() {
    assertThat(meilisearchContainer.isRunning()).isTrue();
  }

  @Test
  void shouldGetHost() {
    assertThat(meilisearchContainer.getHost()).isEqualTo("localhost");
  }

  @Test
  void getPort() {
    assertThat(meilisearchContainer.getMappedPort(7700)).isNotNull();
  }
}
