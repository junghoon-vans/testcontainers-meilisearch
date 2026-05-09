# Development

The `main` / `v2.x` branch targets Java 17 or newer and Testcontainers 2.x.

## Requirements

- JDK 17 or newer for local builds and CI parity
- Docker, because tests start real Meilisearch containers through Testcontainers
- Maven Wrapper from this repository (`./mvnw`)

## Branches

- `1.x` is the maintenance lane for Testcontainers 1.x releases and `v1.*` tags.
- `main` is the active lane for Testcontainers 2.x releases and `v2.*` tags.
- Shared changes should land on `1.x` first when they apply to both lanes, then be ported to `main`.

## Local Verification

Run the full verification before opening or updating a pull request:

```bash
./mvnw -B -ntp verify
```

The CI workflow runs verification with the `coverage` profile:

```bash
./mvnw -B -ntp verify -Pcoverage
```

Use a valid local JDK explicitly if your shell points to a stale Java installation:

```bash
JAVA_HOME="$(/usr/libexec/java_home -v 21)" ./mvnw -B -ntp verify
```

## Test Notes

- Unit and integration tests use JUnit 5.
- Testcontainers tests require Docker to be available.
- Fixture import tests use resources under `src/test/resources/meilisearch/fixtures`.
- Snapshot fixtures must be created with the same Meilisearch image version used by the container.

## Pull Requests

- Keep each pull request scoped to one issue or release chore.
- Pair public API changes with tests and README examples.
- Target `1.x` for `v1.*` work and `main` for `v2.*` work.
