package ru.star;

import org.apache.log4j.Logger;
import ru.star.http.WikiClient;
import ru.star.model.Article;
import ru.star.model.Category;
import ru.star.parser.json.Parser;
import ru.star.utils.FileUtils;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.star.parser.json.Parser.parseCategories;
import static ru.star.utils.FileUtils.createDir;
import static ru.star.utils.StringUtils.threeDigit;

public class WikiPrinter {
    private final static Logger logger = Logger.getLogger(WikiPrinter.class);

    private static final int MAX_ARTICLE = 20;

    private WikiClient client;
    private int articleCounter;

    public WikiPrinter() {
        client = new WikiClient();
        articleCounter = 0;
    }

    public void print(String preventDirs, String categoryId, String category) {
        if (articleCounter >= MAX_ARTICLE) return;

        String dirName = createDirName(preventDirs, categoryId, category);
        createDir(dirName);
        logger.info("Печатаю категорию - " + category + "; articleCounter = " + articleCounter);

        String categoriesFromWiki = client.getCategory(category);
        if (categoriesFromWiki == null) return;

        List<Category> categories = parseCategories(categoriesFromWiki);

        List<Category> pages = categories.stream()
                .filter(cat -> cat.getNs().equals("0") && cat.getType().equals("page"))
                .sorted(Comparator.comparing(Category::getTitle))
                .collect(Collectors.toList());
        printPages(pages, dirName, categoryId);
        if (articleCounter >= MAX_ARTICLE) return;

        List<Category> subCategories = categories.stream()
                .filter(cat -> cat.getType().equals("subcat") && cat.getTitle().startsWith("Категория"))
                .sorted(Comparator.comparing(Category::getTitle))
                .collect(Collectors.toList());
        printSubCategories(subCategories, dirName, categoryId);
    }

    private void printSubCategories(List<Category> subCategories, String dirName, String categoryId) {
        int i = 0;
        for (Category cat : subCategories) {
            i++;
            String nextCat = cat.getTitle().substring("Категория".length() + 1);
            print(dirName, categoryId + "_" + threeDigit("" + i), nextCat);
        }
    }

    private void printPages(List<Category> pages, String dirName, String categoryId) {
        int i = 0;
        for (Category page : pages) {
            i++;
            String articleFromWiki = client.getArticle(page.getPageId());

            Article article = Parser.parseArticle(articleFromWiki, page.getPageId());
            logger.debug(article);

            FileUtils.saveToFile(article.getExtract(), dirName + File.separator + categoryId + "_" + threeDigit("" + i) + "_" + page.getTitle() + ".txt");
            articleCounter++;
            if (articleCounter >= MAX_ARTICLE) return;
        }
    }

    private String createDirName(String preventDirs, String categoryId, String category) {
        return preventDirs + (preventDirs.equals("") ? "" : File.separator) + categoryId + "_" + category;
    }
}
