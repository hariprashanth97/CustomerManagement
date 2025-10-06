package com.example.db;

import com.example.model.Customer;
import com.example.model.Address;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {

    // ------------------- Customer Methods -------------------

    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        Connection c = Database.getConnection(); // persistent connection
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id, short_name, full_name, city, postal_code FROM customer ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Customer(
                        rs.getLong("id"),
                        rs.getString("short_name"),
                        rs.getString("full_name"),
                        rs.getString("city"),
                        rs.getString("postal_code")
                ));
            }
        }
        return list;
    }

    public Customer findById(long id) throws SQLException {
        Connection c = Database.getConnection();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id, short_name, full_name, city, postal_code FROM customer WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getLong("id"),
                            rs.getString("short_name"),
                            rs.getString("full_name"),
                            rs.getString("city"),
                            rs.getString("postal_code")
                    );
                }
            }
        }
        return null;
    }

    public Customer save(Customer cust) throws SQLException {
        Connection c = Database.getConnection();
        if (cust.getId() == null) { // INSERT
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO customer (short_name, full_name, city, postal_code) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, cust.getShortName());
                ps.setString(2, cust.getFullName());
                ps.setString(3, cust.getCity());
                ps.setString(4, cust.getPostalCode());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) cust.setId(keys.getLong(1));
                }
            }
        } else { // UPDATE
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE customer SET short_name=?, full_name=?, city=?, postal_code=? WHERE id=?")) {
                ps.setString(1, cust.getShortName());
                ps.setString(2, cust.getFullName());
                ps.setString(3, cust.getCity());
                ps.setString(4, cust.getPostalCode());
                ps.setLong(5, cust.getId());
                ps.executeUpdate();
            }
        }
        return cust;
    }

    public void delete(long id) throws SQLException {
        Connection c = Database.getConnection();
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM customer WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // ------------------- Address Methods -------------------

    public List<Address> findAddresses(long customerId) throws SQLException {
        List<Address> list = new ArrayList<>();
        Connection c = Database.getConnection();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id, customer_id, line1, line2, line3 FROM address WHERE customer_id = ? ORDER BY id")) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Address(
                            rs.getLong("id"),
                            rs.getLong("customer_id"),
                            rs.getString("line1"),
                            rs.getString("line2"),
                            rs.getString("line3")
                    ));
                }
            }
        }
        return list;
    }

    public Address saveAddress(Address a) throws SQLException {
        Connection c = Database.getConnection();
        if (a.getId() == null) { // INSERT
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO address (customer_id, line1, line2, line3) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, a.getCustomerId());
                ps.setString(2, a.getLine1());
                ps.setString(3, a.getLine2());
                ps.setString(4, a.getLine3());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) a.setId(keys.getLong(1));
                }
            }
        } else { // UPDATE
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE address SET line1=?, line2=?, line3=? WHERE id=?")) {
                ps.setString(1, a.getLine1());
                ps.setString(2, a.getLine2());
                ps.setString(3, a.getLine3());
                ps.setLong(4, a.getId());
                ps.executeUpdate();
            }
        }
        return a;
    }

    public void deleteAddress(long id) throws SQLException {
        Connection c = Database.getConnection();
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM address WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public int countAddresses(long customerId) throws SQLException {
        Connection c = Database.getConnection();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(1) FROM address WHERE customer_id = ?")) {
            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }
}
