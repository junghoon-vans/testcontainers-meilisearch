Testcontainers Meilisearch
===

[![Maven Central](https://img.shields.io/maven-central/v/io.vanslog/testcontainers-meilisearch.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.vanslog/testcontainers-meilisearch/)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=testcontainers-meilisearch&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=testcontainers-meilisearch)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=testcontainers-meilisearch&metric=coverage)](https://sonarcloud.io/summary/new_code?id=testcontainers-meilisearch)
![LICENSE](https://img.shields.io/github/license/junghoon-vans/testcontainers-meilisearch?label=License)
[![DockerHub](https://img.shields.io/badge/meilisearch-v1.3.4-blue)](https://hub.docker.com/layers/getmeili/meilisearch/v1.3.4/images/sha256-3a577f9952b1c9886adbea3742de012ca202bbe69a7943695b6edd8073425376?context=explore)

A [Testcontainers](https://www.testcontainers.org/) implementation for [Meilisearch](https://www.meilisearch.com/).

How to use
---

You can use the `@Container` annotation to start a Meilisearch container.

### Default image

```java
@Container
MeilisearchContainer container = new MeilisearchContainer();
```

### Custom image

```java
@Container
MeilisearchContainer container = new MeilisearchContainer(DockerImageName.parse("getmeili/meilisearch:latest"));
```

### Configure master key

```java
@Container
MeilisearchContainer container = new MeilisearchContainer()
    .withMasterKey("masterKey");
```

Setup
---

This library is available in Maven Central.
You can add it as a dependency to your project using the following snippets.

### Gradle

```groovy
testImplementation 'io.vanslog:testcontainers-meilisearch:1.0.5'
```

### Maven
```xml
<dependency>
    <groupId>io.vanslog</groupId>
    <artifactId>testcontainers-meilisearch</artifactId>
    <version>1.0.5</version>
    <scope>test</scope>
</dependency>
```

Release lanes
---

This project publishes separate release lanes for Testcontainers compatibility:

| Branch | Release tags | Purpose |
| --- | --- | --- |
| `1.x` | `v1.*` | Maintenance line for Testcontainers 1.x |
| `main` | `v2.*` | Active line for Testcontainers 2.x |

Snapshot deployments run from pushes to `main` and `1.x` when the Maven project version ends with `-SNAPSHOT`. Stable releases are deployed only when publishing a GitHub Release whose tag matches the target branch, for example `v1.0.7` from `1.x` or `v2.0.0` from `main`.
