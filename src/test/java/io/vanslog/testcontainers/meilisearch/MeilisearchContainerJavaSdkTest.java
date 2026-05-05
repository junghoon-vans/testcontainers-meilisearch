package io.vanslog.testcontainers.meilisearch;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.model.SearchResult;
import com.meilisearch.sdk.model.TaskInfo;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Java SDK integration tests for {@link MeilisearchContainer}.
 *
 * @author Junghoon Ban
 */
@Testcontainers
class MeilisearchContainerJavaSdkTest {

  @Container
  private static final MeilisearchContainer meilisearchContainer = createMeilisearchContainer();

  @SuppressWarnings("resource")
  private static MeilisearchContainer createMeilisearchContainer() {
    return new MeilisearchContainer().withMasterKey("masterKey");
  }

  @Test
  void shouldUseMeilisearchJavaSdk() throws Exception {
    Client client = new Client(new Config(
        meilisearchContainer.getEndpoint(),
        meilisearchContainer.getMasterKey()));
    String indexUid = "movies";
    Index index = client.index(indexUid);

    assertThat(client.isHealthy()).isTrue();

    TaskInfo createIndexTask = client.createIndex(indexUid, "id");
    index.waitForTask(createIndexTask.getTaskUid(), 15000, 100);

    String documents = "["
        + "{\"id\":1,\"title\":\"Dune\"},"
        + "{\"id\":2,\"title\":\"Foundation\"}"
        + "]";
    TaskInfo addDocumentsTask = index.addDocuments(documents);
    index.waitForTask(addDocumentsTask.getTaskUid(), 15000, 100);

    SearchResult searchResult = index.search("dune");

    assertThat(searchResult.getEstimatedTotalHits()).isEqualTo(1);
    assertThat(searchResult.getHits())
        .singleElement()
        .extracting(hit -> hit.get("title"))
        .isEqualTo("Dune");
  }
}
