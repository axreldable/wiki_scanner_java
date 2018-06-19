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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

        ExecutorService categoryExecutor = Executors.newFixedThreadPool(categories.length);
        List<Callable<Object>> todo = new ArrayList<>(categories.length);

        List<ExecutorService> executorsForPages = initExecutors(categories.length, config.getCrawlingThreadsCount());

        for (int i = 0; i < categories.length; i++) {
            WikiPrinter printer = new WikiPrinter(WikiPrinterModel.builder()
                    .params(WikiPrinterParams.builder()
                            .client(new WikiClient())
                            .articleCounter(new AtomicInteger(0))
                            .printingCount(config.getPrintingCount())
                            .build())
                    .model(PrintModel.builder()
                            .category(categories[i])
                            .categoryId("0" + i + 1)
                            .preventDirs(config.getCrawlingResultsPath())
                            .build())
                    .executorModel(ExecutorModel.builder()
                            .executor(executorsForPages.get(i))
                            .threadsCount(config.getCrawlingThreadsCount())
                            .build())
                    .build());
            todo.add(printer);
        }

        categoryExecutor.invokeAll(todo); // waits all tasks here
        categoryExecutor.shutdown();

        executorsForPages.forEach(ExecutorService::shutdown);

        endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " millis"); // Time taken: 72115 millis

        CsvWorker.printArticles(config.getResultCsvName());
    }

    private static List<ExecutorService> initExecutors(int length, int threadsCount) {
        List<ExecutorService> rez = new ArrayList<>(length);
        for (int i = 1; i <= length; i++) {
            rez.add(Executors.newFixedThreadPool(threadsCount));
        }
        return rez;
    }
}
