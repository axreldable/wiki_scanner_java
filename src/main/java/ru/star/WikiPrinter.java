package ru.star;

import ru.star.http.WikiClient;
import ru.star.model.Article;
import ru.star.model.Category;
import ru.star.parser.json.Parser;

import java.util.List;

import static ru.star.parser.json.Parser.parseCategories;

public class WikiPrinter {
    private WikiClient client;
    private int articleCounter;

    public WikiPrinter() {
        client = new WikiClient();
        articleCounter = 0;
    }

    public void print(String category) {
        System.out.println("Печатаю категорию - " + category + "; articleCounter = " + articleCounter);

        String categoriesFromWiki = client.getCategory(category);

        List<Category> categories = parseCategories(categoriesFromWiki);

        for (Category cat : categories) {
            if (articleCounter >= 400) {
                break;
            }
            if (cat.getNs().equals("0") && cat.getType().equals("page")) {
                String articleFromWiki = client.getArticle(cat.getPageId());
                Article article = Parser.parseArticle(articleFromWiki, cat.getPageId());
                System.out.println(article);
                articleCounter++;
                System.out.println("I print article - " + articleCounter);
            } else if (cat.getType().equals("subcat") && cat.getTitle().startsWith("Категория")) {
                print(cat.getTitle().substring("Категория".length()+1));
            }
        }
    }
}
