package com.encora.api.controller;

import com.encora.api.model.Book;
import com.encora.api.service.BookService;
import com.encora.framework.controller.*;

import java.util.List;

@RestController(url = "/books")
public class BookController implements Controller<Book> {

    private BookService bookService;

    public BookController() {
        bookService = new BookService();
    }

    @Override
    @Get
    public List<Book> getAll(){
        return bookService.getAll();
    }

    @Override
    @Get("/{code}")
    public Book get(String code) {
        return bookService.getByCode(code);
    }

    @Override
    @Post
    public Book create(Book book) {
        return bookService.add(book);
    }

    @Override
    @Put("/{code}")
    public Book update(String code, Book book) {
        return bookService.update(code, book);
    }

    @Override
    @Delete("/{code}")
    public void delete(String code) {
        bookService.delete(code);
    }

}
