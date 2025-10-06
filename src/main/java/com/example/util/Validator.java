package com.example.util;

public class Validator {
    // simple postal code validation: digits only, length 5-8. Modify to suit country rules.
    public static boolean isValidPostal(String postal) {
        if (postal == null) return false;
        return postal.matches("\\d{5,8}");
    }

    public static boolean isValidAddressLine(String s) {
        return s != null && !s.trim().isEmpty() && s.length() <= 80;
    }

    public static boolean isValidOptionalAddressLine(String s) {
        return s == null || s.length() <= 80;
    }

    /** Short Name: required, max 50 chars */
    public static boolean isValidShortName(String s) {
        if (s == null) return false;
        s = s.trim();
        return !s.isEmpty() && s.length() <= 50;
    }

    /** Full Name: required, max 255 chars */
    public static boolean isValidFullName(String s) {
        if (s == null) return false;
        s = s.trim();
        return !s.isEmpty() && s.length() <= 255;
    }

    /** City: optional, max 100 chars */
    public static boolean isValidCity(String s) {
        if (s == null) return true; // optional
        return s.length() <= 100;
    }
}
