package ru.star;

import org.apache.log4j.Logger;
import ru.star.csv.CsvWorker;
import ru.star.model.Config;
import ru.star.parser.json.Parser;
import ru.star.utils.FileUtils;

import java.util.Arrays;

public class Main {
    private final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        String configJson = FileUtils.readFromFile("app_config.json");
        Config config = Parser.parseConfig(configJson);
        if (!config.validateAndFix()) {
            logger.info("Config is incorrect");
            return;
        }

        String[] categories = config.getStartCategories();
        Arrays.sort(categories);
        for (int i = 1; i <= categories.length; i++) {
            new WikiPrinter(config.getPrintingCount()).print(config.getCrawlingResultsPath(), "0" + i, categories[i-1]);
        }

        CsvWorker.printArticles(config.getResultCsvName());
    }
}
