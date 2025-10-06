package com.example.db;

import java.io.InputStream;
import java.nio.file.*;
import java.sql.*;
import java.util.Properties;

public class Database {
    private static final String DB_DIR = System.getProperty("user.home") + "/.customer-swing-app";
    private static Connection connection;

    static {
        try {
            // Ensure directory exists
            Path p = Paths.get(DB_DIR);
            if (!Files.exists(p)) Files.createDirectories(p);

            // Initialize connection and schema (do NOT close connection)
            initialize(getConnection());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize DB", e);
        }
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Load DB config from classpath properties
                Properties props = new Properties();
                try (InputStream fis = Database.class.getClassLoader().getResourceAsStream("db.properties")) {
                    if (fis == null) {
                        throw new RuntimeException("db.properties not found in classpath");
                    }
                    props.load(fis);
                }

                String url = props.getProperty("db.url", "jdbc:h2:" + DB_DIR + "/data;AUTO_SERVER=TRUE");
                String user = props.getProperty("db.username", "root");
                String pass = props.getProperty("db.password", "");
                String driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

                Class.forName(driver);

                connection = DriverManager.getConnection(url, user, pass);
            } catch (Exception e) {
                System.err.println("Error connecting to database: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Cannot establish database connection", e);
            }
        }
        return connection;
    }

    private static void initialize(Connection c) throws SQLException {
        try (Statement s = c.createStatement()) { // only Statement closed
            s.execute(
                    "CREATE TABLE IF NOT EXISTS customer (" +
                            "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                            "short_name VARCHAR(50) NOT NULL, " +
                            "full_name VARCHAR(255) NOT NULL, " +
                            "city VARCHAR(100), " +
                            "postal_code VARCHAR(20) NOT NULL)"
            );

            s.execute(
                    "CREATE TABLE IF NOT EXISTS address (" +
                            "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                            "customer_id BIGINT NOT NULL, " +
                            "line1 VARCHAR(80) NOT NULL, " +
                            "line2 VARCHAR(80), " +
                            "line3 VARCHAR(80), " +
                            "FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE)"
            );
        }
    }
}
