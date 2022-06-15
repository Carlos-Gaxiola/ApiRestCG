package com.encora.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.encora.api.model.Game;

public class GameService {

    private List<Game> data;

    public GameService() {
        data =  new ArrayList();
        data.add(new Game("101", "Red dead redemption", "Sandbox", 2018));
        data.add(new Game("102", "The last of us 2", "Survival horror", 2020));
    }

    public Game getByCode(String code){
        var getIndex = IntStream.range(0, data.size())
                .filter(i -> data.get(i).getCode().equals(code))
                .findFirst().orElse(-1);

        return data.get(getIndex);
    }

    public List<Game> getAll() {
        return data;
    }

    public Game add(Game book) {

        if(!codeExists(book.getCode())) {
            data.add(book);
        }

        return data.get(data.size() - 1);
    }

    public Game update(String code, Game book) {
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
