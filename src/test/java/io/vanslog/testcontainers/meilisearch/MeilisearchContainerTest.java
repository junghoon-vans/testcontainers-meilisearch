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
  void shouldConfigureEnvMode() {
    try (MeilisearchContainer container = new MeilisearchContainer().withEnvMode("development")) {
      assertThat(container.getEnvMap()).containsEntry("MEILI_ENV", "development");
    }
  }

  @Test
  void shouldConfigureTypedEnvMode() {
    try (MeilisearchContainer container = new MeilisearchContainer()
        .withEnvMode(MeilisearchEnvMode.PRODUCTION)) {
      assertThat(container.getEnvMap()).containsEntry("MEILI_ENV", "production");
    }
  }

  @Test
  void shouldConfigureLogLevel() {
    try (MeilisearchContainer container = new MeilisearchContainer().withLogLevel("DEBUG")) {
      assertThat(container.getEnvMap()).containsEntry("MEILI_LOG_LEVEL", "DEBUG");
    }
  }

  @Test
  void shouldConfigureTypedLogLevel() {
    try (MeilisearchContainer container = new MeilisearchContainer()
        .withLogLevel(MeilisearchLogLevel.TRACE)) {
      assertThat(container.getEnvMap()).containsEntry("MEILI_LOG_LEVEL", "TRACE");
    }
  }

  @Test
  void shouldDisableAnalytics() {
    try (MeilisearchContainer container = new MeilisearchContainer().withNoAnalytics()) {
      assertThat(container.getEnvMap()).containsEntry("MEILI_NO_ANALYTICS", "true");
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
