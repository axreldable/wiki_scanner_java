package ru.star.csv;

import ru.star.model.CsvModel;

import java.util.ArrayList;
import java.util.List;

public class CsvWorker {
    private static List<CsvModel> articles = new ArrayList<>();

    public static void addArticle(CsvModel article) {
        articles.add(article);
    }

    public static void printArticles() {
        articles.forEach(System.out::println);
    }
}
