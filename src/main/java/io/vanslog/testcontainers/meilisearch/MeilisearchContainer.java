package io.vanslog.testcontainers.meilisearch;

import java.util.ArrayList;
import java.util.List;
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

  private ImportType importType;
  private boolean ignoreMissingDump;
  private boolean ignoreDumpIfDbExists;
  private boolean ignoreMissingSnapshot;
  private boolean ignoreSnapshotIfDbExists;

  private enum ImportType {
    DUMP,
    SNAPSHOT
  }

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
   * Configure the Meilisearch environment mode.
   * @param envMode The environment mode to use
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withEnvMode(MeilisearchEnvMode envMode) {
    return withEnvMode(envMode.getValue());
  }

  /**
   * Configure the Meilisearch environment mode.
   * @param envMode The environment mode to use
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withEnvMode(String envMode) {
    this.addEnv("MEILI_ENV", envMode);
    return self();
  }

  /**
   * Configure the Meilisearch log level.
   * @param logLevel The log level to use
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withLogLevel(MeilisearchLogLevel logLevel) {
    return withLogLevel(logLevel.getValue());
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
    configureImportType(ImportType.DUMP);
    this.withCopyFileToContainer(dumpFile, DUMP_IMPORT_PATH);
    configureImportCommand();
    return self();
  }

  /**
   * Start normally when the configured dump fixture is missing.
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withIgnoreMissingDump() {
    assertImportTypeCompatible(ImportType.DUMP);
    this.ignoreMissingDump = true;
    configureImportCommand();
    return self();
  }

  /**
   * Start with the existing database when a dump fixture is configured and data already exists.
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withIgnoreDumpIfDbExists() {
    assertImportTypeCompatible(ImportType.DUMP);
    this.ignoreDumpIfDbExists = true;
    configureImportCommand();
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
    configureImportType(ImportType.SNAPSHOT);
    this.withCopyFileToContainer(snapshotFile, SNAPSHOT_IMPORT_PATH);
    configureImportCommand();
    return self();
  }

  /**
   * Start normally when the configured snapshot fixture is missing.
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withIgnoreMissingSnapshot() {
    assertImportTypeCompatible(ImportType.SNAPSHOT);
    this.ignoreMissingSnapshot = true;
    configureImportCommand();
    return self();
  }

  /**
   * Start with the existing database when a snapshot fixture is configured and data already exists.
   * @return The current instance of the Meilisearch container
   */
  public MeilisearchContainer withIgnoreSnapshotIfDbExists() {
    assertImportTypeCompatible(ImportType.SNAPSHOT);
    this.ignoreSnapshotIfDbExists = true;
    configureImportCommand();
    return self();
  }

  private void configureImportType(ImportType importType) {
    assertImportTypeCompatible(importType);
    if (hasFlagsForDifferentImportType(importType)) {
      throw new IllegalStateException("Import flags must match the configured import type");
    }
    this.importType = importType;
  }

  private void assertImportTypeCompatible(ImportType importType) {
    if (this.importType != null && this.importType != importType) {
      throw new IllegalStateException("Only one Meilisearch import type can be configured per container");
    }
  }

  private boolean hasFlagsForDifferentImportType(ImportType importType) {
    if (importType == ImportType.DUMP) {
      return ignoreMissingSnapshot || ignoreSnapshotIfDbExists;
    }
    return ignoreMissingDump || ignoreDumpIfDbExists;
  }

  private void configureImportCommand() {
    if (importType == null) {
      return;
    }

    List<String> command = new ArrayList<>();
    command.add("meilisearch");
    if (importType == ImportType.DUMP) {
      command.add("--import-dump");
      command.add(DUMP_IMPORT_PATH);
      addFlag(command, ignoreMissingDump, "--ignore-missing-dump");
      addFlag(command, ignoreDumpIfDbExists, "--ignore-dump-if-db-exists");
    } else {
      command.add("--import-snapshot");
      command.add(SNAPSHOT_IMPORT_PATH);
      addFlag(command, ignoreMissingSnapshot, "--ignore-missing-snapshot");
      addFlag(command, ignoreSnapshotIfDbExists, "--ignore-snapshot-if-db-exists");
    }
    this.withCommand(command.toArray(new String[0]));
  }

  private static void addFlag(List<String> command, boolean enabled, String flag) {
    if (enabled) {
      command.add(flag);
    }
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
