package io.vanslog.testcontainers.meilisearch;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.model.SearchResult;
import org.junit.jupiter.api.Test;
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
}
