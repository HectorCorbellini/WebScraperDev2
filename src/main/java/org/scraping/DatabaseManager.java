package org.scraping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseManager {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.database-name}") // New property for database name
    private String dbName; // Field to hold the database name

    // Create the database and table if they don't exist
    public void initializeDatabase() {
        String dbUrlWithoutDb = "jdbc:mysql://localhost:3306/";
        try (Connection conn = DriverManager.getConnection(dbUrlWithoutDb, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {
            // Check and create the database if it doesn't exist
            String createDbSQL = "CREATE DATABASE IF NOT EXISTS " + dbName;
            stmt.executeUpdate(createDbSQL);
            System.out.println("Database checked or created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Connect to the specific database to drop and create the table
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             Statement stmt = conn.createStatement()) {

            // Drop the table if it exists
            String dropTableSQL = "DROP TABLE IF EXISTS scraped_data";
            stmt.executeUpdate(dropTableSQL);
            System.out.println("Table 'scraped_data' dropped successfully if it existed.");

            // Create the table
            String createTableSQL = "CREATE TABLE scraped_data (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "url VARCHAR(2048), " +
                    "text VARCHAR(1024))";
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table 'scraped_data' created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Save scraped data to the database
    public void saveData(List<String[]> data) {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
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
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                data.add(new String[]{rs.getString("url"), rs.getString("text")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }
}
