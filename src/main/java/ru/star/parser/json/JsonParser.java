package ru.star.parser.json;

import com.google.gson.Gson;
import ru.star.printer.model.Article;
import ru.star.printer.model.Category;

import java.util.Arrays;
import java.util.List;

/**
 * Wiki response Json ConfigModel.
 */
public class JsonParser {
    /**
     * Parse response for category request.
     *
     * @param categories - category response from Wiki
     * @return List of the {@link Category}s
     */
    public static List<Category> parseCategories(String categories) {
        String jsonCategories = new com.google.gson.JsonParser().parse(categories)
                .getAsJsonObject().get("query")
                .getAsJsonObject().get("categorymembers").toString();
        return Arrays.asList(new Gson().fromJson(jsonCategories, Category[].class));
    }

    /**
     * Parse response for article request.
     *
     * @param article - article response from Wiki
     * @param id      - article's id
     * @return Parsed {@link Article}
     */
    public static Article parseArticle(String article, String id) {
        String jsonArticle = new com.google.gson.JsonParser().parse(article)
                .getAsJsonObject().get("query")
                .getAsJsonObject().get("pages")
                .getAsJsonObject().get(id).toString();
        return new Gson().fromJson(jsonArticle, Article.class);
    }

    public static <T> T parse(String jsonString, Class<T> clazz) {
        return new Gson().fromJson(jsonString, clazz);
    }
}
