Testcontainers Meilisearch
===

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
DockerImageName imageName = DockerImageName.parse("getmeili/meilisearch:latest");

@Container
MeilisearchContainer container = new MeilisearchContainer(imageName);
```

### Set master key

```java
@Container
MeilisearchContainer container = new MeilisearchContainer()
    .withMasterKey("masterKey");
```

Dependency
---

> **Note**
> This project is not available in `Maven Central` repository.
> So you need to add the `Sonatype Snapshots` repository to your build file.

### Gradle

```groovy
// build.gradle
repositories {
    mavenCentral()
    maven {
        url 'https://s01.oss.sonatype.org/content/repositories/snapshots/'
    }
}

testImplementation 'io.vanslog:testcontainers-meilisearch:1.0.0-SNAPSHOT'
```

### Maven

```xml
<!-- pom.xml -->
<repositories>
    <repository>
        <id>sonatype-snapshots</id>
        <name>Sonatype Snapshots</name>
        <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
    </repository>
</repositories>

<dependency>
    <groupId>io.vanslog</groupId>
    <artifactId>testcontainers-meilisearch</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```
