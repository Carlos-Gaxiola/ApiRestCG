package com.encora.framework.server;

import com.encora.framework.controller.Controller;
import com.encora.framework.controller.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class GetRestControllers {
    public static Map getRestControllers(Class<?> main) throws IOException, ClassNotFoundException {
        Map<String, Class<?>> controllers = new HashMap<String, Class<?>>();
        List<String> classes = new ArrayList<>();
        Package p = main.getPackage();
        String packagesSlashes = p.getName().replace(".", "/");
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagesSlashes + "/controller");
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String classesReading = (new Scanner((InputStream) url.getContent()).useDelimiter("\\A").next());
            String[] classesReadingSplit = classesReading.split("\n");

            for (String clsReading: classesReadingSplit) {
                classes.add(clsReading.substring(0, clsReading.indexOf(".")));
            }
        }

        for (String cls: classes) {
            String completeClassPath = (p.getName() + ".controller." + cls);
            Class <?> clazz = Class.forName(completeClassPath);
            RestController fieldAnnotation = clazz.getAnnotation(RestController.class);

            if(fieldAnnotation != null){
                controllers.put(fieldAnnotation.url(), clazz);
            }
        }

        return controllers;
    }

}

