package ru.star;

import org.apache.log4j.Logger;
import ru.star.csv.CsvWorker;
import ru.star.http.WikiClient;
import ru.star.model.Config;
import ru.star.model.printer.ExecutorModel;
import ru.star.model.printer.PrintModel;
import ru.star.model.printer.WikiPrinterModel;
import ru.star.model.printer.WikiPrinterParams;
import ru.star.parser.json.Parser;
import ru.star.printer.WikiPrinter;
import ru.star.utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        long startTime;
        long endTime;
        startTime = System.currentTimeMillis();

        String configJson = FileUtils.readFromFile("app_config.json");
        Config config = Parser.parseConfig(configJson);
        if (!config.validateAndFix()) {
            logger.info("Config is incorrect");
            return;
        }

        String[] categories = config.getStartCategories();
        Arrays.sort(categories);

        ExecutorService executor = Executors.newFixedThreadPool(categories.length);
        List<Callable<String>> todo = new ArrayList<>(categories.length);

        List<ExecutorService> executorServices = new ArrayList<>(categories.length);
        for (int i = 1; i <= categories.length; i++) {
            executorServices.add(Executors.newFixedThreadPool(config.getCrawlingThreadsCount()));
        }

        for (int i = 1; i <= categories.length; i++) {
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
                    .executorModel(ExecutorModel.builder()
                            .executor(executorServices.get(i-1))
                            .threadsCount(config.getCrawlingThreadsCount())
                            .build())
                    .build());
            todo.add(printer);
        }

        executor.invokeAll(todo); // waits all tasks here
        executor.shutdown();

        for (int i = 1; i <= categories.length; i++) {
            executorServices.get(i-1).shutdown();
        }

        endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " millis"); // Time taken: 92010 millis

        CsvWorker.printArticles(config.getResultCsvName());
    }
}
