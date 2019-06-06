package com.hackbiu.framework.util;

import com.hackbiu.accessibility.FaceListener;
import com.hackbiu.game.state.State;

// the class handling with blinking by the user
public class BlinkHandler implements FaceListener {

    private State currentState;

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    @Override
    public void onBlink() {
        currentState.onBlink();
    }
}
