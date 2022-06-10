package com.encora.framework.server;

import com.encora.framework.controller.Controller;
import com.encora.framework.controller.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class ControllerReader {
    public static void controllerReader(Controller controller, Class<?> main) throws IOException, ClassNotFoundException {
        List<String> classes = new ArrayList<>();
        Package p = controller.getClass().getPackage();
        String packagesSlashes = p.getName().replace(".", "/");
        Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagesSlashes);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String classesReading = (new Scanner((InputStream) url.getContent()).useDelimiter("\\A").next());
            String[] classesReadingSplit = classesReading.split("\n");

            for (String clsReading: classesReadingSplit) {
                classes.add(clsReading.substring(0, clsReading.indexOf(".")));
            }
        }

        for (String cls: classes) {
            String completeClassPath = (p.getName() + "." + cls);
            Class <?> clazz = Class.forName(completeClassPath);
            RestController fieldAnnotation = clazz.getAnnotation(RestController.class);
            if(fieldAnnotation != null){
                System.out.println(fieldAnnotation.url());
            }
        }
    }

}

