package com.example.jacksonparse.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.LocalDateTime;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "address")
public class Address {
    @JacksonXmlProperty(localName = "primary", isAttribute = true)
    private boolean primary = false;
    
    @JacksonXmlProperty(localName = "street")
    private String street;
    
    @JacksonXmlProperty(localName = "city")
    private String city;
    
    @JacksonXmlProperty(localName = "zipCode")
    private String zipCode;

    
    @JacksonXmlProperty(localName = "createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JacksonXmlProperty(localName = "updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;    
}
