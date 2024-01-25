package com.example.megamarketParserBot.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

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
        offerElements = webDriver.findElements(By.className("product-offer"));
    }

    public void viewOffer() {
        for (WebElement element : offerElements) {
            WebElement merchantNameElement = element.findElement(By.className("pdp-merchant-rating-block__merchant-name"));
            String merchantName = merchantNameElement.getText();
            WebElement priceElement = element.findElement(By.cssSelector("meta[itemprop='price']"));
            String price = priceElement.getAttribute("content");
            System.out.println(merchantName + " " + price);
        }

        System.out.println();
    }

}

