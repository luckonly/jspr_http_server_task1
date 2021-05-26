import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

public class ProcessConnections implements Runnable {

    private final Socket socket;
    private final List<String> validPaths;
    private final Server server;

    public ProcessConnections(Socket socket, List<String> validPaths, Server server) {
        this.socket = socket;
        this.validPaths = validPaths;
        this.server = server;
    }

    @Override
    public void run() {

        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())) {

            var requestLine = in.readLine();
            final var request = new Request();

            if (!request.setRequest(requestLine)) {
                return;
            }

            while ((requestLine = in.readLine()) != "\r\n") {
                request.addHeader(requestLine);
            }

            var sb = new StringBuilder();
            while ((requestLine = in.readLine()) != null) {
                sb.append(requestLine);
            }
            request.setBody(sb.toString());

            Handler handler = server.getHandler(request.getMethod(), request.path);

            if (handler != null) {
                handler.handle(request, out);
            }

            out.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
