import java.io.*;
import java.net.ServerSocket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private boolean enabled;
    final List<String> validPaths;
    final ExecutorService threadPool = Executors.newFixedThreadPool(64);
    final Map<String, Map<String , Handler>> handlers;

    public Server() {
        validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
        handlers = new ConcurrentHashMap<>();
    }

    public void start() {

        this.enabled = true;

        try (final var serverSocket = new ServerSocket(9999)) {

            System.out.println("Server successfully started");

            while (this.enabled) {
                try {
                    final var socket = serverSocket.accept();
                    Runnable processConnection = new ProcessConnections(socket, validPaths, this);
                    threadPool.submit(processConnection);
                } catch (IOException e) {
                    e.printStackTrace();
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

    public boolean addHandler(String method, String path, Handler handler) {

        Map<String, Handler> pathMap = handlers.get(method);

        if (pathMap == null) {
            pathMap = new ConcurrentHashMap<>();
            pathMap.put(path, handler);
            handlers.put(method, pathMap);
            return true;
        }

        Handler currentHandler = pathMap.get(path);

        if (currentHandler == null) {
            pathMap.put(path, handler);
            return true;
        }

        return false;

    }

    public Handler getHandler(String method, String path) {

        Map<String, Handler> pathMap = handlers.get(method);
        if (pathMap == null) return null;
        return pathMap.get(path);

    }


}


