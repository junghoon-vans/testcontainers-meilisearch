package io.vanslog.testcontainers.meilisearch;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MeilisearchContainerTest {

  private static final MeilisearchContainer meilisearchContainer = new MeilisearchContainer();

  @BeforeAll
  public static void setup() {
    meilisearchContainer.start();
  }

  @AfterAll
  public static void cleanup() {
    meilisearchContainer.stop();
  }

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
