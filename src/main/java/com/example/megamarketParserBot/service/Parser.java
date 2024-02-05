package com.example.megamarketParserBot.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    private WebDriver webDriver;
    private List<WebElement> offerElements;


    public Parser(WebDriver webDriver) {
        this.webDriver = webDriver;
        parseStart();
    }

    private void parseStart() {
        parsePage(webDriver);
    }

    private void parsePage(WebDriver webDriver) {
        offerElements = webDriver.findElements(By.className("product-offer")).isEmpty() ?
                  webDriver.findElements(By.className("pdp-sales-block"))
                : webDriver.findElements(By.className("product-offer"));
    }

    public Map<String, String> getOffers() {
        Map<String, String> offers = new HashMap<>();
        for (WebElement element : offerElements) {
            WebElement merchantNameElement = element.findElement(By.className("pdp-merchant-rating-block__merchant-name"));
            String merchantName = merchantNameElement.getText();
            WebElement priceElement = element.findElement(By.cssSelector("meta[itemprop='price']"));
            String price = priceElement.getAttribute("content");
            offers.put(merchantName, price);
//            System.out.println(merchantName + " " + price);
        }

//        System.out.println();

        return offers;
    }

    /**
     * Будущий фильтр
     */
    public boolean findOffer() {
        Map<String, String> offers = getOffers();
        for (Map.Entry<String, String> offer : offers.entrySet()) {
            if (offer.getKey().equalsIgnoreCase("Мегамаркет Москва")) {
                return true;
            }
        }
        return false;
    }

    public String serverTest() {
        return webDriver.getPageSource();
    }

}

