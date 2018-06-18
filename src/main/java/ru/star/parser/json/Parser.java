package ru.star.parser.json;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import ru.star.model.Article;
import ru.star.model.Category;
import ru.star.model.Config;

import java.util.Arrays;
import java.util.List;

import static ru.star.Constants.*;

/**
 * Wiki response Json Config.
 */
public class Parser {
    /**
     * Parse response for category request.
     *
     * @param categories - category response from Wiki
     * @return List of the {@link Category}s
     */
    public static List<Category> parseCategories(String categories) {
        String jsonCategories = new JsonParser().parse(categories)
                .getAsJsonObject().get(QUERY)
                .getAsJsonObject().get(CATEGORY_MEMBERS).toString();
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
        String jsonArticle = new JsonParser().parse(article)
                .getAsJsonObject().get(QUERY)
                .getAsJsonObject().get(PAGES)
                .getAsJsonObject().get(id).toString();
        return new Gson().fromJson(jsonArticle, Article.class);
    }

    public static Config parseConfig(String jsonConfig) {
        return new Gson().fromJson(jsonConfig, Config.class);
    }
}
