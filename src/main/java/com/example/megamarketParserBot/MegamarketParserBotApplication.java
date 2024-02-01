package com.example.megamarketParserBot;

import com.example.megamarketParserBot.config.WebDriverHandler;
import com.example.megamarketParserBot.service.Parser;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MegamarketParserBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(MegamarketParserBotApplication.class, args);

		while (true) {
			WebDriver webDriver = WebDriverHandler.webDriverInitializer();
			Parser parser = new Parser(webDriver);
			parser.getOffers();
			webDriver.quit();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
