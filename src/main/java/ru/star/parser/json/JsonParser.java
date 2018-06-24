package ru.star.parser.json;

import com.google.gson.Gson;
import ru.star.printer.model.ArticleModel;
import ru.star.printer.model.CategoryModel;

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
     * @return List of the {@link CategoryModel}s
     */
    public static List<CategoryModel> parseCategories(String categories) {
        String jsonCategories = new com.google.gson.JsonParser().parse(categories)
                .getAsJsonObject().get("query")
                .getAsJsonObject().get("categorymembers").toString();
        return Arrays.asList(new Gson().fromJson(jsonCategories, CategoryModel[].class));
    }

    /**
     * Parse response for article request.
     *
     * @param article - article response from Wiki
     * @param id      - article's id
     * @return Parsed {@link ArticleModel}
     */
    public static ArticleModel parseArticle(String article, String id) {
        String jsonArticle = new com.google.gson.JsonParser().parse(article)
                .getAsJsonObject().get("query")
                .getAsJsonObject().get("pages")
                .getAsJsonObject().get(id).toString();
        return new Gson().fromJson(jsonArticle, ArticleModel.class);
    }

    public static <T> T parse(String jsonString, Class<T> clazz) {
        return new Gson().fromJson(jsonString, clazz);
    }
}
