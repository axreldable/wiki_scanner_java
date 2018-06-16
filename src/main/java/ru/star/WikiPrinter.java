package ru.star;

import ru.star.http.WikiClient;

public class WikiPrinter {
    private String category;

    public WikiPrinter(String category) {
        this.category = category;
    }

    public void print() {
//        System.out.println(new ru.star.http.WikiClient().getCategory(category));
        System.out.println(new WikiClient().getArticle("6654859"));
    }
}
