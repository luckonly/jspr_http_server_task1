import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class Request {

    final char QUERY_DELIMITER = '?';
    private String method;
    private String path;
    private String version;
    private String body;
    private Map<String, String> headers;
    private List<NameValuePair> queryParams;

    public Request() {
    }

    public Request(String method, String path, String version, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public Request(String[] requestLine) {
        this.method = requestLine[0];
        this.path = requestLine[1];
        this.version = requestLine[2];
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public String getBody() {
        return body;
    }

    public boolean addHeader(String header) {
        String[] headerParts = header.split(":");
        if (headerParts.length == 2) {
            this.headers.put(headerParts[0], headerParts[1].replace(" ",""));
            return true;
        } else {
            return false;
        }
    }

    public Optional<String> extractHeader(String header) {
        return headers.entrySet()
                .stream()
                .filter(o -> o.getKey().equals(header))
                .map(o -> o.getValue())
                .findFirst();
    }

    public void setQueryParams() {
        int delimiter = path.indexOf(QUERY_DELIMITER);
        if (delimiter == -1) return;
        queryParams = URLEncodedUtils.parse(path.substring(delimiter + 1), Charset.forName("UTF-8"));
    }

    public List<NameValuePair> getBodyParams() {
        return URLEncodedUtils.parse(body, Charset.forName("UTF-8"));
    }

    public String getPathWithoutQueryParams() {
        int queryDelimiter = path.indexOf(QUERY_DELIMITER);
        if (queryDelimiter != -1) {
            path = path.substring(0, queryDelimiter);
            System.out.println(path);
        }
        return path;
    }

    public Optional<String> getQueryParamValue(String queryParam) {
        return queryParams.stream()
                .filter(o -> o.getName().equals(queryParam))
                .map(o -> o.getValue())
                .findFirst();
    }

    public Optional<String> getHeaderValue(String header) {
        return headers.entrySet()
                .stream()
                .filter(o -> o.getKey().equals(header))
                .map(o -> o.getValue())
                .findFirst();
    }

}
