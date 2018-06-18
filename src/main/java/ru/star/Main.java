package ru.star;

import org.apache.log4j.Logger;
import ru.star.csv.CsvWorker;
import ru.star.http.WikiClient;
import ru.star.model.Config;
import ru.star.model.printer.PrintModel;
import ru.star.model.printer.WikiPrinterModel;
import ru.star.model.printer.WikiPrinterParams;
import ru.star.parser.json.Parser;
import ru.star.utils.FileUtils;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

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

        ForkJoinPool pool = new ForkJoinPool(5);

        long startTime;
        long endTime;
        startTime = System.currentTimeMillis();



        for (int i = 1; i <= categories.length; i++) {
//            new WikiPrinter(config.getPrintingCount()).print(config.getCrawlingResultsPath(), "0" + i, categories[i-1]);
            WikiPrinter printer = new WikiPrinter(WikiPrinterModel.builder()
                    .params(WikiPrinterParams.builder()
                            .client(new WikiClient())
                            .articleCounter(new AtomicInteger(0))
                            .printingCount(config.getPrintingCount())
                            .build())
                    .model(PrintModel.builder()
                            .category(categories[i-1])
                            .categoryId("0"+i)
                            .preventDirs(config.getCrawlingResultsPath())
                            .build())
                    .build());
            printer.compute();
        }

        endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " millis");

        CsvWorker.printArticles(config.getResultCsvName());

    }
}
