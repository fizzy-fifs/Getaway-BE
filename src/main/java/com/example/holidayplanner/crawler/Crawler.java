package com.example.holidayplanner.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Crawler {

    public static void main(String[] args) throws UnsupportedEncodingException {
        String url = "https://www.airbnb.co.uk/?checkin=2024-12-18&checkout=2024-12-27&adults=10&price_filter_num_nights=9&price_max=610&price_min=590";

        crawl(1, url, new ArrayList<>());
    }

    private static void crawl(int level, String url, ArrayList<String> visitedUrls) throws UnsupportedEncodingException {
        if (level <= 5) {
            Document doc = request(url, visitedUrls);

            if (doc != null) {
                for (Element link : doc.select("a[href]")) {
                    String next_link = link.absUrl("href").trim();
                    String encodedLink = encodeUrl(next_link);
                    if (url.contains("airbnb")) {
                        System.out.println("Next Link: " + next_link);
                    }

                    if (!visitedUrls.contains(next_link)) {
                        crawl(level++, encodedLink, visitedUrls);
                    }
                }
            }
        }
    }

    private static Document request(String url, ArrayList<String> visited) {
        try {
            Connection conn = Jsoup.connect(url).ignoreContentType(true).ignoreHttpErrors(true);
            Document doc = conn.get();

            if (conn.response().statusCode() == 200) {
                if (url.contains("airbnb")) {
                    System.out.println("Title: " + doc.title());
                    System.out.println("Link: " + url);
                }
                visited.add(url);

                return doc;
            }

            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String encodeUrl(String url) throws UnsupportedEncodingException {
        int indexOfLastSlash = url.lastIndexOf('/');
        if (indexOfLastSlash == -1) {
            return URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
        }

        String baseUrl = url.substring(0, indexOfLastSlash + 1);
        String pathComponent = url.substring(indexOfLastSlash + 1);
        String encodedPathComponent = URLEncoder.encode(pathComponent, StandardCharsets.UTF_8.toString()).replace("+", "%20");

        return baseUrl + encodedPathComponent;
    }
}
