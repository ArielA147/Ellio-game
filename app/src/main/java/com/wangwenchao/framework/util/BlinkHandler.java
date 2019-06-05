package com.wangwenchao.framework.util;

import com.wangwenchao.accessibility.FaceListener;
import com.wangwenchao.game.state.State;

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
