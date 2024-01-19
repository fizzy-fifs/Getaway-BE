package com.example.holidayplanner.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

public class Crawler {

    public static void main(String[] args) {
        String url = "https://www.airbnb.co.uk/?checkin=2024-12-18&checkout=2024-12-27&adults=10&price_filter_num_nights=9&price_max=610&price_min=590";

        crawl(1, url, new ArrayList<>());
    }

    private  static void crawl(int level, String url, ArrayList<String> visitedUrls) {
        if (level <= 5) {
            Document doc = request(url, visitedUrls);

            if (doc != null) {
                for (Element link : doc.select("a[href]")) {
                    System.out.println(link);

                    String next_link = link.absUrl("href");
                    System.out.println(next_link);

                    if (!visitedUrls.contains(next_link)) {
                        crawl(level++, next_link, visitedUrls);
                    }
                }
            }
        }
    }

    private static Document request(String url, ArrayList<String> visited) {
        try {
           Connection conn = Jsoup.connect(url);
           Document doc = conn.get();

           if (conn.response().statusCode() == 200){
               System.out.println(doc.title());
               System.out.println("Link: " + url);
               visited.add(url);

               return doc;
           }

           return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
