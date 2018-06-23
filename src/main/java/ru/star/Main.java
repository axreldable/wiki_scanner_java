package ru.star;

import org.apache.log4j.Logger;
import ru.star.config.ConfigWorker;
import ru.star.config.exception.WrongConfigException;
import ru.star.csv.CsvConsumer;
import ru.star.http.WikiClient;
import ru.star.model.printer.ExecutorModel;
import ru.star.model.printer.PrintModel;
import ru.star.model.printer.WikiPrinterModel;
import ru.star.model.printer.WikiPrinterParams;
import ru.star.printer.WikiPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException, WrongConfigException {
        long startTime = System.currentTimeMillis();

        ConfigWorker configWorker = initConfig();

        String[] categories = configWorker.getConfig().getStartCategories();
        Arrays.sort(categories);

        ExecutorService categoryExecutor = Executors.newFixedThreadPool(categories.length);
        List<Callable<Object>> todo = new ArrayList<>(categories.length);

        List<ExecutorService> executorsForPages = initExecutors(categories.length, configWorker.getConfig().getCrawlingThreadsCount());

        for (int i = 1; i <= categories.length; i++) {
            WikiPrinter printer = new WikiPrinter(WikiPrinterModel.builder()
                    .params(WikiPrinterParams.builder()
                            .client(new WikiClient())
                            .articleCounter(new AtomicInteger(0))
                            .printingCount(configWorker.getConfig().getPrintingCount())
                            .build())
                    .model(PrintModel.builder()
                            .category(categories[i - 1])
                            .categoryId("0" + i)
                            .preventDirs(configWorker.getConfig().getCrawlingResultsPath())
                            .build())
                    .executorModel(ExecutorModel.builder()
                            .executor(executorsForPages.get(i - 1))
                            .threadsCount(configWorker.getConfig().getCrawlingThreadsCount())
                            .build())
                    .build());
            todo.add(printer);
        }

        Thread thread = new Thread(new CsvConsumer(configWorker.getConfig().getResultCsvName()));
        thread.start();

        categoryExecutor.invokeAll(todo); // waits all tasks here
        categoryExecutor.shutdown();

        thread.interrupt();

        executorsForPages.forEach(ExecutorService::shutdown);

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " millis"); // Time taken: 72115 millis
    }

    private static ConfigWorker initConfig() throws WrongConfigException {
        ConfigWorker configWorker = new ConfigWorker("app_config.json");
        configWorker.createCrawlingResultPath();
        configWorker.createResultFilePath();
        return configWorker;
    }

    private static List<ExecutorService> initExecutors(int length, int threadsCount) {
        List<ExecutorService> rez = new ArrayList<>(length);
        for (int i = 1; i <= length; i++) {
            rez.add(Executors.newFixedThreadPool(threadsCount));
        }
        return rez;
    }
}
