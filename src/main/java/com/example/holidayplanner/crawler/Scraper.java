package com.example.holidayplanner.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Scraper {
    public static void main(String[] args) {
        var url = "https://www.airbnb.co.uk";

        try {
            Connection conn = Jsoup.connect(url);
            Document doc = conn.get();

            if (conn.response().statusCode() == 200) {
                Elements accommodations = doc.select(" dir dir-ltr");

                for (var accommodation : accommodations) {
                    String title = accommodation.select("a > ").text();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void scrape(int level, String url, ArrayList<String> visitedUrls) {

        if (level <= 5) {
            Document doc = request(url, visitedUrls);

            if (doc != null) {
                System.out.println("Received web page at " + url);
                System.out.println(doc.text());

                var links = doc.select("a[href]");

                for (var link : links) {
                    var nextUrl = link.absUrl("href");

                    if (!visitedUrls.contains(nextUrl)) {
                        scrape(level++, nextUrl, visitedUrls);
                    }
                }
            } else {
                System.out.println("Null document received at " + url);
            }

        }

    }

    private static Document request(String url, ArrayList<String> visited) {
        try {
            Connection conn = Jsoup.connect(url);
            Document doc = conn.get();

            if (conn.response().statusCode() == 200) {
                visited.add(url);

                return doc;
            }
            return doc;
        } catch (IOException e) {
            Logger.getGlobal().warning("Error connecting to " + url);
        }
        return null;
    }
}
