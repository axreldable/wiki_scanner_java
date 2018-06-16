package ru.star.http;

import org.apache.http.HttpEntity;
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

public class WikiClient {
    final static Logger logger = Logger.getLogger(WikiClient.class);

    private String WIKI_API_URL = "http://ru.wikipedia.org/w/api.php";

    private HttpClient client;

    public WikiClient() {
        client = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();
    }

    public String getCategory(String name) {
        try {
            return executeRequest((new URIBuilder(WIKI_API_URL)
                    .setParameter("action", "query")
                    .setParameter("format", "xml")
                    .setParameter("list", "categorymembers")
                    .setParameter("cmprop", "title|type|ids")
                    .setParameter("cmlimit", "500")
                    .setParameter("cmtitle", "Category:" + name)
                    .build()));
        } catch (URISyntaxException e) {
            logger.info("Exception during uri building", e);
        }
        return null;
    }

    public String getArticle(String id) {
        try {
            return executeRequest((new URIBuilder(WIKI_API_URL).setParameters()
                    .setParameter("action", "query")
                    .setParameter("format", "xml")
                    .setParameter("prop", "extracts")
                    .setParameter("explaintext", null)
                    .setParameter("exsectionformat", "plain")
                    .setParameter("pageids", id)
                    .build()));
        } catch (URISyntaxException e) {
            logger.info("Exception during uri building", e);
        }
        return null;
    }

    private String executeRequest(URI uri) {
        try {
            HttpResponse response = client.execute(new HttpGet(uri));
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                logger.info("Bad response from wiki");
            }
        } catch (IOException e) {
            logger.info("Exception during execute GET request", e);
        }
        return null;
    }
}
