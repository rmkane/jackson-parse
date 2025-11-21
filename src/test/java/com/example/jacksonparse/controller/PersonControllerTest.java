package com.example.jacksonparse.controller;

import com.example.jacksonparse.model.Address;
import com.example.jacksonparse.model.Person;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonControllerTest {
    @Mock
    private HttpServletRequest request;

    private PersonController controller;

    @BeforeEach
    void setUp() {
        controller = new PersonController();
    }

    @Test
    void testCreatePersonWithJsonContentType() {
        Person person = createTestPerson();
        when(request.getContentType()).thenReturn("application/json");
        when(request.getHeader("Accept")).thenReturn("application/json");

        ResponseEntity<Person> response = controller.createPerson(person, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Person responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(person.getId(), responseBody.getId());
        assertEquals(person.getName(), responseBody.getName());
    }

    @Test
    void testCreatePersonWithXmlContentType() {
        Person person = createTestPerson();
        when(request.getContentType()).thenReturn("application/xml");
        when(request.getHeader("Accept")).thenReturn("application/xml");

        ResponseEntity<Person> response = controller.createPerson(person, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Person responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(person.getId(), responseBody.getId());
    }

    @Test
    void testCreatePersonWithNullContentType() {
        Person person = createTestPerson();
        when(request.getContentType()).thenReturn(null);
        when(request.getHeader("Accept")).thenReturn("application/json");

        ResponseEntity<Person> response = controller.createPerson(person, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCreatePersonWithNullAcceptHeader() {
        Person person = createTestPerson();
        when(request.getContentType()).thenReturn("application/json");
        when(request.getHeader("Accept")).thenReturn(null);

        ResponseEntity<Person> response = controller.createPerson(person, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCreatePersonWithBothHeadersNull() {
        Person person = createTestPerson();
        when(request.getContentType()).thenReturn(null);
        when(request.getHeader("Accept")).thenReturn(null);

        ResponseEntity<Person> response = controller.createPerson(person, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testCreatePersonReturnsSamePerson() {
        Person person = createTestPerson();
        when(request.getContentType()).thenReturn("application/json");
        when(request.getHeader("Accept")).thenReturn("application/json");

        ResponseEntity<Person> response = controller.createPerson(person, request);

        assertSame(person, response.getBody());
    }

    private Person createTestPerson() {
        Address address = new Address();
        address.setStreet("123 Main St");
        address.setCity("Springfield");
        address.setZipCode("12345");
        address.setPrimary(true);
        address.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30, 0));
        address.setUpdatedAt(LocalDateTime.of(2024, 1, 20, 14, 45, 0));

        Person person = new Person();
        person.setId(1L);
        person.setName("John Doe");
        person.setEmail("john.doe@example.com");
        person.setAddresses(List.of(address));
        person.setBirthDate(LocalDateTime.of(1990, 5, 15, 8, 0, 0));
        person.setRegisteredAt(LocalDateTime.of(2024, 1, 1, 12, 0, 0));

        return person;
    }
}

