package com.example.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Customer {
    private Long id;
    private String shortName;
    private String fullName;
    private String city;
    private String postalCode;
}
