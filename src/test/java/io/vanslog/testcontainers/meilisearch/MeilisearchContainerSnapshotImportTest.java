package io.vanslog.testcontainers.meilisearch;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.model.SearchResult;
import com.meilisearch.sdk.model.TaskInfo;
import com.github.dockerjava.api.model.Bind;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
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
  void shouldImportSnapshotFixtureWithIgnoreDatabaseExistsFlag() throws Exception {
    MeilisearchContainer container = new MeilisearchContainer();
    try {
      container
          .withMasterKey("masterKey")
          .withSnapshotImport("meilisearch/fixtures/movies.snapshot")
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
  void shouldIgnoreSnapshotImportWhenDatabaseExists() throws Exception {
    String volumeName = newVolumeName();
    seedExistingDatabase(volumeName);

    MeilisearchContainer container = new MeilisearchContainer();
    try {
      withDataVolume(container, volumeName)
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
      removeVolume(volumeName);
    }
  }

  private static void seedExistingDatabase(String volumeName) throws Exception {
    MeilisearchContainer container = new MeilisearchContainer();
    try {
      withDataVolume(container, volumeName)
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

  private static MeilisearchContainer withDataVolume(MeilisearchContainer container, String volumeName) {
    return container.withCreateContainerCmdModifier(command -> command
        .getHostConfig()
        .withBinds(Bind.parse(volumeName + ":/meili_data")));
  }

  private static String newVolumeName() {
    return "testcontainers-meilisearch-" + UUID.randomUUID();
  }

  private static void removeVolume(String volumeName) {
    DockerClientFactory.instance().client().removeVolumeCmd(volumeName).exec();
  }
}
