package net.bank.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * TODO:
 * 1. DONE - Make Container point to a Test Database (mongodb with TestContainer) instead of MongoDB Atlas
 * 2. DONE - Wipe and re-initialise Test Database on startup
 * 3. DONE - Handle exceptions like creating the same record multiple times
 * 4. DONE - Use Eureka Service Discovery
 * 5. DONE - Use Zuul Gateway
 * 6. Spring WebFlux WebSocket and Vue.js
 * 7. DONE - SLF4J
 * 8. DONE - Use TestContainer with Docker compose for Integration Tests
 * 9. DONE - Use JUnit 5 for Integration Tests
 *
 * Advanced TODO:
 * 1. HTTPS
 * 2. Local "Docker registry"
 * 3. Use WebTestClient instead of RestTemplate (can be done only with WebFLux)
 * 4. DONE - Use git-commit-id-plugin / Maven
 * 5. Use Ignite / Redis / Hazelcast
 * 6. DONE - Fix HATEOAS and ZUUL issue
 * 7. Add AUDIT trail on tables, logging action per user
 */
@Slf4j
public class IntegrationTests {

	/*
	 * TestContainers configuration
	 */
	private static final boolean useTestContainers = false;
	private static final String dockerComposeFilename = "docker-compose.yml";
	private static final String mainServiceName = "bank-account";
	private static final String mainServiceHost = "localhost";
	private static final int mainServicePort = 8080;
	private static final String mainServiceEntryPoint = "/accounts";
	private static final String mainServiceBaseUrl = String.format("http://%s:%d",
			mainServiceHost,
			mainServicePort
	);
	private static final HttpWaitStrategy mainServiceUp = Wait.forHttp(mainServiceEntryPoint).forStatusCode(HttpStatus.OK.value());

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@ClassRule
	public static DockerComposeContainer serviceContainers =
			new DockerComposeContainer(new File(dockerComposeFilename))
					.withExposedService(String.format("%s_1", mainServiceName),
							mainServicePort,
							mainServiceUp);

	@BeforeAll
	public static void setUp() {
		if (useTestContainers) {
			serviceContainers.start();
		}
	}

	@AfterAll
	public static void tearDown() {
		if (useTestContainers) {
			serviceContainers.stop();
		}
	}

	@Test
	public void testOne() throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject sarahAccount = new JSONObject();
		sarahAccount.put("holder", "Valentin");
		sarahAccount.put("amount", 500);
		HttpEntity<String> postData = new HttpEntity<>(sarahAccount.toString(), headers);
		URI postUri = toUri(mainServiceBaseUrl, mainServiceEntryPoint);
		log.info("POST: {} to {}", sarahAccount.toString(), postUri);

		RestTemplate restTemplate = new RestTemplate();
		String res =
				restTemplate.postForObject(postUri, postData, String.class);
		JsonNode root = objectMapper.readTree(res);
		log.info("RESPONSE: {}", root.toPrettyString());

	}

	private static URI toUri(String baseUrl, String relativePath) {
		String fullUrl = String.format("%s%s", baseUrl, relativePath);
		URI uri = null;
		try {
			uri = new URI(baseUrl);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return uri;
	}

}
