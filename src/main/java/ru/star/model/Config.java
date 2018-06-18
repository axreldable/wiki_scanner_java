package ru.star.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.apache.log4j.Logger;
import ru.star.utils.FileUtils;

import java.io.File;

@Getter
public class Config {
    private final static Logger logger = Logger.getLogger(Config.class);

    @SerializedName("start_categories")
    private String[] startCategories;
    @SerializedName("crawling_threads_count")
    private int crawlingThreadsCount;
    @SerializedName("query_delay")
    private int queryDelay;
    @SerializedName("crawling_results_path")
    private String crawlingResultsPath;
    @SerializedName("result_csv_name")
    private String resultCsvName;
    @SerializedName("print_count")
    private int printingCount;

    public boolean validateAndFix() {
        if (!(startCategories.length > 0)) {
            logger.info("At least one starting category is required");
            return false;
        }
        if (!(crawlingThreadsCount > 0)) {
            logger.info("At least one crawling thread is required");
            return false;
        }
        if (!(queryDelay >= 0)) {
            logger.info("queryDelay should not be negative");
            return false;
        }
        if (!(new File(crawlingResultsPath).exists())) {
            logger.info("crawlingResultsPath doesn't exist");
            FileUtils.createDir(crawlingResultsPath);
            logger.info(crawlingResultsPath + " created");
        }
        if (!(printingCount > 0)) {
            logger.info("At least one article for printing is required");
            return false;
        }
        return true;
    }
}
