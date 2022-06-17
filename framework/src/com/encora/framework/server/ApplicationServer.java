package com.encora.framework.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ApplicationServer {

    private static final int PORT = 3001;
    public static String path;

    public static void run(String[] args, Class<?> main) throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        GetRestControllers restControllers = new GetRestControllers();
        Map controllers = restControllers.getRestControllers(main);



        Iterator<Map.Entry<String, Class>> itr = controllers.entrySet().iterator();

        while(itr.hasNext())
        {
            Map.Entry<String, Class> entry = itr.next();
            Object object = entry.getValue().getDeclaredConstructor().newInstance();
            server.createContext(entry.getKey(), new RootHandler(object));
        }

        server.setExecutor(null);
        server.start();

    }
}
