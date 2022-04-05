package com.alvaria.test.pqueue.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@Testcontainers
@ActiveProfiles("integration-test")
@SpringBootTest
@Import(QueueControllerTestIT.ContextConfiguration.class)
public class QueueControllerTestIT {


  private static final int SERVICE_PORT = 8080;

  @ClassRule
  public static GenericContainer<?> container = new GenericContainer<>("adoptopenjdk/openjdk11:alpine-jre")
      .withExposedPorts(8080)
      .withCopyFileToContainer(MountableFile.forHostPath(Paths.get("target/pqueue.jar")), "/pqueue/pqueue.jar")
      .withCommand("java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "-Dspring.profiles.active=prod", "/pqueue/pqueue.jar")
      .waitingFor(Wait.forLogMessage(".*Started PQueueApplication.*", 1));


  @BeforeAll
  public static void setUp() {
    container.start();
  }

  @Autowired
  private RestTemplate restTemplate;

  @TestConfiguration
  public static class ContextConfiguration {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
      return builder.build();
    }
  }

  @Test
  public void givenProdServiceUp_whenGetHealthRequest_thenReturnOk() {

    ResponseEntity<String> healthResponse = restTemplate.getForEntity(getBaseUrl() + "/actuator/health", String.class);

    assertThat(healthResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(healthResponse.getBody()).contains("\"status\":\"UP\"");
  }

  @Test
  public void givenProdServiceUp_whenGetSwaggerRequest_thenReturnError() {

    Assertions.assertThrows(HttpClientErrorException.class, () -> {
      restTemplate.getForEntity(getBaseUrl() + "/v2/api-docs", String.class);
    });
  }


  private static String getBaseUrl() {
    return "http://" + container.getContainerIpAddress() + ":" + container.getMappedPort(SERVICE_PORT);
  }


}
