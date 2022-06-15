package com.encora.api.controller;

import com.encora.api.model.Game;
import com.encora.api.service.GameService;
import com.encora.framework.controller.*;

import java.util.List;

@RestController(url = "/games")
public class GameController implements Controller<Game> {

    private GameService gameService;

    public GameController() {
       gameService  = new GameService();
    }

    @Override
    @Get
    public List<Game> getAll(){
        return gameService.getAll();
    }

    @Override
    @Get("/{code}")
    public Game get(String code) {
        return gameService.getByCode(code);
    }

    @Override
    @Post
    public Game create(Game game) {
        return gameService.add(game);
    }

    @Override
    @Put("/{code}")
    public Game update(String code, Game game) {
        return gameService.update(code, game);
    }

    @Override
    @Delete("/{code}")
    public void delete(String code) {
        gameService.delete(code);
    }

}
