package com.example.holidayplanner.scraper;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scraper {

    public static void main(String[] args) throws IOException {
        String url = "https://www.airbnb.co.uk/?checkin=2024-12-18&checkout=2024-12-27&adults=10&price_filter_num_nights=9&price_max=610&price_min=590";

        HtmlPage searchPage;
        try (WebClient client = new WebClient(BrowserVersion.BEST_SUPPORTED)) {
            client.getOptions().setJavaScriptEnabled(false);
            client.getOptions().setThrowExceptionOnScriptError(false);
            searchPage = client.getPage(url);
        }

        List listingsDivs = searchPage.getByXPath(".//div[contains(@class, 'dir dir-ltr')]");

        System.out.println("Size of Listings Divs: " + listingsDivs.size());
        System.out.println("Listings Divs: " + listingsDivs);

//        if (!listingsDivs.isEmpty() && listingsDivs.get(0) instanceof HtmlDivision) {
//            List<String> listingsLinks = new ArrayList<>();
//
//            for (HtmlDivision div : (List<HtmlDivision>) listingsDivs) {
//                listingsLinks.add(div.getByXPath(".//a").toString());
//                //div.getAttribute("href");
//            }
//
//            System.out.println("Listings: " + listingsLinks.size());
//            System.out.println("Listings Links: " + listingsLinks);
//        }
    }
}


//gsgwcjk atm_1d13e1y_k75hcd atm_yrukzc_wwb3ei atm_10yczz8_kb7nvz atm_10yczz8_cs5v99__1ldigyt atm_10yczz8_11wpgbn__1v156lz atm_10yczz8_egatvm__qky54b atm_10yczz8_qfx8er__1xolj55 atm_10yczz8_ouytup__w5e62l g14v8520 atm_9s_11p5wf0 atm_d5_j5tqy atm_d7_1ymvx20 atm_dl_1mvrszh atm_dz_hxz02 dir dir-ltr