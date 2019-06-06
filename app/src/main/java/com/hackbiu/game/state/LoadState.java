package com.hackbiu.game.state;

import android.view.MotionEvent;

import com.hackbiu.crocoboy.Assets;
import com.hackbiu.framework.util.Painter;

public class LoadState extends State{

	@Override
	public void init() {
		Assets.load();
	}

	@Override
	public void update(float delta) {
		setCurrentState(new MenuState());
	}

	@Override
	public void render(Painter g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaledX, int scaledY) {
		return false;
	}

	@Override
	public boolean onBlink(){
		return false;
	}

}
