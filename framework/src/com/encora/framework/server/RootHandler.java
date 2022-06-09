package com.encora.framework.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.encora.framework.controller.Controller;
import com.encora.framework.json.JSONSerializer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("unchecked")
public class RootHandler implements HttpHandler {

    private Controller controller;
    private Class<?> clazz;

    public RootHandler(Controller controller, Class<?> clazz) {
        this.controller = controller;
        this.clazz = clazz;
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        String method = he.getRequestMethod();

        switch (method) {
            case "GET" -> handleGet(he);
            case "POST" -> handlePost(he);
            case "PUT" -> handlePut(he);
            case "DELETE" -> handleDelete(he);
            default -> requestHandler(he, 400, "Invalid request");
        }

    }

    private void handleDelete(HttpExchange he) {
        try {
            var code = he.getRequestURI().getPath().split("/")[2];

            controller.delete(code);

            requestHandler(he, 200, "success");

        } catch (Exception e) {
            requestHandler(he, 404, "not found");
        }
    }

    private void handlePut(HttpExchange he) {
        try {
            var code = he.getRequestURI().getPath().split("/")[2];
            var body = transformRequest(he.getRequestBody());


            var elementEdited = JSONSerializer.serilaize(controller.update(code, JSONSerializer.deserialize(clazz, body)));

            requestHandler(he, 201, elementEdited);

        } catch (IOException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void handlePost(HttpExchange he) {
        try {
            String body = transformRequest(he.getRequestBody());

            Object element = JSONSerializer.deserialize(clazz, body);

            element = controller.create(element);

            String elementAdded = JSONSerializer.serilaize(element);

            requestHandler(he, 201, elementAdded);

        } catch (IOException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void handleGet(HttpExchange he) {
        try {
            var code = he.getRequestURI().getPath().split("/");

            if (code.length < 3) {
                List<String> response = new ArrayList<String>();
                controller.getAll().forEach(element -> {
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
		        		/*Object collect = controller.getAll().stream()
		        		.map(book -> {
							try {
								return JSONSerializer.serilaize(book);
							} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
								e.printStackTrace();
								return "";
							}
						})
		        		.collect(Collectors.toList());
		        		*/


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

            } else if (code.length == 3) {
                var response = JSONSerializer.serilaize(controller.get(code[2]));

                requestHandler(he, 200, response);

            } else {
                var message = "There are too many parameters in your request";

                requestHandler(he, 400, message);

            }

        } catch (IOException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
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
