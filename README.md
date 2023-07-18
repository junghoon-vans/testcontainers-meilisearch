Testcontainers Meilisearch
===

A [Testcontainers](https://www.testcontainers.org/) implementation for [Meilisearch](https://www.meilisearch.com/).

How to use
---

You can use the `@Container` annotation to start a Meilisearch container.

### Default image

```java
@Container
MeiliSearchContainer container = new MeiliSearchContainer();
```

### Custom image

```java
DockerImageName imageName = DockerImageName.parse("getmeili/meilisearch:latest");

@Container
MeiliSearchContainer container = new MeiliSearchContainer(imageName);
```
