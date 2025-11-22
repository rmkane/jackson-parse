package com.example.jacksonparse.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jacksonparse.model.Person;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/person")
public class PersonController {
    @Operation(summary = "Create or process a person", description = "Accepts and returns person data in JSON or XML format based on Content-Type and Accept headers", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Person object in JSON or XML format", required = true, content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE),
            @Content(mediaType = MediaType.APPLICATION_XML_VALUE) }), responses = {
                    @ApiResponse(responseCode = "200", description = "Person processed successfully", content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Person.class)),
                            @Content(mediaType = MediaType.APPLICATION_XML_VALUE, schema = @Schema(implementation = Person.class)) }) })
    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<Person> createPerson(@RequestBody Person person, HttpServletRequest request) {
        String contentType = request.getContentType();
        String accept = request.getHeader("Accept");
        log.info("POST /api/person - Content-Type: {}, Accept: {}, Person[id={}, name={}]", contentType, accept,
                person.getId(), person.getName());
        return ResponseEntity.ok(person);
    }
}
