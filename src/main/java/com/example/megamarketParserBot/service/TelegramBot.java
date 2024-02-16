package com.example.megamarketParserBot.service;

import com.example.megamarketParserBot.config.BotConfig;
import com.example.megamarketParserBot.config.Urls;
import com.example.megamarketParserBot.config.WebDriverHandler;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private WebDriver webDriver;
    private int counterStart;
    private String testPage;
    private String testPage2;
    private boolean isParsing = false;
    final BotConfig config;
    Map<String, String> offers = new HashMap<>();

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }


    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();


            switch (messageText) {
                case "/go":
                    isParsing = true;
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    executor.submit(() -> startParse(chatId));

                    break;

                case "/start":

                    isParsing = true;
                    executor.submit(() -> startParse(chatId));
                    sendMessage(chatId, "Парсер запущен");
                    break;

                case "/stop":
                    isParsing = false;
                    sendMessage(chatId, "Останавливаю парсер...");
                    break;

                case "/offers":
                    sendOffers(chatId, offers);
                    break;

                case "/test":
                    sendMessage(chatId, counterStart + "");
                    testPage = webDriver.getPageSource();

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("/root/bot/MegamarketParser-Bot/TEST1.txt"))) {
//                        "/root/bot/MegamarketParser-Bot/TEST1.txt"
//                        "C:\\Users\\foxir\\OneDrive\\Рабочий стол\\TEST1.txt"
                        writer.write(testPage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                case "/test2":
                    sendMessage(chatId, counterStart + "");

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("/root/bot/MegamarketParser-Bot/TEST2.txt"))) {
//                        "/root/bot/MegamarketParser-Bot/TEST2.txt"
//                        "C:\\Users\\foxir\\OneDrive\\Рабочий стол\\TEST2.txt"
                        writer.write(testPage2);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                default:
                    sendMessage(chatId, "Команда не поддерживается");
            }
        }

    }

    private void startParse(long chatId) {

        while (isParsing) {
            try {
                sendMessage(chatId, "0");
                webDriver = WebDriverHandler.webDriverInitializer();
                sendMessage(chatId, "1");
                Parser parser = new Parser(webDriver);
                sendMessage(chatId, "2");
                webDriver.navigate().to(Urls.IPHONE15PRO.getUrl());
                sendMessage(chatId, "3");
                parser.parseStart();
                sendMessage(chatId, "4");

                if (parser.findOffer()) {
                    sendMessage(chatId, "Товар появлися в наличии!");
                    webDriver.quit();
                    try {
                        TimeUnit.SECONDS.sleep(30);
                        continue;
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
                sendMessage(chatId, "5");
                offers = parser.getOffers();
                sendMessage(chatId, "6");
                webDriver.quit();

                sendMessage(chatId, "Страница спарсена!");

//                try {
//                    TimeUnit.SECONDS.sleep(30);
//                } catch (InterruptedException e) {
//                }
                counterStart++;
            } catch (Exception e) {
                Parser parser = new Parser(webDriver);
                offers = parser.getOffers();
                webDriver.quit();
                sendMessage(chatId, "Критическая ошибка!!!");
                log.error("Error occurred: " + e.getMessage());
            }
        }

        sendMessage(chatId, "Парсер остановлен");

    }

    public void sendOffers(long chatId, Map<String, String> offers) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String date = LocalDateTime.now().format(formatter);
        StringBuilder messageText = new StringBuilder(date + "\nНайденные предложения:\n");
        for (Map.Entry<String, String> entry : offers.entrySet()) {
            messageText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        sendMessage(chatId, messageText.toString());
    }


    private void startCommandReceived(long chatId, String name) {

        String answer = "Привет, " + name + ", приятно познакомиться";
//        log.info("Replied to user: " + name);

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
//            log.error("Error occurred: " + e.getMessage());
        }

    }


}
