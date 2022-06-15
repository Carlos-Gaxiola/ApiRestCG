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
        this.controller = (T) Class.forName(String.valueOf(controller).substring(String.valueOf(controller).indexOf(" ") + 1));;
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        String route = "/" + he.getRequestURI().getPath().split("/")[1];
        String method = he.getRequestMethod();

        switch (method) {
            case "GET" -> handleGet(he, method);
            case "POST" -> handlePost(he, method);
            case "PUT" -> handlePut(he, method);
            case "DELETE" -> handleDelete(he, method);
            default -> requestHandler(he, 400, "Invalid request");
        }

    }

    public String getRoute(HttpExchange he) throws IOException {
        return "/" + he.getRequestURI().getPath().split("/")[1];
    }

    private void handleDelete(HttpExchange he, String method) {
        try {
            var code = he.getRequestURI().getPath().split("/")[2];
            Method m = obtainMethod(method);

            m.invoke(controller, code);

            requestHandler(he, 200, "success");

        } catch (Exception e) {
            requestHandler(he, 404, "not found");
        }
    }

    private void handlePut(HttpExchange he, String method) {
        try {
            var code = he.getRequestURI().getPath().split("/")[2];
            var body = transformRequest(he.getRequestBody());
            Method m = obtainMethod(method);

            Class<?> cls = Class.forName(m.toString().split(" ")[1]);

            Object elementToEdit = JSONSerializer.deserialize(cls, body);

            elementToEdit = m.invoke(controller, code, elementToEdit);

            String elementEdited = JSONSerializer.serilaize(elementToEdit);


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

    private void handlePost(HttpExchange he, String method) {
        try {
            Method m = obtainMethod(method);

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

    private void handleGet(HttpExchange he, String method) {
        try {
            var code = he.getRequestURI().getPath().split("/");
            Method m = obtainMethod(method);

            if (code.length < 3) {
                List<String> response = new ArrayList<>();
                System.out.println(controller);
                List<?> data = (List<?>) m.invoke(controller, null);

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
                    requestHandler(he, 200, "There are no books stored");

                }

            }  else {
                var message = "There are too many parameters in your request";

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
        }
    }

    public Method obtainMethod(String method) throws ClassNotFoundException {
        Method methodToInvoke = null;
        Class<?> clazz = Class.forName(String.valueOf(controller).substring(String.valueOf(controller).indexOf(" ") + 1));
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m: methods) {
            switch (method) {
                case "GET":
                    Get getAnnotation = m.getAnnotation(Get.class);
                    if(getAnnotation != null && getAnnotation.value().equals("/")){
                        methodToInvoke = m;
                    }
                case "POST":
                    Post postAnnotation = m.getAnnotation(Post.class);
                    if(postAnnotation != null && postAnnotation.value().equals("/")){
                        methodToInvoke = m;
                    }
                case "PUT":
                    Put putAnnotation = m.getAnnotation(Put.class);
                    if(putAnnotation != null && putAnnotation.value().equals("/{code}")){
                        methodToInvoke = m;
                    }
                case "DELETE":
                    Delete deleteAnnotation = m.getAnnotation(Delete.class);
                    if(deleteAnnotation != null && deleteAnnotation.value().equals("/{code}")){
                        methodToInvoke = m;
                    }
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
        var inputStreamReader = new InputStreamReader(body, StandardCharsets.UTF_8);
        var bufferedReader = new BufferedReader(inputStreamReader);
        var stringBuilder = new StringBuilder();
        int character;
        while ((character = bufferedReader.read()) != -1) {
            stringBuilder.append((char) character);
        }
        body.close();
        return stringBuilder.toString();
    }

}