package com.example.megamarketParserBot.config;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--display=:1");
//        chromeOptions.addArguments("user-agent=Mozilla/5.0 (X11; Ubuntu; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.6167.139 Safari/537.36");

        chromeOptions.addArguments("user-agent=Mozilla/5.0 (Windows NT 11.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.6167.140 Safari/537.36");
        chromeOptions.addArguments("referer=https://www.google.com/search?q=%D1%81%D0%B1%D0%B5%D1%80%D0%BC%D0%B5%D0%B3%D0%B0%D0%BC%D0%B0%D1%80%D0%BA%D0%B5%D1%82&sca_esv=5f32bda464ccee7b&ei=mvrAZc3EDM2nwPAPi4aikAs&oq=%D1%81%D0%B1%D0%B5%D1%80%D0%BC%D0%B5&gs_lp=Egxnd3Mtd2l6LXNlcnAiDNGB0LHQtdGA0LzQtSoCCAAyCBAAGIAEGLADMggQABiABBiwAzIIEAAYgAQYsAMyCBAAGIAEGLADMggQABiABBiwAzIIEAAYgAQYsAMyCBAAGIAEGLADMggQABiABBiwAzIIEAAYgAQYsAMyBxAAGB4YsANI9QtQAFgAcAF4AJABAJgBAKABAKoBALgBA8gBAOIDBBgBIEGIBgGQBgo&sclient=gws-wiz-serp");
        chromeOptions.addArguments("accept-language=ru-Ru");

//        chromeOptions.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
//         chromeOptions.setExperimentalOption("useAutomationExtension", false);
        chromeOptions.addArguments("--disable-blink-features=AutomationControlled");

//        chromeOptions.addArguments("--headless=chrome");
        chromeOptions.addArguments("--no-sandbox");
        // /root/bot/MegamarketParser-Bot/chromedriver


//        String proxyAddress = "193.187.107.219";
//        int proxyPort = 62924;
//        String proxyUsername = "iwCDSmtB";
//        String proxyPassword = "n9YEBAah";

//        chromeOptions.addArguments("--proxy-server=http://" + proxyAddress + ":" + proxyPort);

        ChromeDriver webDriver = new ChromeDriver(chromeOptions);
        webDriver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", Map.of(
                "source", "delete window.cdc_adoQpoasnfa76pfcZLmcfl_Array;" +
                        "delete window.cdc_adoQpoasnfa76pfcZLmcfl_Promise;" +
                        "delete window.cdc_adoQpoasnfa76pfcZLmcfl_Symbol;" +
                        "delete window.cdc_adoQpoasnfa76pfcZLmcfl_JSON;" +
                        "delete window.cdc_adoQpoasnfa76pfcZLmcfl_Proxy;" +
                        "delete window.cdc_adoQpoasnfa76pfcZLmcfl_Object;"
        ));

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
            webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        } catch (TimeoutException e) {
            if (++pageLoadExceptionCounter == 10) {
                webDriver.quit();
                /**
                 * Присылать в бот уведомление, что страница долгое время не грузится
                 */
                return;
            }
        }
    }

    private static void getPage(WebDriver webDriver) {
        try {
            webDriver.get(Urls.TEST.getUrl());

        } catch (Exception e) {
            if (++pageLoadExceptionCounter == 10) {
                webDriver.quit();
                /**
                 * Присылать в бот уведомление, что неудается получить GET страницы
                 */
                return;
            }
        }

        try {
//            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            clickButtonCity(webDriver);
        } catch (Exception e) {
        }
    }

    private static void clickButtonCity(WebDriver webDriver) {
        /**
         * Разобраться с тем, что кнопка может не появиться.
         */
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20));
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

