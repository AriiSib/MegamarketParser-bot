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
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    webDriver = WebDriverHandler.webDriverInitializer();
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
                    webDriver.navigate().to(Urls.IPHONE15PRO.getUrl());
                    sendOffers(chatId, offers);
                    break;

                case "/test":
                    sendMessage(chatId, counterStart+"");
                    try(BufferedWriter writer = new BufferedWriter(new FileWriter("/root/bot/MegamarketParser-Bot/TESTMESSAGE.txt"))) {
//                        "/root/bot/MegamarketParser-Bot/TESTMESSAGE.txt"
//                        "C:\\Users\\foxir\\OneDrive\\Рабочий стол\\TTEST.txt"
                        writer.write(testPage);
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

        try {
            while (isParsing) {
                Parser parser = new Parser(webDriver);
                testPage = parser.serverTest().isEmpty() ? testPage : parser.serverTest();
                if (parser.findOffer()) {
                    sendMessage(chatId, "Товар появлися в наличии!");
                    webDriver.navigate().refresh();
                    try {
                        TimeUnit.SECONDS.sleep(30);
                        continue;
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
                offers = parser.getOffers();
                webDriver.navigate().refresh();

                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                }
                counterStart++;
            }
            sendMessage(chatId, "Парсер остановлен");
        } catch (Exception e) {
            sendMessage(chatId, "Критическая ошибка!!!");
            log.error("Error occurred: " + e.getMessage());
        }
    }

    public void sendOffers(long chatId, Map<String, String> offers) {
        StringBuilder messageText = new StringBuilder("Найденные предложения:\n");
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
