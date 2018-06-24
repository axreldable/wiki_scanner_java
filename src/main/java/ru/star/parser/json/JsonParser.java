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
    private Gson gson;

    public JsonParser() {
        this.gson = new Gson();
    }

    /**
     * Parse response for category request.
     *
     * @param categories - category response from Wiki
     * @return List of the {@link CategoryModel}s
     */
    public List<CategoryModel> parseCategories(String categories) {
        String jsonCategories = new com.google.gson.JsonParser().parse(categories)
                .getAsJsonObject().get("query")
                .getAsJsonObject().get("categorymembers").toString();
        return Arrays.asList(gson.fromJson(jsonCategories, CategoryModel[].class));
    }

    /**
     * Parse response for article request.
     *
     * @param article - article response from Wiki
     * @param id      - article's id
     * @return Parsed {@link ArticleModel}
     */
    public ArticleModel parseArticle(String article, String id) {
        String jsonArticle = new com.google.gson.JsonParser().parse(article)
                .getAsJsonObject().get("query")
                .getAsJsonObject().get("pages")
                .getAsJsonObject().get(id).toString();
        return gson.fromJson(jsonArticle, ArticleModel.class);
    }

    public <T> T parse(String jsonString, Class<T> clazz) {
        return gson.fromJson(jsonString, clazz);
    }
}
