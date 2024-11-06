// StaticScraper.java
package org.scraping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class StaticScraper implements CommandLineRunner {

    @Autowired
    private DatabaseManager dbManager;

    @Autowired
    private DataScraper scraper;

    @Autowired
    private DataDisplay dataDisplay;

    public static void main(String[] args) {
        SpringApplication.run(StaticScraper.class, args);
    }

    @Override
    public void run(String... args) {
        // Initialize the database
        dbManager.initializeDatabase();

        // Get URL from the user
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the URL to scrape: ");
        String url = scanner.nextLine();

        // Scrape data, will be modified by developer 1
        List<String[]> scrapedData = scraper.scrapeData(url);

        // Save scraped data to database
        dbManager.saveData(scrapedData);

        // Display data from the database
        dataDisplay.displayData();
    }
}
