package com.antond.config;

import org.junit.jupiter.api.extension.Extension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;

public class MongoContainerExtension implements Extension {

  @Container
  static final GenericContainer<?> CONTAINER = new GenericContainer<>("mongo:latest")
      .withEnv("MONGO_INITDB_ROOT_USERNAME", "admin")
      .withEnv("MONGO_INITDB_ROOT_PASSWORD", "password")
      .withExposedPorts(27017)
      .withCommand("--quiet", "--bind_ip_all")
      .withReuse(false);

  static {
    CONTAINER.start();

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    System.setProperty("DB_HOST", CONTAINER.getHost());
    System.setProperty("DB_PORT", String.valueOf(CONTAINER.getFirstMappedPort()));
    System.setProperty("DB_NAME", "notes");

    System.out.println("✅ MongoDB started with admin:password credentials");
    System.out.println(
        "   Connection: " + CONTAINER.getHost() + ":" + CONTAINER.getFirstMappedPort());
  }
}
