package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Address {
    private Long id;
    private Long customerId;
    private String line1;
    private String line2;
    private String line3;
}
