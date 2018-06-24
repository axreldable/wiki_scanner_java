package ru.star.parser.json;

import ru.star.printer.model.ArticleModel;
import ru.star.printer.model.CategoryModel;

import java.util.List;

/**
 * Class for json parsing in the project.
 */
public class WikiParser implements JParser {
    /**
     * Parse response for category request.
     *
     * @param categories - category response from Wiki
     * @return List of the {@link CategoryModel}s
     */
    public List<CategoryModel> parseCategories(String categories) {
        return parseArray(getInnerJson(categories, "query", "categorymembers"), CategoryModel[].class);
    }

    /**
     * Parse response for article request.
     *
     * @param article - article response from Wiki
     * @param id      - article's id
     * @return Parsed {@link ArticleModel}
     */
    public ArticleModel parseArticle(String article, String id) {
        return parse(getInnerJson(article, "query", "pages", id), ArticleModel.class);
    }
}
