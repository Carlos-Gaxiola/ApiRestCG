package com.encora.framework.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.encora.framework.controller.*;
import com.encora.framework.json.JSONSerializer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("unchecked")
public class RootHandler<T> implements HttpHandler {


    private final T controller;

    public RootHandler(T controller) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException {
        this.controller = controller;
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        String route = null;
        if(he.getRequestURI().getPath().split("/").length == 3){
            route = he.getRequestURI().getPath().split("/")[2];
        }

        String method = he.getRequestMethod();

        switch (method) {
            case "GET" -> handleGet(he, method, route);
            case "POST" -> handlePost(he, method, route);
            case "PUT" -> handlePut(he, method, route);
            case "DELETE" -> handleDelete(he, method, route);
            default -> requestHandler(he, 400, "Invalid request");
        }


    }

    public String getRoute(HttpExchange he) throws IOException {
        return "/" + he.getRequestURI().getPath().split("/")[1];
    }

    private void handleDelete(HttpExchange he, String method, String route) {
        try {
            String code = he.getRequestURI().getPath().split("/")[2];
            Method m = obtainMethod(method, route);

            m.invoke(controller, code);

            requestHandler(he, 200, "success");

        } catch (Exception e) {
            requestHandler(he, 404, "not found");
        }
    }

    private void handlePut(HttpExchange he, String method, String route) {
        try {
            String code = he.getRequestURI().getPath().split("/")[2];
            String body = transformRequest(he.getRequestBody());
            Method m = obtainMethod(method, route);
            String methodString = m.toString().split(" ")[2];
            String classToFind = methodString.substring(0, methodString.indexOf(".update"));
            Class<?> clazz = Class.forName(classToFind);
            String cls = null;
            for (Method mt: clazz.getDeclaredMethods()) {
                if (mt.getName().contains("update")) {
                    cls = mt.toString().split(" ")[1];
                    break;
                }
            }

            Class<?> klass = Class.forName(cls);

            Object element = JSONSerializer.deserialize(klass, body);

            element = m.invoke(controller, code, element);

            String elementEdited = JSONSerializer.serilaize(element);

            requestHandler(he, 201, elementEdited);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void handlePost(HttpExchange he, String method, String route) {
        try {
            Method m = obtainMethod(method, route);

            String body = transformRequest(he.getRequestBody());

            Class<?> cls = Class.forName(m.toString().split(" ")[1]);

            Object element = JSONSerializer.deserialize(cls, body);

            element = m.invoke(controller, element);

            String elementAdded = JSONSerializer.serilaize(element);

            requestHandler(he, 201, elementAdded);



        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGet(HttpExchange he, String method, String route) {
        try {
            String[] code = he.getRequestURI().getPath().split("/");
            Method m = obtainMethod(method, route);

            if (code.length < 3) {
                List<String> response = new ArrayList<>();
                List<?> data = (List<?>) m.invoke(controller);

                data.forEach(element -> {
                    String serializedObject;
                    try {
                        serializedObject = JSONSerializer.serilaize(element);
                        response.add(serializedObject);
                    } catch (NoSuchMethodException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                });

                if(response.size() > 0) {

                    byte[] bs = response.toString().getBytes("UTF-8");
                    he.sendResponseHeaders(200, bs.length);
                    OutputStream os = he.getResponseBody();
                    os.write(response.toString().getBytes());
                    os.close();
                }
                else {
                    requestHandler(he, 200, "There are no elements stored");

                }


            } else if (code.length == 3) {
                String response = JSONSerializer.serilaize(m.invoke(controller, route));

                requestHandler(he, 200, response);

            }

            else {
                String message = "There are too many parameters in your request";

                requestHandler(he, 400, message);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Method obtainMethod(String method, String route) throws ClassNotFoundException {
        Method methodToInvoke = null;
        Method[] methods = controller.getClass().getDeclaredMethods();
        for (Method m: methods) {
            switch (method) {
                case "GET":
                    Get getAnnotation = m.getAnnotation(Get.class);
                    if(route == null){
                        if(getAnnotation != null && getAnnotation.value().equals("/")){
                            methodToInvoke = m;
                        }
                    }else{
                        if(getAnnotation != null && getAnnotation.value().equals("/{code}")){
                            methodToInvoke = m;
                        }
                    }
                    break;
                case "POST":
                    Post postAnnotation = m.getAnnotation(Post.class);
                    if(postAnnotation != null && postAnnotation.value().equals("/")){
                        methodToInvoke = m;
                    }
                    break;
                case "PUT":
                    Put putAnnotation = m.getAnnotation(Put.class);
                    if(putAnnotation != null && putAnnotation.value().equals("/{code}")){
                        methodToInvoke = m;
                    }
                    break;
                case "DELETE":
                    Delete deleteAnnotation = m.getAnnotation(Delete.class);
                    if(deleteAnnotation != null && deleteAnnotation.value().equals("/{code}")){
                        methodToInvoke = m;
                    }
                    break;
            }
        }
        return methodToInvoke;
    }

    private void requestHandler(HttpExchange he, int statusCode, String message){
        try {
            he.sendResponseHeaders(statusCode, message.length());
            OutputStream os = he.getResponseBody();
            os.write(message.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String transformRequest(InputStream body) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(body, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        int character;
        while ((character = bufferedReader.read()) != -1) {
            stringBuilder.append((char) character);
        }
        body.close();
        return stringBuilder.toString();
    }

}