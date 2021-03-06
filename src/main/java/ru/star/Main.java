package ru.star;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import ru.star.config.ConfigWorker;
import ru.star.config.exception.WrongConfigException;
import ru.star.config.model.ConfigModel;
import ru.star.csv.CsvConsumer;
import ru.star.http.WikiClient;
import ru.star.parser.json.WikiParser;
import ru.star.printer.WikiPrinter;
import ru.star.printer.model.DirNameModel;
import ru.star.printer.model.ExecutorModel;
import ru.star.printer.model.WikiPrinterModel;
import ru.star.printer.model.WikiPrinterParamsModel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.star.Constants.WIKI_API_URL;

/**
 * Main class of the app.
 * Parses config.
 * Start threads for categories printing.
 * And starts the thread for printing result file.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException, WrongConfigException, IOException {
        long startTime = System.currentTimeMillis();

        WikiParser parser = new WikiParser(); // one instance for all threads
        ConfigModel config = initAndValidateConfig(parser);

        String[] categories = config.getStartCategories();
        Arrays.sort(categories);

        ExecutorService categoryExecutor = Executors.newFixedThreadPool(categories.length);
        List<Callable<Object>> todo = new ArrayList<>(categories.length);

        List<ExecutorService> executorsForPages = initExecutors(categories.length, config.getCrawlingThreadsCount());

        for (int i = 1; i <= categories.length; i++) {
            WikiPrinter printer = new WikiPrinter(WikiPrinterModel.builder()
                    .params(WikiPrinterParamsModel.builder()
                            .client(new WikiClient(HttpClients.custom()
                                    .setDefaultRequestConfig(RequestConfig.custom()
                                            .setCookieSpec(CookieSpecs.STANDARD).build())
                                    .build(), WIKI_API_URL))
                            .articleCounter(new AtomicInteger(0))
                            .printingCount(config.getPrintingCount())
                            .build())
                    .dirName(DirNameModel.builder()
                            .category(categories[i - 1])
                            .categoryId("0" + i)
                            .preventDirs(config.getCrawlingResultsPath())
                            .build())
                    .executor(ExecutorModel.builder()
                            .executor(executorsForPages.get(i - 1))
                            .threadsCount(config.getCrawlingThreadsCount())
                            .build())
                    .parser(parser)
                    .build());
            todo.add(printer);
        }

        Thread thread = new Thread(new CsvConsumer(new BufferedWriter(new FileWriter(config.getResultCsvName()))));
        thread.start();

        categoryExecutor.invokeAll(todo); // waits all tasks here
        categoryExecutor.shutdown();

        thread.interrupt();

        executorsForPages.forEach(ExecutorService::shutdown);

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " millis"); // Time taken: 72115 millis
    }

    /**
     * Method for config initializing.
     *
     * @param parser - app Parser
     * @return instance of config model
     * @throws WrongConfigException when the config is wrong
     */
    private static ConfigModel initAndValidateConfig(WikiParser parser) throws WrongConfigException {
        ConfigWorker configWorker = new ConfigWorker("app_config.json", parser);
        configWorker.createCrawlingResultPath();
        configWorker.createResultFilePath();
        return configWorker.getConfig();
    }

    /**
     * Method for init several {@link ExecutorService}s.
     *
     * @param length       - length of the list of the {@link ExecutorService}s
     * @param threadsCount - amount of threads in each {@link ExecutorService}
     * @return list of the {@link ExecutorService}s
     */
    private static List<ExecutorService> initExecutors(int length, int threadsCount) {
        List<ExecutorService> rez = new ArrayList<>(length);
        for (int i = 1; i <= length; i++) {
            rez.add(Executors.newFixedThreadPool(threadsCount));
        }
        return rez;
    }
}
