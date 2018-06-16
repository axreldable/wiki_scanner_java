package ru.star.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import ru.star.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WikiClient {
    private String WIKI_API_URL = "http://ru.wikipedia.org/w/api.php";

    private HttpClient client;

    public WikiClient() {
        client = HttpClients.createDefault();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return null;
    }

    private String executeRequest(URI uri) {
        try {
            HttpResponse response = client.execute(new HttpGet(uri));
            return StringUtils.inputStreamToString(response.getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
