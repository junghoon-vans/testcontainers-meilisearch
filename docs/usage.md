# Usage Guide

This guide shows the main `MeilisearchContainer` API for JUnit 5, Spring Boot,
the Meilisearch Java SDK, and fixture imports.

## Start a container

Use the default Meilisearch image:

```java
@Container
MeilisearchContainer container = new MeilisearchContainer();
```

Use a custom compatible image when your tests need a specific Meilisearch
version:

```java
@Container
MeilisearchContainer container = new MeilisearchContainer(
    DockerImageName.parse("getmeili/meilisearch:v1.43.0"));
```

## Configure runtime options

Configure a master key for authenticated tests:

```java
@Container
MeilisearchContainer container = new MeilisearchContainer()
    .withMasterKey("masterKey");
```

Configure the Meilisearch environment mode with the enum values
`MeilisearchEnvMode.DEVELOPMENT` or `MeilisearchEnvMode.PRODUCTION`:

```java
@Container
MeilisearchContainer container = new MeilisearchContainer()
    .withEnvMode(MeilisearchEnvMode.DEVELOPMENT);
```

The string overload is also available:

```java
@Container
MeilisearchContainer container = new MeilisearchContainer()
    .withEnvMode("development");
```

Configure the log level with `MeilisearchLogLevel.OFF`, `ERROR`, `WARN`, `INFO`,
`DEBUG`, or `TRACE`:

```java
@Container
MeilisearchContainer container = new MeilisearchContainer()
    .withLogLevel(MeilisearchLogLevel.DEBUG);
```

The string overload is also available:

```java
@Container
MeilisearchContainer container = new MeilisearchContainer()
    .withLogLevel("DEBUG");
```

Disable Meilisearch analytics for the container:

```java
@Container
MeilisearchContainer container = new MeilisearchContainer()
    .withNoAnalytics();
```

## Connect with the Java SDK

The container exposes helpers for the Meilisearch Java SDK:

```java
Client client = new Client(new Config(container.getEndpoint(), container.getMasterKey()));
```

Create an index, add documents, and wait for asynchronous tasks to finish:

```java
@Container
static MeilisearchContainer container = new MeilisearchContainer()
    .withMasterKey("masterKey");

Client client = new Client(new Config(container.getEndpoint(), container.getMasterKey()));
String indexUid = "movies";
Index index = client.index(indexUid);

TaskInfo createIndexTask = client.createIndex(indexUid, "id");
index.waitForTask(createIndexTask.getTaskUid(), 15000, 100);

String documents = "["
    + "{\"id\":1,\"title\":\"Dune\"},"
    + "{\"id\":2,\"title\":\"Foundation\"}"
    + "]";
TaskInfo addDocumentsTask = index.addDocuments(documents);
index.waitForTask(addDocumentsTask.getTaskUid(), 15000, 100);
```

## Use with JUnit 5

```java
@Testcontainers
class SearchTest {

    @Container
    static MeilisearchContainer container = new MeilisearchContainer()
        .withMasterKey("masterKey");

    @Test
    void connectsWithSdk() {
        Client client = new Client(new Config(container.getEndpoint(), container.getMasterKey()));
    }
}
```

## Use with Spring Boot

For Spring Boot tests, register your own application properties from the
container. Spring Boot 3.1+ can also wire custom containers through its
Testcontainers support if you prefer.

```java
@Testcontainers
@SpringBootTest
class SearchIntegrationTest {

    @Container
    static MeilisearchContainer container = new MeilisearchContainer()
        .withMasterKey("masterKey");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("app.search.endpoint", container::getEndpoint);
        registry.add("app.search.api-key", container::getMasterKey);
    }
}
```

## Import fixtures

Import a dump fixture from the test classpath:

```java
@Container
static MeilisearchContainer container = new MeilisearchContainer()
    .withMasterKey("masterKey")
    .withDumpImport("meilisearch/fixtures/movies.dump");
```

Import a dump fixture from a `MountableFile`:

```java
@Container
static MeilisearchContainer container = new MeilisearchContainer()
    .withMasterKey("masterKey")
    .withDumpImport(MountableFile.forClasspathResource("meilisearch/fixtures/movies.dump"));
```

Import a snapshot fixture from the test classpath:

```java
@Container
static MeilisearchContainer container = new MeilisearchContainer()
    .withMasterKey("masterKey")
    .withSnapshotImport("meilisearch/fixtures/movies.snapshot");
```

Import a snapshot fixture from a `MountableFile`:

```java
@Container
static MeilisearchContainer container = new MeilisearchContainer()
    .withMasterKey("masterKey")
    .withSnapshotImport(MountableFile.forClasspathResource("meilisearch/fixtures/movies.snapshot"));
```

Snapshots are exact copies of Meilisearch data and must be created with the same
Meilisearch version as the container image that imports them. Use dumps when you
need a fixture that can move across Meilisearch versions.

Dump and snapshot imports are strict by default. Only one import source can be
configured per container. Dump helpers only apply to dump imports, and snapshot
helpers only apply to snapshot imports.

Keep an existing database instead of importing a dump fixture when `/meili_data`
already contains data:

```java
@Container
static MeilisearchContainer container = new MeilisearchContainer()
    .withDumpImport("meilisearch/fixtures/movies.dump")
    .withIgnoreDumpIfDbExists();
```

Keep an existing database instead of importing a snapshot fixture when
`/meili_data` already contains data:

```java
@Container
static MeilisearchContainer container = new MeilisearchContainer()
    .withSnapshotImport("meilisearch/fixtures/movies.snapshot")
    .withIgnoreSnapshotIfDbExists();
```
