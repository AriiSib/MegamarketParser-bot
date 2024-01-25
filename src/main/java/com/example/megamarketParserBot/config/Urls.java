package com.example.megamarketParserBot.config;

public enum Urls {
    /**
     * Сохранить это все в проперти и тащить от туда
     */

    PATH("C:\\Users\\foxir\\OneDrive\\Рабочий стол\\page.txt"),
    IPHONE15PRO("https://megamarket.ru/catalog/details/smartfon-apple-iphone-15-pro-128-gb-nano-sim-esim-natural-titanium-100061025299/"),

    MVIDEO("https://megamarket.ru/catalog/details/apple-iphone-15-pro-128gb-natural-titanium-100062422149/#?details_block=prices"),

    BUDS("https://megamarket.ru/catalog/details/naushniki-oneplus-buds-pro-2-e507a-besprovodnye-chernye-100050653829/#?details_block=prices"),

    TEST("https://megamarket.ru/catalog/details/kofe-v-zernah-lavazza-qualita-oro-100022776412/#?details_block=prices");

    private final String url;

    Urls(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
