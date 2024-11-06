package org.scraping;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseManager {
    // MySQL connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dropi_webscraping_db";
    private static final String USER = "uko";
    private static final String PASS = "placita";

    // Create the database and table if they don't exist
    public void initializeDatabase() {
        String dbName = "dropi_webscraping_db";
        String dbUrlWithoutDb = "jdbc:mysql://localhost:3306/";
        try (Connection conn = DriverManager.getConnection(dbUrlWithoutDb, USER, PASS);
              Statement stmt = conn.createStatement()) {
            // Check and create database if it doesn't exist
            String createDbSQL = "CREATE DATABASE IF NOT EXISTS " + dbName;
            stmt.executeUpdate(createDbSQL);
            System.out.println("Database checked or created successfully.");
        } catch (SQLException e) { e.printStackTrace(); }
        // Now connect to the specific database to create the table if needed
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
              Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS scraped_data (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "url VARCHAR(2048), " +
                    "text VARCHAR(1024))";
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table 'scraped_data' checked or created successfully.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Save scraped data to the database
    public void saveData(List<String[]> data) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String insertSQL = "INSERT INTO scraped_data (url, text) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                for (String[] row : data) {
                    pstmt.setString(1, row[0].length() > 2048 ? row[0].substring(0, 2048) : row[0]);
                    pstmt.setString(2, row[1].length() > 1024 ? row[1].substring(0, 1024) : row[1]);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Retrieve data from the database
    public List<String[]> getData() {
        List<String[]> data = new ArrayList<>();
        String query = "SELECT url, text FROM scraped_data";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                data.add(new String[]{rs.getString("url"), rs.getString("text")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }
}
