Testcontainers Meilisearch
===

[![Maven Central](https://img.shields.io/maven-central/v/io.vanslog/testcontainers-meilisearch.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.vanslog/testcontainers-meilisearch/)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=testcontainers-meilisearch&metric=sqale_rating&branch=main)](https://sonarcloud.io/summary/new_code?id=testcontainers-meilisearch&branch=main)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=testcontainers-meilisearch&metric=coverage&branch=main)](https://sonarcloud.io/summary/new_code?id=testcontainers-meilisearch&branch=main)
![LICENSE](https://img.shields.io/github/license/junghoon-vans/testcontainers-meilisearch?label=License)
[![DockerHub](https://img.shields.io/badge/meilisearch-v1.43.0-blue)](https://hub.docker.com/r/getmeili/meilisearch/tags?name=v1.43.0)

A [Testcontainers](https://www.testcontainers.org/) implementation for [Meilisearch](https://www.meilisearch.com/).

How to use
---

Use the `@Container` annotation to start a Meilisearch container in your tests,
then connect clients with the container endpoint and master key.

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

The container supports custom images, master keys, environment modes, log levels,
analytics opt-out, Java SDK client helpers, Spring Boot property wiring, dump
imports, snapshot imports, and existing-database import flags.

See [docs/usage.md](docs/usage.md) for full API examples.

Setup
---

This library is available in Maven Central.
You can add it as a dependency to your project using the following snippets.

### Gradle

```groovy
testImplementation 'io.vanslog:testcontainers-meilisearch:2.0.0'
```

### Maven
```xml
<dependency>
    <groupId>io.vanslog</groupId>
    <artifactId>testcontainers-meilisearch</artifactId>
    <version>2.0.0</version>
    <scope>test</scope>
</dependency>
```

If you use the Meilisearch Java SDK in tests, add:

```xml
<dependency>
    <groupId>com.meilisearch.sdk</groupId>
    <artifactId>meilisearch-java</artifactId>
    <version>0.15.0</version>
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
