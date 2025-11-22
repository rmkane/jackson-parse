package com.example.jacksonparse.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestConstructor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.RequiredArgsConstructor;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class PersonParsingTest {
    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;

    @Test
    void testParseJsonFromResourceFile() throws IOException {
        // Load JSON resource file
        Person person = loadPerson("person.json", jsonMapper);

        // Verify the parsed object
        assertNotNull(person);
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

    @Test
    void testParseXmlFromResourceFile() throws IOException {
        // Load XML resource file
        Person person = loadPerson("person.xml", xmlMapper);

        // Verify the parsed object
        assertNotNull(person);
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

        // Verify extra fields
        assertEquals("A", person.getExtra().get("unknownA"));
        assertEquals("B", person.getExtra().get("unknownB"));
    }

    @Test
    void testJsonAndXmlProduceSameObject() throws IOException {
        // Parse both JSON and XML
        Person jsonPerson = loadPerson("person.json", jsonMapper);
        Person xmlPerson = loadPerson("person.xml", xmlMapper);

        // Verify they produce equivalent objects
        assertEquals(jsonPerson.getId(), xmlPerson.getId());
        assertEquals(jsonPerson.getName(), xmlPerson.getName());
        assertEquals(jsonPerson.getEmail(), xmlPerson.getEmail());
        assertEquals(jsonPerson.getBirthDate(), xmlPerson.getBirthDate());
        assertEquals(jsonPerson.getRegisteredAt(), xmlPerson.getRegisteredAt());

        assertEquals(jsonPerson.getAddresses().size(), xmlPerson.getAddresses().size());
        for (int i = 0; i < jsonPerson.getAddresses().size(); i++) {
            Address jsonAddr = jsonPerson.getAddresses().get(i);
            Address xmlAddr = xmlPerson.getAddresses().get(i);
            assertEquals(jsonAddr.getStreet(), xmlAddr.getStreet());
            assertEquals(jsonAddr.getCity(), xmlAddr.getCity());
            assertEquals(jsonAddr.getZipCode(), xmlAddr.getZipCode());
            assertEquals(jsonAddr.isPrimary(), xmlAddr.isPrimary());
            assertEquals(jsonAddr.getCreatedAt(), xmlAddr.getCreatedAt());
            assertEquals(jsonAddr.getUpdatedAt(), xmlAddr.getUpdatedAt());
        }
    }

    private Person loadPerson(@NonNull String path, ObjectMapper mapper) throws IOException {
        return loadResource(path, Person.class, mapper);
    }

    private <E> E loadResource(@NonNull String path, Class<E> type, ObjectMapper mapper) throws IOException {
        try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
            return mapper.readValue(inputStream, type);
        }
    }
}
