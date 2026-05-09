# Releasing

This repository publishes separate release lanes for Testcontainers compatibility.

## Release Lanes

| Branch | Release tags | Purpose |
| --- | --- | --- |
| `1.x` | `v1.*` | Maintenance line for Testcontainers 1.x |
| `main` | `v2.*` | Active line for Testcontainers 2.x |

Stable releases are deployed when a GitHub Release is published. Snapshot deployments run from pushes to `main` and `1.x` only when the Maven project version ends with `-SNAPSHOT`.

## Before a Stable Release

1. Ensure all release-scope pull requests are merged into the target branch.
2. Ensure the milestone has no open issues, or move intentionally deferred issues out of the milestone.
3. Run final verification on the target branch:

```bash
./mvnw -B -ntp verify
```

4. Open a release pull request against the target branch:
   - Set `pom.xml` to the release version, for example `2.0.0`.
   - Update README dependency snippets to the same release version.
   - Keep README snippets on the latest stable version after release.
5. Merge the release pull request after CI passes.

## Publishing

1. Create and publish a GitHub Release tag from the target branch.
2. Use tag format `v<version>`, for example `v2.0.0`.
3. The release workflow validates the tag lane:
   - `v1.*` tags must be contained in `1.x`.
   - `v2.*` tags must be contained in `main`.
4. The release workflow sets the Maven version from the tag, verifies the build, and deploys to Central Publishing.

## After a Stable Release

1. Open a post-release pull request against the release branch.
2. Bump `pom.xml` to the next snapshot version, for example `1.2.1-SNAPSHOT`.
3. Leave README dependency snippets on the released stable version.
4. Merge after CI passes.

## Required Repository Secrets

Central Publishing requires these repository secrets:

- `CENTRAL_USERNAME`
- `CENTRAL_TOKEN`
- `GPG_PRIVATE_KEY`
- `GPG_PASSPHRASE`

## Documentation Site

This repository currently has no separate documentation site or documentation deployment workflow. Public documentation lives in `README.md` and Javadocs published with release artifacts.

If a standalone documentation site is added later, add the site build and deployment steps before documenting a docs deployment process here.
