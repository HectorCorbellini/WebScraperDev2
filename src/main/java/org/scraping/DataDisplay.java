package org.scraping;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataDisplay {
    private DatabaseManager dbManager;

    public DataDisplay(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    // display data in the console
    public void displayData() {
        List<String[]> data = dbManager.getData();
        // Log each data row
        for (String[] row : data) {
            System.out.println("URL: " + row[0] + ", Text: " + row[1]);
        }
    }
}
