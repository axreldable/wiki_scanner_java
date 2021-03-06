package ru.star.printer;

import org.apache.log4j.Logger;
import ru.star.csv.CsvProducer;
import ru.star.printer.model.ArticleModel;
import ru.star.printer.model.CsvModel;
import ru.star.printer.model.PageCategoryModel;
import ru.star.printer.model.PagesPrinterModel;
import ru.star.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

import static java.lang.String.format;

/**
 * Task for saving Wiki articles to files and adds csv models to the queue for adding to the result file.
 */
class PagesPrinter implements Callable<Object>, CsvProducer {
    private final static Logger logger = Logger.getLogger(PagesPrinter.class);

    private PagesPrinterModel model;

    PagesPrinter(PagesPrinterModel model) {
        this.model = model;
    }

    @Override
    public String call() {
        for (PageCategoryModel page : model.getPages()) {
            String articleFromWiki;
            try {
                articleFromWiki = model.getWikiPrinterParams().getClient().getArticle(page.getCategory().getPageId());
            } catch (URISyntaxException | IOException e) {
                logger.error("Exception during get article from Wiki", e);
                continue;
            }

            ArticleModel article = model.getParser().parseArticle(articleFromWiki, page.getCategory().getPageId());
            logger.debug(article);

            String fileName = createFileName(model.getDirName(), model.getCategoryId(), page.getPageNumber());
            String extract = article.getExtract();
            FileUtils.saveToFile(extract, fileName + ".txt");

            String csvFileId = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            addCsv(CsvModel.builder()
                    .fileId(csvFileId)
                    .articleName(page.getCategory().getTitle())
                    .url("https://ru.wikipedia.org/wiki/" + page.getCategory().getTitle().replaceAll(" ", "_"))
                    .category(model.getDirName().substring(0, model.getDirName().indexOf('_')))
                    .level((int) csvFileId.chars().filter(ch -> ch == '_').count() - 1)
                    .articleSize(extract.getBytes().length)
                    .build());

            model.getWikiPrinterParams().getArticleCounter().incrementAndGet();
            logger.debug(model.getWikiPrinterParams().getArticleCounter().get());
            if (model.getWikiPrinterParams().getArticleCounter().get() >= model.getWikiPrinterParams().getPrintingCount())
                return null;
        }
        return null;
    }

    /**
     * Method for file name generation.
     *
     * @param dirName    - parent dir name
     * @param categoryId - id of the category
     * @param i          - order number
     * @return generated file name
     */
    private String createFileName(String dirName, String categoryId, int i) {
        return dirName + File.separator + categoryId + "_" + format("%03d", i);
    }
}
