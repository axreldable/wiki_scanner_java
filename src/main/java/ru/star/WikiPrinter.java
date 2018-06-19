package ru.star;

import org.apache.log4j.Logger;
import ru.star.csv.CsvWorker;
import ru.star.model.Article;
import ru.star.model.Category;
import ru.star.model.CsvModel;
import ru.star.model.printer.PrintModel;
import ru.star.model.printer.WikiPrinterModel;
import ru.star.model.printer.WikiPrinterParams;
import ru.star.parser.json.Parser;
import ru.star.utils.FileUtils;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static ru.star.parser.json.Parser.parseCategories;
import static ru.star.utils.FileUtils.createDir;
import static ru.star.utils.StringUtils.threeDigit;

public class WikiPrinter implements Callable<String> {
    private final static Logger logger = Logger.getLogger(WikiPrinter.class);

    private WikiPrinterModel model;

    public WikiPrinter(WikiPrinterModel model) {
        this.model = model;
    }

    @Override
    public String call() {
        if (model.getParams().getArticleCounter().get() >= model.getParams().getPrintingCount()) return "done";

        String dirName = createDirName(
                model.getModel().getPreventDirs(),
                model.getModel().getCategoryId(),
                model.getModel().getCategory());
        createDir(dirName);
        logger.info(
                "Печатаю категорию - " + model.getModel().getCategory() + ";" +
                        " articleCounter = " + model.getParams().getArticleCounter()
        );

        String categoriesFromWiki = model.getParams().getClient().getCategory(model.getModel().getCategory());
        if (categoriesFromWiki == null) return "done";

        List<Category> categories = parseCategories(categoriesFromWiki);

        List<Category> pages = categories.stream()
                .filter(cat -> cat.getNs().equals("0") && cat.getType().equals("page"))
                .sorted(Comparator.comparing(Category::getTitle))
                .collect(Collectors.toList());
        printPages(pages, dirName, model.getModel().getCategoryId());
        if (model.getParams().getArticleCounter().get() >= model.getParams().getPrintingCount()) return "done";

        List<Category> subCategories = categories.stream()
                .filter(cat -> cat.getType().equals("subcat") && cat.getTitle().startsWith("Категория"))
                .sorted(Comparator.comparing(Category::getTitle))
                .collect(Collectors.toList());
        printSubCategories(subCategories, dirName);
        return "done";
    }

    private void printSubCategories(List<Category> subCategories, String dirName) {
        int i = 0;
        for (Category cat : subCategories) {
            i++;
            String nextCat = cat.getTitle().substring("Категория".length() + 1);
//            print(dirName, categoryId + "_" + threeDigit("" + i), nextCat);
            WikiPrinter printer = new WikiPrinter(WikiPrinterModel.builder()
                    .params(WikiPrinterParams.builder()
                            .client(model.getParams().getClient())
                            .articleCounter(model.getParams().getArticleCounter())
                            .printingCount(model.getParams().getPrintingCount())
                            .build())
                    .model(PrintModel.builder()
                            .category(nextCat)
                            .categoryId(model.getModel().getCategoryId() + "_" + threeDigit("" + i))
                            .preventDirs(dirName)
                            .build())
                    .build());
            printer.call();
        }
    }

    private void printPages(List<Category> pages, String dirName, String categoryId) {
        int i = 0;
        for (Category page : pages) {
            i++;
            String articleFromWiki = model.getParams().getClient().getArticle(page.getPageId());

            Article article = Parser.parseArticle(articleFromWiki, page.getPageId());
            logger.debug(article);

            String fileName = createFileName(dirName, categoryId, i);
            String extract = article.getExtract();
            FileUtils.saveToFile(extract, fileName + ".txt");

            String csvFileId = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            CsvWorker.addArticle(CsvModel.builder()
                    .fileId(csvFileId)
                    .articleName(page.getTitle())
                    .url("https://ru.wikipedia.org/wiki/" + page.getTitle().replaceAll(" ", "_"))
                    .category(dirName.substring(0, dirName.indexOf('_')))
                    .level((int) csvFileId.chars().filter(ch -> ch == '_').count() - 1)
                    .articleSize(extract.getBytes().length)
                    .build());

            model.getParams().getArticleCounter().addAndGet(1);
            if (model.getParams().getArticleCounter().get() >= model.getParams().getPrintingCount()) return;
        }
    }

    private String createFileName(String dirName, String categoryId, int i) {
        return dirName + File.separator + categoryId + "_" + threeDigit("" + i);
    }

    private String createDirName(String preventDirs, String categoryId, String category) {
        return preventDirs + (preventDirs.equals("") ? "" : File.separator) + categoryId + "_" + category;
    }
}
