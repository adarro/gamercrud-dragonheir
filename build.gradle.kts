plugins {
  java
  alias(libs.plugins.quarkus)
  alias(libs.plugins.lombok)
  alias(libs.plugins.mvnpm.java.native)
}

dependencies {

  implementation(enforcedPlatform(libs.quarkus.platform.bom))
  implementation(libs.renarde)
  implementation(libs.renarde.backoffice)
  implementation(libs.renarde.oidc)
  implementation(libs.quarkus.mailer)
  implementation(libs.renarde.barcode)
  implementation(libs.renarde.pdf)
  // upgrade at some point to ("io.quarkus:quarkus-hibernate-panache-next")
  implementation(libs.quarkus.hibernate.orm.panache)
  implementation(libs.quarkus.web.bundler)
  implementation(libs.quarkus.oidc)
  implementation(libs.quarkus.elytron.security.common)
  implementation(libs.quarkus.smallrye.jwt.build)

  // implementation(libs.quarkus.rest.client)
  // implementation(libs.quarkus.rest.jackson)
  implementation(libs.quarkus.jdbc.postgresql)
  implementation(libs.quarkus.security.webauthn)
  implementation(libs.quarkus.test.security.webauthn)

  implementation(libs.quarkus.jdbc.postgresql)
  //  May need to add these [mvnpm] to test scope?
  runtimeOnly(libs.org.mvnpm.bootstrap)
  runtimeOnly(libs.org.mvnpm.at.popperjs.core)
  runtimeOnly(libs.org.mvnpm.htmx.org)
  runtimeOnly(libs.org.mvnpm.bootstrap.icons)
  testImplementation(libs.renarde.oidc.tests)

//    testImplementation(libs.quarkus.junit5)
//    testImplementation(libs.quarkus.junit5.internal)
//    testImplementation(libs.io.rest.assured.rest.assured)
  testImplementation(libs.com.github.tomakehurst.wiremock)


  testImplementation(libs.quarkus.junit)
  testImplementation(libs.renarde.test)
}

group = "io.truthencode"
version = "1.0.0-SNAPSHOT"

java {
  toolchain {
    languageVersion =
      JavaLanguageVersion.of(
        libs.versions.java
          .get()
          .toInt(),
      )
  }
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
  options.compilerArgs.add("-parameters")
}

dependencyLocking {
  configurations {
    compileClasspath {
      resolutionStrategy.activateDependencyLocking()
    }
  }
}
