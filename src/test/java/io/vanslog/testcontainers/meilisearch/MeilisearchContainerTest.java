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
  void shouldKeepDumpImportStrictByDefault() {
    try (MeilisearchContainer container = new MeilisearchContainer()
        .withDumpImport("meilisearch/fixtures/movies.dump")) {
      assertThat(container.getCommandParts())
          .contains("--import-dump")
          .doesNotContain("--ignore-missing-dump", "--ignore-dump-if-db-exists");
    }
  }

  @Test
  void shouldConfigureDumpImportFlags() {
    try (MeilisearchContainer container = new MeilisearchContainer()
        .withDumpImport("meilisearch/fixtures/movies.dump")
        .withIgnoreMissingDump()
        .withIgnoreDumpIfDbExists()) {
      assertThat(container.getCommandParts())
          .contains("--import-dump", "--ignore-missing-dump", "--ignore-dump-if-db-exists");
    }
  }

  @Test
  void shouldKeepSnapshotImportStrictByDefault() {
    try (MeilisearchContainer container = new MeilisearchContainer()
        .withSnapshotImport("meilisearch/fixtures/movies.snapshot")) {
      assertThat(container.getCommandParts())
          .contains("--import-snapshot")
          .doesNotContain("--ignore-missing-snapshot", "--ignore-snapshot-if-db-exists");
    }
  }

  @Test
  void shouldConfigureSnapshotImportFlags() {
    try (MeilisearchContainer container = new MeilisearchContainer()
        .withIgnoreMissingSnapshot()
        .withIgnoreSnapshotIfDbExists()
        .withSnapshotImport("meilisearch/fixtures/movies.snapshot")) {
      assertThat(container.getCommandParts())
          .contains("--import-snapshot", "--ignore-missing-snapshot", "--ignore-snapshot-if-db-exists");
    }
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
  void shouldRejectMixedImportTypes() {
    try (MeilisearchContainer container = new MeilisearchContainer()
        .withDumpImport("meilisearch/fixtures/movies.dump")) {
      assertThatThrownBy(() -> container.withSnapshotImport("meilisearch/fixtures/movies.snapshot"))
          .isInstanceOf(IllegalStateException.class);
    }

    try (MeilisearchContainer container = new MeilisearchContainer()
        .withSnapshotImport("meilisearch/fixtures/movies.snapshot")) {
      assertThatThrownBy(() -> container.withDumpImport("meilisearch/fixtures/movies.dump"))
          .isInstanceOf(IllegalStateException.class);
    }
  }

  @Test
  void shouldRejectImportFlagsForDifferentImportType() {
    try (MeilisearchContainer container = new MeilisearchContainer().withIgnoreMissingDump()) {
      assertThatThrownBy(() -> container.withSnapshotImport("meilisearch/fixtures/movies.snapshot"))
          .isInstanceOf(IllegalStateException.class);
    }

    try (MeilisearchContainer container = new MeilisearchContainer().withIgnoreMissingSnapshot()) {
      assertThatThrownBy(() -> container.withDumpImport("meilisearch/fixtures/movies.dump"))
          .isInstanceOf(IllegalStateException.class);
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
