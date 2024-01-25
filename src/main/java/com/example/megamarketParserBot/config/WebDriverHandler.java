package com.example.megamarketParserBot.config;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WebDriverHandler {
    private static int pageLoadExceptionCounter;

    /**
     * Решить проблему с долгиим ожиданием загрузки (указать максимально ожидание загрузки страницы)
     *
     * @throws Exception
     */

    public static WebDriver webDriverInitializer() {
        WebDriverHandler webDriverHandler = new WebDriverHandler();
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriver webDriver = new ChromeDriver();
        setPageLoadTimeout(webDriver);

        getPage(webDriver);

        pageLoadExceptionCounter = 0;

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return webDriver;
    }

    private static void setPageLoadTimeout(WebDriver webDriver) {
        try {
            webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
        } catch (TimeoutException e) {
            if (++pageLoadExceptionCounter == 10) {
                webDriver.quit();
                /**
                 * Присылать в бот уведомление, что страница долгое время не грузится
                 */
                return; // тут точно ретерн нужен?
            }
        }
    }

    private static void getPage(WebDriver webDriver) {
        try {
            webDriver.get(Urls.IPHONE15PRO.getUrl());
        } catch (Exception e) {
            if (++pageLoadExceptionCounter == 10) {
                webDriver.quit();
                /**
                 * Присылать в бот уведомление, что неудается получить GET страницы
                 */
                return; // тут точно ретерн нужен?
            }
        }

        clickButtonCity(webDriver);
    }

    private static void clickButtonCity(WebDriver webDriver) {
        /**
         * Разобраться с тем, что кнопка может не появиться.
         */
        try {
//            webDriver.findElement(By.cssSelector("button.btn.header-region-selector-view__footer-ok.xs")).click();
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.btn.header-region-selector-view__footer-ok.xs"))).click();
        } catch (Exception e) {
            if (++pageLoadExceptionCounter == 10) {
                webDriver.quit();
                /**
                 * Присылать в бот уведомление, что неудается получить GET страницы
                 */
                return; // тут точно ретерн нужен?
            }
        }
    }

}

