package com.encora.api;

import java.io.IOException;

import com.encora.api.controller.BookController;
import com.encora.api.model.Book;
import com.encora.framework.server.ApplicationServer;

public class App {

    public static void main(String[] args) throws IOException {
        ApplicationServer.run(args, new BookController(), Book.class);

    }

}
