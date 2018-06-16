package ru.star.parser.json;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import ru.star.model.Article;
import ru.star.model.Category;

import java.util.Arrays;
import java.util.List;

public class Parser {
    public static List<Category> parseCategories(String categories) {
        String jsonCategories = new JsonParser().parse(categories)
                .getAsJsonObject().get("query")
                .getAsJsonObject().get("categorymembers").toString();
        return Arrays.asList(new Gson().fromJson(jsonCategories, Category[].class));
    }

    public static Article parseArticle(String article, String id) {
        String jsonArticle = new JsonParser().parse(article)
                .getAsJsonObject().get("query")
                .getAsJsonObject().get("pages")
                .getAsJsonObject().get(id).toString();
        return new Gson().fromJson(jsonArticle, Article.class);
    }
}
