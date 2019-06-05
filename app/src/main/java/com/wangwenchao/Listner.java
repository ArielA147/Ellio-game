package com.wangwenchao;


import com.wangwenchao.accessibility.FaceListner;
import com.wangwenchao.game.model.Player;

public class Listner implements FaceListner {
    private Player player;

    @Override
    public void onBlink() {
        player.jump();
    }
}
