package ru.star.printer;

import org.apache.log4j.Logger;
import ru.star.csv.CsvWorker;
import ru.star.model.Article;
import ru.star.model.CsvModel;
import ru.star.model.PageCategory;
import ru.star.model.PagesPrinterModel;
import ru.star.parser.json.Parser;
import ru.star.utils.FileUtils;

import java.io.File;
import java.util.concurrent.RecursiveAction;

import static ru.star.utils.StringUtils.threeDigit;

public class PagesPrinter extends RecursiveAction {
    private final static Logger logger = Logger.getLogger(PagesPrinter.class);

    private PagesPrinterModel model;

    public PagesPrinter(PagesPrinterModel model) {
        this.model = model;
    }

    @Override
    protected void compute() {
        for (PageCategory page : model.getPages()) {
            String articleFromWiki = model.getWikiPrinterParams().getClient().getArticle(page.getCategory().getPageId());

            Article article = Parser.parseArticle(articleFromWiki, page.getCategory().getPageId());
            logger.debug(article);

            String fileName = createFileName(model.getDirName(), model.getCategoryId(), page.getPageNumber());
            String extract = article.getExtract();
            FileUtils.saveToFile(extract, fileName + ".txt");

            String csvFileId = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            CsvWorker.addArticle(CsvModel.builder()
                    .fileId(csvFileId)
                    .articleName(page.getCategory().getTitle())
                    .url("https://ru.wikipedia.org/wiki/" + page.getCategory().getTitle().replaceAll(" ", "_"))
                    .category(model.getDirName().substring(0, model.getDirName().indexOf('_')))
                    .level((int) csvFileId.chars().filter(ch -> ch == '_').count() - 1)
                    .articleSize(extract.getBytes().length)
                    .build());

            model.getWikiPrinterParams().getArticleCounter().incrementAndGet();
            if (model.getWikiPrinterParams().getArticleCounter().get() >= model.getWikiPrinterParams().getPrintingCount()) return;
        }
    }

    private String createFileName(String dirName, String categoryId, int i) {
        return dirName + File.separator + categoryId + "_" + threeDigit("" + i);
    }
}