# gamercrud-dragonheir

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Overview

This is a simple Data centered template designed to be used to quickly get you up and running
in a data based site.


## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./gradlew build -Dquarkus.native.enabled=true
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/gamercrud-dragonheir-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/gradle-tooling>.

## Related Guides

- Renarde ([guide](https://quarkiverse.github.io/quarkiverse-docs/quarkus-renarde/dev/index.html)): Renarde is a server-side Web Framework based on Quarkus, Qute, Hibernate and RESTEasy Reactive.

## Provided Code

### Renarde

This is a small Renarde webapp. Once the quarkus app is started visit http://localhost:8080/renarde

[Related guide section...](https://quarkiverse.github.io/quarkiverse-docs/quarkus-renarde/dev/index.html)

## Acknowlegements

The general scaffolding is implemented using the [Quarkus Renarde TODO demo app](https://github.com/FroMage/quarkus-renarde-todo) for out of the box OIDC login, CRUD Database entities and basic front end web site.  The original TODO app may be removed in the near future.

