import java.util.List;

public class Request {

    String method;
    String path;
    String version;
    List<String> headers;
    String body;

    public Request() {
    }

    public Request(String method, String path, String version, List<String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public boolean setRequest(String requestLine) {

        String[] parts = requestLine.split(" ");

        if (parts.length == 3) {
            this.method     = parts[0];
            this.path       = parts[1];
            this.version    = parts[2];
            return true;
        }
        return false;
    }

    public void addHeader(String header) {
        this.headers.add(header);
    }

    public void addHeaders(List<String> headers) {
        for (String header : headers) {
            if (!this.headers.contains(header)) {
                addHeader(header);
            }
        }
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
}
