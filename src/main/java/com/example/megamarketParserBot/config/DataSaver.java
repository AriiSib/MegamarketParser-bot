package com.example.megamarketParserBot.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class DataSaver {
    private String path;
    private String pageSource;

    public DataSaver(String path, String pageSource) {
        this.path = path;
        this.pageSource = pageSource;
    }
    private static void saveSource(String path, String pageSource) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path));
             BufferedReader reader = new BufferedReader(new FileReader(path))) {
            writer.write(pageSource);

            while (reader.ready()) {
                String line = reader.readLine();
                if (line.contains("Маркетплейс Москва")) {
                    System.out.println("Появился в наличии!");
                } else {
                    System.out.println("Ничего нет");
                }
                break;
            }
        }
    }
}

