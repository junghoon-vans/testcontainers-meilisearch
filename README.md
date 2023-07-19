Testcontainers Meilisearch
===

[![Maven Central](https://img.shields.io/maven-central/v/io.vanslog/testcontainers-meilisearch.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.vanslog%22%20AND%20a:%22testcontainers-meilisearch%22)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=testcontainers-meilisearch&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=testcontainers-meilisearch)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=testcontainers-meilisearch&metric=coverage)](https://sonarcloud.io/summary/new_code?id=testcontainers-meilisearch)
![](https://img.shields.io/github/license/junghoon-vans/testcontainers-meilisearch?label=License)
![](https://img.shields.io/badge/meilisearch-1.2.0-blue)

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

### Setup master key

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
testImplementation 'io.vanslog:testcontainers-meilisearch:1.0.0'
```

### Maven
```xml
<dependency>
    <groupId>io.vanslog</groupId>
    <artifactId>testcontainers-meilisearch</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```
