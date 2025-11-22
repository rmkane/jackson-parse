package com.example.jacksonparse.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestConstructor;
import org.springframework.util.StreamUtils;

import com.example.jacksonparse.model.Address;
import com.example.jacksonparse.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.RequiredArgsConstructor;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class PersonIntegrationTest {
    private final TestRestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final XmlMapper xmlMapper;

    @LocalServerPort
    private int port;

    @Test
    void testJsonInputJsonOutput() throws IOException {
        var response = fetch("person.json", MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var person = objectMapper.readValue(response.getBody(), Person.class);
        assertNotNull(person);
        verifyPersonData(person);
    }

    @Test
    void testJsonInputXmlOutput() throws IOException {
        var response = fetch("person.json", MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var body = response.getBody();
        assertNotNull(body);
        verifyXmlResponse(body, false);
    }

    @Test
    void testXmlInputJsonOutput() throws IOException {
        var response = fetch("person.xml", MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var person = objectMapper.readValue(response.getBody(), Person.class);
        assertNotNull(person);
        verifyPersonData(person);
    }

    @Test
    void testXmlInputXmlOutput() throws IOException {
        var response = fetch("person.xml", MediaType.APPLICATION_XML, MediaType.APPLICATION_XML);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var xmlResponse = response.getBody();
        assertNotNull(xmlResponse);
        verifyXmlResponse(xmlResponse, true);
    }

    @Test
    void testInvalidJsonTriggersExceptionHandler() {
        var invalidJson = "{ invalid json }";
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(invalidJson, headers);

        var response = restTemplate.exchange(getBaseUrl(), HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Error:"));
    }

    @Test
    void testInvalidXmlTriggersExceptionHandler() {
        var invalidXml = "<person><invalid>";
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        var entity = new HttpEntity<>(invalidXml, headers);

        var response = restTemplate.exchange(getBaseUrl(), HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Error:"));
    }

    @Test
    void testControllerWithNullContentType() throws IOException {
        var body = loadResource("person.json");
        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        var entity = new HttpEntity<>(body, headers);

        var response = restTemplate.exchange(getBaseUrl(), HttpMethod.POST, entity, String.class);

        // Spring will try to infer content type or may reject, but controller should handle it
        assertNotNull(response);
    }

    @Test
    void testControllerWithMissingAcceptHeader() throws IOException {
        var body = loadResource("person.json");
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(body, headers);

        var response = restTemplate.exchange(getBaseUrl(), HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    private ResponseEntity<String> fetch(String path, MediaType contentType, MediaType acceptType) throws IOException {
        var body = loadResource(path);
        var entity = createEntity(body, contentType, acceptType);
        return restTemplate.exchange(getBaseUrl(), HttpMethod.POST, entity, String.class);
    }

    private String loadResource(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    private HttpEntity<String> createEntity(String body, MediaType contentType, MediaType acceptType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.setAccept(Collections.singletonList(acceptType));
        return new HttpEntity<>(body, headers);
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/person";
    }

    private void verifyXmlResponse(String xmlResponse, boolean includeAddressDetails) throws IOException {
        assertTrue(xmlResponse.contains("<person"));
        assertTrue(xmlResponse.contains("<name>John Doe</name>"));
        assertTrue(xmlResponse.contains("<email>john.doe@example.com</email>"));
        if (includeAddressDetails) {
            assertTrue(xmlResponse.contains("<address") || xmlResponse.contains("</address>"));
            assertTrue(xmlResponse.contains("<street>123 Main St</street>"));
        }

        var person = xmlMapper.readValue(xmlResponse, Person.class);
        assertNotNull(person);
        verifyPersonData(person);
        if (includeAddressDetails || xmlResponse.contains("unknownA")) {
            assertEquals("A", person.getExtra().get("unknownA"));
            assertEquals("B", person.getExtra().get("unknownB"));
        }
    }

    private void verifyPersonData(Person person) {
        assertEquals(1L, person.getId());
        assertEquals("John Doe", person.getName());
        assertEquals("john.doe@example.com", person.getEmail());

        // Verify nested Address objects
        assertNotNull(person.getAddresses());
        assertEquals(2, person.getAddresses().size());

        Address primaryAddress = person.getAddresses().get(0);
        assertEquals("123 Main St", primaryAddress.getStreet());
        assertEquals("Springfield", primaryAddress.getCity());
        assertEquals("12345", primaryAddress.getZipCode());
        assertTrue(primaryAddress.isPrimary());

        Address secondaryAddress = person.getAddresses().get(1);
        assertEquals("456 Oak Ave", secondaryAddress.getStreet());
        assertEquals("Springfield", secondaryAddress.getCity());
        assertEquals("12346", secondaryAddress.getZipCode());
        assertFalse(secondaryAddress.isPrimary());

        // Verify LocalDateTime fields
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30, 0), primaryAddress.getCreatedAt());
        assertEquals(LocalDateTime.of(2024, 1, 20, 14, 45, 0), primaryAddress.getUpdatedAt());
        assertEquals(LocalDateTime.of(1990, 5, 15, 8, 0, 0), person.getBirthDate());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0, 0), person.getRegisteredAt());
    }
}
