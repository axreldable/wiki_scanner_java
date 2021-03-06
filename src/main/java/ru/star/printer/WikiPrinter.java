package ru.star.printer;

import org.apache.log4j.Logger;
import ru.star.printer.model.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static ru.star.utils.FileUtils.createDirs;

/**
 * Task for separate category.
 * It creates directory for the Wiki category,
 * sorts Wiki pages and prints them in several threads and starts sub tasks for sub categories printing.
 */
public class WikiPrinter implements Callable<Object> {
    private final static Logger logger = Logger.getLogger(WikiPrinter.class);

    private WikiPrinterModel model;

    public WikiPrinter(WikiPrinterModel model) {
        this.model = model;
    }

    @Override
    public Object call() throws InterruptedException {
        if (model.getParams().getArticleCounter().get() >= model.getParams().getPrintingCount()) return null;

        String dirName = createDirName(
                model.getDirName().getPreventDirs(),
                model.getDirName().getCategoryId(),
                model.getDirName().getCategory());
        createDirs(dirName);
        logger.info(
                "Печатаю категорию - " + model.getDirName().getCategory() + ";" +
                        " articleCounter = " + model.getParams().getArticleCounter()
        );

        String categoriesFromWiki;
        try {
            categoriesFromWiki = model.getParams().getClient().getCategory(model.getDirName().getCategory());
        } catch (URISyntaxException | IOException e) {
            logger.error("Exception during get category from Wiki", e);
            return null;
        }

        List<CategoryModel> categories = model.getParser().parseCategories(categoriesFromWiki);

        if (printAllPages(categories, dirName)) return null;

        List<CategoryModel> subCategories = categories.stream()
                .filter(cat -> cat.getType().equals("subcat") && cat.getTitle().startsWith("Категория"))
                .sorted(Comparator.comparing(CategoryModel::getTitle))
                .collect(Collectors.toList());
        printSubCategories(subCategories, dirName);
        return null;
    }

    /**
     * Sorts pages from the category and print them to the directory in several threads.
     *
     * @param categories - list of the pages and sub categories from the category
     * @param dirName    - path to article storing
     * @return was we printed enough articles
     * @throws InterruptedException if any of sub tasks was interrupt
     */
    private boolean printAllPages(List<CategoryModel> categories, String dirName) throws InterruptedException {
        AtomicInteger i = new AtomicInteger(0);
        List<PageCategoryModel> pages = categories.stream()
                .filter(cat -> cat.getNs().equals("0") && cat.getType().equals("page"))
                .sorted(Comparator.comparing(CategoryModel::getTitle))
                .map(x -> new PageCategoryModel(x, i.incrementAndGet()))
                .collect(Collectors.toList());

        List<List<PageCategoryModel>> partsOfPages = split(pages, model.getExecutor().getThreadsCount());
        List<Callable<Object>> todo = new ArrayList<>(model.getExecutor().getThreadsCount());

        for (List<PageCategoryModel> pagesList : partsOfPages) {
            todo.add(new PagesPrinter(PagesPrinterModel.builder()
                    .wikiPrinterParams(model.getParams())
                    .pages(pagesList)
                    .dirName(dirName)
                    .categoryId(model.getDirName().getCategoryId())
                    .parser(model.getParser())
                    .build()));
        }

        model.getExecutor().getExecutor().invokeAll(todo); // waits all tasks here
        return model.getParams().getArticleCounter().get() >= model.getParams().getPrintingCount();
    }

    /**
     * Starts tasks for printing sub categories.
     *
     * @param subCategories - sub categories for printing
     * @param dirName       - path for categories storing
     * @throws InterruptedException if any of sub tasks was interrupt
     */
    private void printSubCategories(List<CategoryModel> subCategories, String dirName) throws InterruptedException {
        int i = 0;
        for (CategoryModel cat : subCategories) {
            i++;
            String nextCat = cat.getTitle().substring("Категория".length() + 1);

            WikiPrinter printer = new WikiPrinter(WikiPrinterModel.builder()
                    .params(WikiPrinterParamsModel.builder()
                            .client(model.getParams().getClient())
                            .articleCounter(model.getParams().getArticleCounter())
                            .printingCount(model.getParams().getPrintingCount())
                            .build())
                    .dirName(DirNameModel.builder()
                            .category(nextCat)
                            .categoryId(model.getDirName().getCategoryId() + "_" + format("%03d", i))
                            .preventDirs(dirName)
                            .build())
                    .executor(model.getExecutor())
                    .parser(model.getParser())
                    .build());
            printer.call();
        }
    }

    /**
     * Method for dir name generation.
     *
     * @param preventDirs - parent dir name
     * @param categoryId  - id of the category
     * @param category    - name of the category
     * @return generated dir name
     */
    private String createDirName(String preventDirs, String categoryId, String category) {
        return preventDirs + (preventDirs.equals("") ? "" : File.separator) + categoryId + "_" + category;
    }

    /**
     * Split list of the pages in several partitions.
     *
     * @param ar            - list for splitting
     * @param partitionSize - partition size
     * @return list of the lists
     */
    private List<List<PageCategoryModel>> split(List<PageCategoryModel> ar, int partitionSize) {
        List<List<PageCategoryModel>> rez = new ArrayList<>();
        for (int i = 0; i < ar.size(); i += partitionSize) {
            rez.add(ar.subList(i, Math.min(i + partitionSize, ar.size())));
        }
        return rez;
    }
}
