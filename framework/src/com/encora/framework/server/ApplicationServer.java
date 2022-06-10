package com.encora.framework.server;

import com.encora.framework.controller.Controller;
import com.encora.framework.controller.RestController;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;


public class ApplicationServer {

    private static final int PORT = 3001;

    public static void run(String[] args, Controller controller, Class<?> clazz, Class<?> main) throws IOException, ClassNotFoundException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        ControllerReader cr = new ControllerReader();
        cr.controllerReader(controller, main);

        server.createContext("/books", new RootHandler(controller, clazz));
        server.setExecutor(null);
        server.start();

    }

}
