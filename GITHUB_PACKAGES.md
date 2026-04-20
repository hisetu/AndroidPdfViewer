# GitHub Packages Maven

This fork publishes `android-pdf-viewer` to GitHub Packages instead of JitPack.

## Publish

The publish command is unchanged:

```bash
./gradlew :android-pdf-viewer:publishReleasePublicationToGitHubPackagesRepository
```

Under the hood this now works in two steps:

1. Publish the `release` publication to Maven local.
2. Upload the generated `.aar`, `.pom`, `.module`, `-sources.jar`, and `-javadoc.jar` files to GitHub Packages with authenticated HTTP `PUT` requests.

Set credentials in `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_TOKEN
```

You can also provide them with Gradle properties or environment variables:

```bash
GITHUB_ACTOR=YOUR_GITHUB_USERNAME
GITHUB_TOKEN=YOUR_GITHUB_TOKEN
```

Token scopes:

```text
Publish: write:packages
Consume: read:packages
```

Priority order is:

```text
-Pgpr.user / -Pgpr.key
~/.gradle/gradle.properties or local.properties
GITHUB_ACTOR / GITHUB_TOKEN
```

Note:

```text
This custom upload flow is intended for fixed-version releases. It does not add extra logic to update remote maven-metadata.xml.
```

Published coordinates:

```text
com.github.hisetu:android-pdf-viewer:3.2.0-beta.3-zoomfix1
```

## Consume

Add the GitHub Packages Maven repository:

```kotlin
maven {
    url = uri("https://maven.pkg.github.com/hisetu/AndroidPdfViewer")
    credentials {
        username = providers.gradleProperty("gpr.user").orNull
            ?: System.getenv("GITHUB_ACTOR")
        password = providers.gradleProperty("gpr.key").orNull
            ?: System.getenv("GITHUB_TOKEN")
    }
}
```

Use the dependency:

```kotlin
implementation("com.github.hisetu:android-pdf-viewer:3.2.0-beta.3-zoomfix1")
```
