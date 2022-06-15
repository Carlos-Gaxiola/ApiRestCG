package com.encora.framework.controller;

import java.util.List;

public interface Controller <T>{
    List<T> getAll();

    T get(String id);

    T create(T element);

    T update (String id, T element);

    void delete(String id);

}