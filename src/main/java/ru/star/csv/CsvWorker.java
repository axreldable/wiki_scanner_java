package ru.star.csv;

import org.apache.log4j.Logger;
import ru.star.model.CsvModel;
import ru.star.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CsvWorker {
    private final static Logger logger = Logger.getLogger(CsvWorker.class);

    private static List<CsvModel> articles = new ArrayList<>();

    public static synchronized void addArticle(CsvModel article) {
        articles.add(article);
    }

    public static void printArticles(String fileName) {
        String recordsForCsv = articles.stream()
                .map(CsvModel::toCsvRow)
                .collect(Collectors.joining(System.getProperty("line.separator")));

        FileUtils.saveToFile(recordsForCsv, fileName);
        logger.info("Print csv file - " + fileName);
    }
}
