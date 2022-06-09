package com.encora.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.encora.api.model.Book;

public class BookService {

    private List<Book> data;

    public BookService() {
        data =  new ArrayList();
        data.add(new Book("101", "The gunslinger", "Stephen King", 1982, 270));
        data.add(new Book("102", "11/22/63", "Stephen King", 2011, 800));
        data.add(new Book("103", "The lord of the rings", "JRR Tolkien", 1970, 900));
    }

    public Book getByCode(String code){
        var getIndex = IntStream.range(0, data.size())
                .filter(i -> data.get(i).getCode().equals(code))
                .findFirst().orElse(-1);

        return data.get(getIndex);
    }

    public List<Book> getAll() {
        return data;
    }

    public Book add(Book book) {

        if(!codeExists(book.getCode())) {
            data.add(book);
        }

        return data.get(data.size() - 1);
    }

    public Book update(String code, Book book) {
        var getIndex = IntStream.range(0, data.size())
                .filter(i -> data.get(i).getCode().equals(code))
                .findFirst().orElse(-1);

        if(codeExists(code)) {
            book.setCode(code);
            data.set(getIndex, book);
        }

        return data.get(getIndex);
    }

    public void delete(String code) {
        var getIndex = IntStream.range(0, data.size())
                .filter(i -> data.get(i).getCode().equals(code))
                .findFirst().orElse(-1);

        data.remove(getIndex);
    }

    public boolean codeExists(String code){
        var getIndex = IntStream.range(0, data.size())
                .filter(i -> data.get(i).getCode().equals(code))
                .findFirst().orElse(-1);

        return (getIndex != -1);
    }

}
