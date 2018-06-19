package ru.star.printer;

import org.apache.log4j.Logger;
import ru.star.model.Category;
import ru.star.model.PageCategory;
import ru.star.model.PagesPrinterModel;
import ru.star.model.printer.PrintModel;
import ru.star.model.printer.WikiPrinterModel;
import ru.star.model.printer.WikiPrinterParams;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.star.parser.json.Parser.parseCategories;
import static ru.star.utils.FileUtils.createDir;
import static ru.star.utils.StringUtils.threeDigit;

public class WikiPrinter implements Callable<Object> {
    private final static Logger logger = Logger.getLogger(WikiPrinter.class);

    private WikiPrinterModel model;

    public WikiPrinter(WikiPrinterModel model) {
        this.model = model;
    }

    @Override
    public String call() throws InterruptedException {
        if (model.getParams().getArticleCounter().get() >= model.getParams().getPrintingCount()) return null;

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
        if (categoriesFromWiki == null) return null;

        List<Category> categories = parseCategories(categoriesFromWiki);

        if (printAllPages(categories, dirName)) return null;

        List<Category> subCategories = categories.stream()
                .filter(cat -> cat.getType().equals("subcat") && cat.getTitle().startsWith("Категория"))
                .sorted(Comparator.comparing(Category::getTitle))
                .collect(Collectors.toList());
        printSubCategories(subCategories, dirName);
        return null;
    }

    private boolean printAllPages(List<Category> categories, String dirName) throws InterruptedException {
        AtomicInteger i = new AtomicInteger(0);
        List<PageCategory> pages = categories.stream()
                .filter(cat -> cat.getNs().equals("0") && cat.getType().equals("page"))
                .sorted(Comparator.comparing(Category::getTitle))
                .map(x -> new PageCategory(x, i.incrementAndGet()))
                .collect(Collectors.toList());

        List<List<PageCategory>> partsOfPages = split(pages, model.getExecutorModel().getThreadsCount());
        List<Callable<Object>> todo = new ArrayList<>(model.getExecutorModel().getThreadsCount());

        for (List<PageCategory> pagesList : partsOfPages) {
            todo.add(new PagesPrinter(PagesPrinterModel.builder()
                    .wikiPrinterParams(model.getParams())
                    .pages(pagesList)
                    .dirName(dirName)
                    .categoryId(model.getModel().getCategoryId())
                    .build()));
        }

        model.getExecutorModel().getExecutor().invokeAll(todo); // waits all tasks here
        return model.getParams().getArticleCounter().get() >= model.getParams().getPrintingCount();
    }

    private void printSubCategories(List<Category> subCategories, String dirName) throws InterruptedException {
        int i = 0;
        for (Category cat : subCategories) {
            i++;
            String nextCat = cat.getTitle().substring("Категория".length() + 1);

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
                    .executorModel(model.getExecutorModel())
                    .build());
            printer.call();
        }
    }

    private String createDirName(String preventDirs, String categoryId, String category) {
        return preventDirs + (preventDirs.equals("") ? "" : File.separator) + categoryId + "_" + category;
    }

    private List<List<PageCategory>> split(List<PageCategory> ar, int partitionSize) {
        List<List<PageCategory>> rez = new ArrayList<>();
        for (int i = 0; i < ar.size(); i += partitionSize) {
            rez.add(ar.subList(i, Math.min(i + partitionSize, ar.size())));
        }
        return rez;
    }
}
