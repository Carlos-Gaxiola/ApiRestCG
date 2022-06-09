package com.encora.api.controller;

import com.encora.api.model.Book;
import com.encora.api.service.BookService;
import com.encora.framework.controller.Controller;
import com.encora.framework.controller.Get;
import com.encora.framework.controller.RestController;

import java.util.List;

@RestController("/book")
public class BookController implements Controller<Book> {

    private BookService bookService;


    public BookController() {
        bookService = new BookService();
    }

    @Override
    public List<Book> getAll(){
        return bookService.getAll();
    }

    @Override
    public Book get(String code) {
        return bookService.getByCode(code);
    }

    @Override
    public Book create(Book book) {
        return bookService.add(book);
    }

    @Override
    public Book update(String code, Book book) {
        return bookService.update(code, book);
    }

    @Override
    public void delete(String code) {
        bookService.delete(code);
    }

}
