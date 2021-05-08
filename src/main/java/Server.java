import java.io.*;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private boolean enabled;
    final List<String> validPaths;
    final ExecutorService threadPool = Executors.newFixedThreadPool(64);

    public Server() {
        validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    }

    public void Start() {

        this.enabled = true;

        try (final var serverSocket = new ServerSocket(9999)) {

            System.out.println("Server successfully started");

            while (this.enabled) {
                try (final var socket = serverSocket.accept()) {
                    Runnable processConnection = new ProcessConections(socket, validPaths);
                    threadPool.submit(processConnection);
                }
            }

            System.out.println("Server successfully stopped");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void StopServer() {
        if (this.enabled) {
            this.enabled = false;
        }
    }


}


