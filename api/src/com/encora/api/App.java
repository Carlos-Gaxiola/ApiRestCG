package com.encora.api;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.encora.framework.server.ApplicationServer;

public class App {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        ApplicationServer.run(args, App.class);

    }

}
