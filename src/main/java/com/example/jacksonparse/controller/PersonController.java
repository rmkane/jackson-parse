package com.example.jacksonparse.controller;

import com.example.jacksonparse.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/person")
public class PersonController {
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                 produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Person> createPerson(
            @RequestBody Person person,
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Accept", required = false) String accept) {
        log.info("POST /api/person - Content-Type: {}, Accept: {}, Person[id={}, name={}]",
                contentType, accept, person.getId(), person.getName());
        return ResponseEntity.ok(person);
    }
}

