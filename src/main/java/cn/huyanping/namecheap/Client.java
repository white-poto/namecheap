package cn.huyanping.namecheap;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dom4j.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huyanping on 2016/12/27.
 */
public class Client {

    private String url;
    private String user;
    private String key;
    private String name;
    private String clientIp;

    private CloseableHttpClient httpClient;

    public Client(String url, String user, String key, String name, String clientIp) {
        this.url = url;
        this.user = user;
        this.key = key;
        this.name = name;
        this.clientIp = clientIp;

        this.httpClient = HttpClients.createDefault();
    }

    public String getList(int page, short pageSize, String sortBy, String listType, String searchTerm) {
        Map<String, String> params = new HashMap<>();
        params.put("Page", String.valueOf(page));
        params.put("PageSize", String.valueOf(pageSize));
        params.put("SortBy", sortBy);
        params.put("ListType", listType);
        params.put("SearchTerm", searchTerm);


    }

    private Document parseResponse(CloseableHttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("status code is not 200. response:" + formatResponseErrorParams(response));
        }
    }

    private CloseableHttpResponse get(String command, Map<String, String> params) throws IOException {
        String uri = createURI(command, params);
        HttpGet get = new HttpGet(uri);

        return httpClient.execute(get);
    }

    private String createURI(String command, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url);
        builder.append("?command=").append(command).append("&");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        return builder.toString();
    }

    private String formatResponseErrorParams(CloseableHttpResponse response) throws IOException {
        StringBuilder builder = new StringBuilder();
        InputStream stream = response.getEntity().getContent();
        int available = stream.available();
        byte[] body = new byte[stream.available()];
        int length = stream.read(body);
        if (length != available) {
            throw new IOException("read http body error");
        }

        builder.append("status:").append(response)
                .append("body:").append(new String(body));

        return builder.toString();
    }

}
