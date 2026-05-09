Testcontainers Meilisearch
===

[![Maven Central](https://img.shields.io/maven-central/v/io.vanslog/testcontainers-meilisearch.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.vanslog/testcontainers-meilisearch/)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=testcontainers-meilisearch&metric=sqale_rating&branch=1.x)](https://sonarcloud.io/summary/new_code?id=testcontainers-meilisearch&branch=1.x)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=testcontainers-meilisearch&metric=coverage&branch=1.x)](https://sonarcloud.io/summary/new_code?id=testcontainers-meilisearch&branch=1.x)
![LICENSE](https://img.shields.io/github/license/junghoon-vans/testcontainers-meilisearch?label=License)
[![DockerHub](https://img.shields.io/badge/meilisearch-v1.43.0-blue)](https://hub.docker.com/r/getmeili/meilisearch/tags?name=v1.43.0)

A [Testcontainers](https://www.testcontainers.org/) implementation for [Meilisearch](https://www.meilisearch.com/).

How to use
---

Use the `@Container` annotation to start a Meilisearch container in your tests.

### Default image

```java
@Container
MeilisearchContainer container = new MeilisearchContainer();
```

### Custom image

```java
@Container
MeilisearchContainer container = new MeilisearchContainer(
    DockerImageName.parse("getmeili/meilisearch:v1.43.0"));
```

### Configure master key

```java
@Container
MeilisearchContainer container = new MeilisearchContainer()
    .withMasterKey("masterKey");
```

### Configure environment mode

```java
@Container
MeilisearchContainer container = new MeilisearchContainer()
    .withEnvMode(MeilisearchEnvMode.DEVELOPMENT);
```

### Configure log level

```java
@Container
MeilisearchContainer container = new MeilisearchContainer()
    .withLogLevel(MeilisearchLogLevel.DEBUG);
```

### Disable analytics

```java
@Container
MeilisearchContainer container = new MeilisearchContainer()
    .withNoAnalytics();
```

### Java SDK client setup

The container exposes helpers for the Meilisearch Java SDK:

```java
Client client = new Client(new Config(container.getEndpoint(), container.getMasterKey()));
```

### JUnit 5 integration

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

### Spring Boot integration

For Spring Boot tests, register your own application properties from the container.
Spring Boot 3.1+ can also wire custom containers through its Testcontainers
support if you prefer.

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

### Index documents and wait for tasks

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

### Import a dump fixture

```java
@Container
static MeilisearchContainer container = new MeilisearchContainer()
    .withMasterKey("masterKey")
    .withDumpImport("meilisearch/fixtures/movies.dump");
```

### Import a snapshot fixture

```java
@Container
static MeilisearchContainer container = new MeilisearchContainer()
    .withMasterKey("masterKey")
    .withSnapshotImport("meilisearch/fixtures/movies.snapshot");
```

Snapshots are exact copies of Meilisearch data and must be created with the same
Meilisearch version as the container image that imports them. Use dumps when you
need a fixture that can move across Meilisearch versions.

Dump and snapshot imports are strict by default. Only one import source can be
configured per container, and dump helpers only apply to dump imports while
snapshot helpers only apply to snapshot imports. Add the matching helpers when
you want Meilisearch to keep an existing database instead of importing a fixture:

```java
@Container
static MeilisearchContainer container = new MeilisearchContainer()
    .withDumpImport("meilisearch/fixtures/movies.dump")
    .withIgnoreDumpIfDbExists();
```

```java
@Container
static MeilisearchContainer container = new MeilisearchContainer()
    .withSnapshotImport("meilisearch/fixtures/movies.snapshot")
    .withIgnoreSnapshotIfDbExists();
```

Setup
---

This library is available in Maven Central.
You can add it as a dependency to your project using the following snippets.

### Gradle

```groovy
testImplementation 'io.vanslog:testcontainers-meilisearch:1.1.0'
```

### Maven
```xml
<dependency>
    <groupId>io.vanslog</groupId>
    <artifactId>testcontainers-meilisearch</artifactId>
    <version>1.1.0</version>
    <scope>test</scope>
</dependency>
```

If you use the Meilisearch Java SDK in tests on Java 17 or later, add:

```xml
<dependency>
    <groupId>com.meilisearch.sdk</groupId>
    <artifactId>meilisearch-java</artifactId>
    <version>0.20.1</version>
    <scope>test</scope>
</dependency>
```

Development
---

See [DEVELOPMENT.md](DEVELOPMENT.md) for local setup, verification commands,
branch lanes, and Testcontainers testing notes.

Releasing
---

See [RELEASING.md](RELEASING.md) for stable release, snapshot deployment, and
post-release version bump steps.

Release lanes
---

This project publishes separate release lanes for Testcontainers compatibility:

| Branch | Release tags | Purpose |
| --- | --- | --- |
| `1.x` | `v1.*` | Maintenance line for Testcontainers 1.x |
| `main` | `v2.*` | Active line for Testcontainers 2.x |

Snapshot deployments run from pushes to `main` and `1.x` when the Maven project version ends with `-SNAPSHOT`. Stable releases are deployed only when publishing a GitHub Release whose tag matches the target branch, for example `v1.0.7` from `1.x` or `v2.0.0` from `main`.
