package org.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataScraper {

    // Method to scrape data from a given URL
    public List<String[]> scrapeData(String url) {
        List<String[]> data = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                data.add(new String[]{ link.attr("abs:href"), link.text() });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
