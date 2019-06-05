package com.wangwenchao.ellio;

import com.wangwenchao.game.model.Player;
import com.wangwenchao.game.state.PlayState;

import java.util.Map;

public class ServerListner {
    private PlayState state;
    private Map<Integer, Player> idToPlayers;

    public void notifyNewPlayer(int id){
        Player newPlayer = state.addPlayer();
        idToPlayers.put(id,newPlayer);
    }

    public void notifyJump(int id){
        idToPlayers.get(id).jump();
    }


    public void notifyDuck(int id){
        idToPlayers.get(id).duck();
    }
}
