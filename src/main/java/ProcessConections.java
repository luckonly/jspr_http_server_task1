import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class ProcessConections implements Runnable {

    Socket socket;
    List<String> validPaths;

    public ProcessConections(Socket socket, List<String> validPaths) {
        this.socket = socket;
        this.validPaths = validPaths;
    }

    public void ProcessConnections() {

        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())
        )   {

            final var requestParts = in.readLine();
            final var parts = requestParts.split(" ");

            if (parts.length != 3) {
                return;
            }

            final var path = parts[1];

            if (!validPaths.contains(path)) {
                out.write(("HTTP/1.1 404 Not Found\r\n" +
                        "Content-length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n").getBytes(StandardCharsets.UTF_8));
                out.flush();
                return;
            }

            final var filePath = Path.of(".", "public", path);
            final var mimeType = Files.probeContentType(filePath);

            if (path.equals("/classic.html")) {

                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}", LocalDateTime.now().toString()
                ).getBytes(StandardCharsets.UTF_8);

                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-type: " + mimeType + "\r\n" +
                                "Content-length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes(StandardCharsets.UTF_8));
                out.write(content);
                out.flush();

                return;
            }

            final var length = Files.size(filePath);
            out.write((
                    "HTTP 1.1 200 OK\r\n" +
                            "Content-type: " + mimeType + "\r\n" +
                            "Content-length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes(StandardCharsets.UTF_8));
            Files.copy(filePath, out);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        ProcessConnections();
    }
}
