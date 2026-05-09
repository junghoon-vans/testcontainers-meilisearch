package io.vanslog.testcontainers.meilisearch;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.model.SearchResult;
import com.meilisearch.sdk.model.TaskInfo;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.BindMode;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Snapshot import integration tests for {@link MeilisearchContainer}.
 *
 * @author Junghoon Ban
 */
@Testcontainers
class MeilisearchContainerSnapshotImportTest {

  @Container
  private static final MeilisearchContainer meilisearchContainer = createMeilisearchContainer();

  @SuppressWarnings("resource")
  private static MeilisearchContainer createMeilisearchContainer() {
    return new MeilisearchContainer()
        .withMasterKey("masterKey")
        .withSnapshotImport("meilisearch/fixtures/movies.snapshot");
  }

  @Test
  void shouldImportSnapshotFixture() throws Exception {
    Client client = new Client(new Config(
        meilisearchContainer.getEndpoint(),
        meilisearchContainer.getMasterKey()));

    assertThat(client.isHealthy()).isTrue();

    SearchResult searchResult = client.index("movies").search("dune");

    assertThat(searchResult.getEstimatedTotalHits()).isEqualTo(1);
    assertThat(searchResult.getHits())
        .singleElement()
        .extracting(hit -> hit.get("title"))
        .isEqualTo("Dune");
  }

  @Test
  void shouldImportSnapshotFixtureWithIgnoreFlags() throws Exception {
    MeilisearchContainer container = new MeilisearchContainer();
    try {
      container
          .withMasterKey("masterKey")
          .withSnapshotImport("meilisearch/fixtures/movies.snapshot")
          .withIgnoreMissingSnapshot()
          .withIgnoreSnapshotIfDbExists();
      container.start();

      Client client = new Client(new Config(
          container.getEndpoint(),
          container.getMasterKey()));

      assertThat(client.isHealthy()).isTrue();

      SearchResult searchResult = client.index("movies").search("dune");

      assertThat(searchResult.getEstimatedTotalHits()).isEqualTo(1);
      assertThat(searchResult.getHits())
          .singleElement()
          .extracting(hit -> hit.get("title"))
          .isEqualTo("Dune");
    } finally {
      container.close();
    }
  }

  @Test
  void shouldIgnoreSnapshotImportWhenDatabaseExists(@TempDir Path dataDirectory) throws Exception {
    seedExistingDatabase(dataDirectory);

    MeilisearchContainer container = new MeilisearchContainer();
    try {
      container
          .withFileSystemBind(dataDirectory.toString(), "/meili_data", BindMode.READ_WRITE)
          .withMasterKey("masterKey")
          .withSnapshotImport("meilisearch/fixtures/movies.snapshot")
          .withIgnoreSnapshotIfDbExists();
      container.start();

      Client client = new Client(new Config(
          container.getEndpoint(),
          container.getMasterKey()));

      assertThat(client.isHealthy()).isTrue();

      SearchResult searchResult = client.index("existing_movies").search("arrival");

      assertThat(searchResult.getEstimatedTotalHits()).isEqualTo(1);
      assertThat(searchResult.getHits())
          .singleElement()
          .extracting(hit -> hit.get("title"))
          .isEqualTo("Arrival");
    } finally {
      container.close();
    }
  }

  private static void seedExistingDatabase(Path dataDirectory) throws Exception {
    MeilisearchContainer container = new MeilisearchContainer();
    try {
      container
          .withFileSystemBind(dataDirectory.toString(), "/meili_data", BindMode.READ_WRITE)
          .withMasterKey("masterKey");
      container.start();

      Client client = new Client(new Config(
          container.getEndpoint(),
          container.getMasterKey()));
      String indexUid = "existing_movies";
      Index index = client.index(indexUid);

      TaskInfo createIndexTask = client.createIndex(indexUid, "id");
      index.waitForTask(createIndexTask.getTaskUid(), 15000, 100);

      TaskInfo addDocumentsTask = index.addDocuments("[{\"id\":1,\"title\":\"Arrival\"}]");
      index.waitForTask(addDocumentsTask.getTaskUid(), 15000, 100);
    } finally {
      container.close();
    }
  }
}
