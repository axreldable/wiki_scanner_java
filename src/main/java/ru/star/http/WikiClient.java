package ru.star.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Class for http communication with Wiki.
 */
public class WikiClient {
    private final static Logger logger = Logger.getLogger(WikiClient.class);

    private final static String WIKI_API_URL = "http://ru.wikipedia.org/w/api.php";

    private HttpClient client;

    public WikiClient() {
        client = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
    }

    /**
     * GET methods for getting categories from Wiki.
     *
     * @param name - name of the category
     * @return Wiki response in String
     * @throws URISyntaxException when exception in Uri building occurs
     * @throws IOException        when exception in response from Wiki reading occurs
     */
    public String getCategory(String name) throws URISyntaxException, IOException {
        try {
            return executeRequest((new URIBuilder(WIKI_API_URL)
                    .setParameter("action", "query")
                    .setParameter("format", "json")
                    .setParameter("list", "categorymembers")
                    .setParameter("cmprop", "title|type|ids")
                    .setParameter("cmlimit", "500")
                    .setParameter("cmtitle", "Category:" + name)
                    .build()));
        } catch (URISyntaxException e) {
            logger.error("Exception during uri building", e);
            throw e;
        }
    }

    /**
     * GET method for getting the article from Wiki.
     *
     * @param id - article's id
     * @return Wiki response in String
     * @throws URISyntaxException when exception in Uri building occurs
     * @throws IOException        when exception in response from Wiki reading occurs
     */
    public String getArticle(String id) throws URISyntaxException, IOException {
        try {
            return executeRequest((new URIBuilder(WIKI_API_URL).setParameters()
                    .setParameter("action", "query")
                    .setParameter("format", "json")
                    .setParameter("prop", "extracts")
                    .setParameter("explaintext", null)
                    .setParameter("exsectionformat", "plain")
                    .setParameter("pageids", id)
                    .build()));
        } catch (URISyntaxException e) {
            logger.info("Exception during uri building", e);
            throw e;
        }
    }

    /**
     * Method for execute request from http client. If smth goes wrong return null and log bad response.
     *
     * @param uri - uri for GET method
     * @return String response from Server
     * @throws IOException when exception in response from Wiki reading occurs
     */
    private String executeRequest(URI uri) throws IOException {
        try {
            HttpResponse response = client.execute(new HttpGet(uri));
            if (isOk(response.getStatusLine().getStatusCode())) {
                return EntityUtils.toString(response.getEntity());
            } else {
                logger.error("Bad response from wiki - " + response.getStatusLine());
                return null;
            }
        } catch (IOException e) {
            logger.error("Exception during response reading", e);
            throw e;
        }
    }

    /**
     * Checks if the status ok or not.
     *
     * @param status status for checking
     * @return check result
     */
    private boolean isOk(int status) {
        return status >= 200 && status < 300;
    }
}
