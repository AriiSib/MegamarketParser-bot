package com.example.megamarketParserBot.service;

import com.example.megamarketParserBot.config.BotConfig;
import com.example.megamarketParserBot.config.WebDriverHandler;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private boolean isParsing = false;
    final BotConfig config;

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

                default:
                    sendMessage(chatId, "Команда не поддерживается");
            }
        }

    }

    private void startParse(long chatId) {

        try {
            while (isParsing) {
                WebDriver webDriver = WebDriverHandler.webDriverInitializer();
                Parser parser = new Parser(webDriver);
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

                webDriver.quit();

                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                }
            }
            sendMessage(chatId, "Парсер остановлен");
        } catch (Exception e) {
            sendMessage(chatId, "Критическая ошибка!!!");
        }
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
