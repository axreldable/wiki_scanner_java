package ru.star;

import ru.star.config.ConfigWorker;
import ru.star.config.exception.WrongConfigException;
import ru.star.csv.CsvConsumer;
import ru.star.http.WikiClient;
import ru.star.parser.json.JsonParser;
import ru.star.printer.WikiPrinter;
import ru.star.printer.model.DirNameModel;
import ru.star.printer.model.ExecutorModel;
import ru.star.printer.model.WikiPrinterModel;
import ru.star.printer.model.WikiPrinterParamsModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException, WrongConfigException {
        long startTime = System.currentTimeMillis();

        JsonParser parser = new JsonParser();
        ConfigWorker configWorker = initConfig(parser);

        String[] categories = configWorker.getConfig().getStartCategories();
        Arrays.sort(categories);

        ExecutorService categoryExecutor = Executors.newFixedThreadPool(categories.length);
        List<Callable<Object>> todo = new ArrayList<>(categories.length);

        List<ExecutorService> executorsForPages = initExecutors(categories.length, configWorker.getConfig().getCrawlingThreadsCount());

        for (int i = 1; i <= categories.length; i++) {
            WikiPrinter printer = new WikiPrinter(WikiPrinterModel.builder()
                    .params(WikiPrinterParamsModel.builder()
                            .client(new WikiClient())
                            .articleCounter(new AtomicInteger(0))
                            .printingCount(configWorker.getConfig().getPrintingCount())
                            .build())
                    .dirName(DirNameModel.builder()
                            .category(categories[i - 1])
                            .categoryId("0" + i)
                            .preventDirs(configWorker.getConfig().getCrawlingResultsPath())
                            .build())
                    .executor(ExecutorModel.builder()
                            .executor(executorsForPages.get(i - 1))
                            .threadsCount(configWorker.getConfig().getCrawlingThreadsCount())
                            .build())
                    .parser(parser)
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

    private static ConfigWorker initConfig(JsonParser parser) throws WrongConfigException {
        ConfigWorker configWorker = new ConfigWorker("app_config.json", parser);
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
