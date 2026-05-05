package io.vanslog.testcontainers.meilisearch;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * Container for Meilisearch
 *
 * @since 1.0
 * @author Junghoon Ban
 */
public class MeilisearchContainer extends GenericContainer<MeilisearchContainer> {

  private static final int MEILISEARCH_DEFAULT_PORT = 7700;
  private static final String DUMP_IMPORT_PATH = importPath("dumps", "import.dump");
  private static final String SNAPSHOT_IMPORT_PATH = importPath("snapshots", "import.snapshot");
  private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("getmeili/meilisearch");
  private static final String DEFAULT_IMAGE_TAG = "v1.43.0";

  private static String importPath(String directory, String fileName) {
    return String.join("/", "", "meili_data", directory, fileName);
  }

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
    dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);
    this.addExposedPort(MEILISEARCH_DEFAULT_PORT);
    this.waitingFor(Wait.forHttp("/health")
        .forPort(MEILISEARCH_DEFAULT_PORT)
        .forStatusCode(200));
  }

  /**
   * Configure master key
   * @param masterKey A master key to use
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withMasterKey(String masterKey) {
    this.addEnv("MEILI_MASTER_KEY", masterKey);
    return self();
  }

  /**
   * Configure the Meilisearch log level.
   * @param logLevel The log level to use
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withLogLevel(String logLevel) {
    this.addEnv("MEILI_LOG_LEVEL", logLevel);
    return self();
  }

  /**
   * Disable Meilisearch analytics for this container.
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withNoAnalytics() {
    this.addEnv("MEILI_NO_ANALYTICS", "true");
    return self();
  }

  /**
   * Import a dump fixture from the test classpath when Meilisearch starts.
   * @param classpathResource The dump resource to import
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withDumpImport(String classpathResource) {
    return withDumpImport(MountableFile.forClasspathResource(classpathResource));
  }

  /**
   * Import a dump fixture when Meilisearch starts.
   * @param dumpFile The dump file to import
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withDumpImport(MountableFile dumpFile) {
    this.withCopyFileToContainer(dumpFile, DUMP_IMPORT_PATH);
    this.withCommand("meilisearch", "--import-dump", DUMP_IMPORT_PATH);
    return self();
  }

  /**
   * Import a snapshot fixture from the test classpath when Meilisearch starts.
   * @param classpathResource The snapshot resource to import
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withSnapshotImport(String classpathResource) {
    return withSnapshotImport(MountableFile.forClasspathResource(classpathResource));
  }

  /**
   * Import a snapshot fixture when Meilisearch starts.
   * @param snapshotFile The snapshot file to import
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withSnapshotImport(MountableFile snapshotFile) {
    this.withCopyFileToContainer(snapshotFile, SNAPSHOT_IMPORT_PATH);
    this.withCommand("meilisearch", "--import-snapshot", SNAPSHOT_IMPORT_PATH);
    return self();
  }

  /**
   * Get the HTTP endpoint for the running Meilisearch container.
   * @return The HTTP endpoint to use with Meilisearch clients
   */
  public String getEndpoint() {
    return "http://" + getHost() + ":" + getMappedPort(MEILISEARCH_DEFAULT_PORT);
  }

  /**
   * Get the configured master key.
   * @return The configured master key, or {@code null} when none was configured
   */
  public String getMasterKey() {
    return getEnvMap().get("MEILI_MASTER_KEY");
  }
}
