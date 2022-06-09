package com.encora.framework.server;

import com.encora.framework.controller.Controller;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;


public class ApplicationServer {

    private static final int PORT = 3001;

    public static void run(String[] args, Controller controller, Class<?> clazz) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/books", new RootHandler(controller, clazz));
        server.setExecutor(null);
        server.start();

    }
}
