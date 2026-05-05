package io.vanslog.testcontainers.meilisearch;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link MeilisearchContainer}.
 *
 * @author Junghoon Ban
 */
@Testcontainers
@SuppressWarnings("resource")
class MeilisearchContainerTest {

  @Container
  private static final MeilisearchContainer meilisearchContainer = new MeilisearchContainer()
      .withMasterKey("masterKey");

  @Test
  void shouldStartMeilisearch() {
    assertThat(meilisearchContainer.isRunning()).isTrue();
  }

  @Test
  void shouldGetEndpoint() {
    assertThat(meilisearchContainer.getEndpoint())
        .startsWith("http://")
        .contains(":" + meilisearchContainer.getMappedPort(7700));
  }

  @Test
  void getPort() {
    assertThat(meilisearchContainer.getMappedPort(7700)).isNotNull();
  }

  @Test
  void shouldGetMasterKey() {
    assertThat(meilisearchContainer.getMasterKey()).isEqualTo("masterKey");
  }

  @Test
  void shouldConfigureLogLevel() {
    try (MeilisearchContainer container = new MeilisearchContainer().withLogLevel("DEBUG")) {
      assertThat(container.getEnvMap()).containsEntry("MEILI_LOG_LEVEL", "DEBUG");
    }
  }

  @Test
  void shouldRejectIncompatibleImage() {
    DockerImageName redisImage = DockerImageName.parse("redis:7");

    assertThatThrownBy(() -> {
      try (MeilisearchContainer ignored = new MeilisearchContainer(redisImage)) {
      }
    })
        .isInstanceOf(IllegalStateException.class);
  }

}
