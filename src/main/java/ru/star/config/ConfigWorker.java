package ru.star.config;

import lombok.Getter;
import org.apache.log4j.Logger;
import ru.star.config.exception.WrongConfigException;
import ru.star.config.model.ConfigModel;
import ru.star.parser.json.JsonParser;
import ru.star.utils.FileUtils;

import java.io.File;

public class ConfigWorker {
    private final static Logger logger = Logger.getLogger(ConfigWorker.class);

    @Getter private ConfigModel config;

    public ConfigWorker(String configPath) throws WrongConfigException {
        String configJson = FileUtils.readFromFile(configPath);
        this.config = JsonParser.parse(configJson, ConfigModel.class);
        validate();
    }

    private void validate() throws WrongConfigException {
        checkCondition(config.getStartCategories().length > 0, "At least one starting category is required");
        checkCondition(config.getCrawlingThreadsCount() > 0, "At least one crawling thread is required");
        checkCondition(config.getQueryDelay() >= 0, "queryDelay should not be negative");
        checkCondition(config.getPrintingCount() > 0, "At least one article for printing is required");

        checkCondition(config.getCrawlingResultsPath().length() != 0, "crawlingResultsPath shouldn't be empty");
        checkCondition(config.getResultCsvName().length() != 0, "getResultCsvName shouldn't be empty");
    }

    public void createCrawlingResultPath() {
        String crawlingResultsPath = config.getCrawlingResultsPath();
        if (!(new File(crawlingResultsPath).exists())) {
            logger.info("crawlingResultsPath doesn't exist");
            FileUtils.createDir(crawlingResultsPath);
            logger.info(crawlingResultsPath + " created");
        }
    }

    public void createResultFilePath() {
        String resultCsvName = config.getResultCsvName();
        File pathToResultFile = new File(resultCsvName).getParentFile();
        if (!(pathToResultFile.exists())) {
            logger.info("path to " + resultCsvName + " doesn't exist");
            FileUtils.createDir(pathToResultFile.getName());
            logger.info(pathToResultFile.getName() + " created");
        }
    }

    private void checkCondition(boolean condition, String message) throws WrongConfigException {
        if (!condition) {
            throw new WrongConfigException(message);
        }
    }
}
