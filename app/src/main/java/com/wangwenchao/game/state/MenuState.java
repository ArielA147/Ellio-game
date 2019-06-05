package com.wangwenchao.game.state;

import android.util.Log;
import android.view.MotionEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import com.wangwenchao.ellio.Assets;
import com.wangwenchao.ellio.GameMainActivity;
import com.wangwenchao.framework.util.Painter;
import com.wangwenchao.framework.util.UIButton;

public class MenuState extends State {
    private UIButton playButton, scoreButton, muteButton, unmuteButton;
    private Date arrowDate;
    private int arrowIndex;
    private int arrowX, arrowY;
    boolean doubleBackToExitPressedOnce;
    private final int arrowX1 = 227;
    private final int arrowX2 = 227;
    private final int arrowY1 = 240;
    private final int arrowY2 = 300;

    @Override
    public void init() {
        playButton = new UIButton(316, 227, 484, 286, Assets.start, Assets.startDown);
        scoreButton = new UIButton(316, 300, 484, 359, Assets.score, Assets.scoreDown);
        muteButton = new UIButton(0, 0, 50, 48, Assets.musicOn,
                Assets.musicOff);
        unmuteButton = new UIButton(0, 0, 50, 48, Assets.musicOff,
                Assets.musicOn);
        arrowDate = Calendar.getInstance().getTime();
        arrowIndex = 0;
        arrowX = arrowX1;
        arrowY = arrowY1;
    }

    @Override
    public void update(float delta) {
        // TODO Auto-generated method stub

    }

    @Override
    public void render(Painter g) {
        g.drawImage(Assets.welcome, 0, 0);
        if ((Calendar.getInstance().getTime().getSeconds()) - arrowDate.getSeconds() >= 1) {
            if (arrowY == arrowY1)
                arrowY = arrowY2;
            else
                arrowY = arrowY1;
            arrowDate = Calendar.getInstance().getTime();
        }
        g.drawImage(Assets.arrow, arrowX, arrowY);
        arrowDate = Calendar.getInstance().getTime();
        playButton.render(g);
        scoreButton.render(g);
        if (GameMainActivity.isMuted()) {
            unmuteButton.render(g);
        } else {
            muteButton.render(g);
        }

    }

    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            playButton.onTouchDown(scaledX, scaledY);
            scoreButton.onTouchDown(scaledX, scaledY);
            if (GameMainActivity.isMuted()) {
                unmuteButton.onTouchDown(scaledX, scaledY);
            } else {
                muteButton.onTouchDown(scaledX, scaledY);
            }
        }

        if (e.getAction() == MotionEvent.ACTION_UP) {
            if (playButton.isPressed(scaledX, scaledY)) {
                playButton.cancel();
                Log.d("MenuState", "Play Button Pressed!");
                setCurrentState(new PlayState());
            } else if (scoreButton.isPressed(scaledX, scaledY)) {
                scoreButton.cancel();
                Log.d("MenuState", "Score Button Pressed!");
                setCurrentState(new ScoreState());
            } else if (muteButton.isPressed(scaledX, scaledY)) {
                muteButton.cancel();
                Assets.onMute();
                GameMainActivity.setMuted(true);
            } else if (unmuteButton.isPressed(scaledX, scaledY)) {
                unmuteButton.cancel();
                Assets.onUnmute();
                GameMainActivity.setMuted(false);
            } else {
                playButton.cancel();
                scoreButton.cancel();
                muteButton.cancel();
                unmuteButton.cancel();
            }
        }
		
		/*if (e.getAction() == MotionEvent.ACTION_DOWN) {
			if (playRect.contains(scaledX, scaledY)) {
				playDown = true;
				scoreDown = false;
			}else if (scoreRect.contains(scaledX,scaledY)) {
				scoreDown = true;
				playDown = false;
			}
		}
		if (e.getAction() == MotionEvent.ACTION_UP) {
			if (playDown && playRect.contains(scaledX, scaledY)) {
				playDown = false;
				Log.d("MenuState", "Play Button Pressed!");
			}else if (scoreDown && scoreRect.contains(scaledX, scaledY)) {
				scoreDown = false;
				Log.d("MenuState", "Score Button Pressed!");
			}else {
				scoreDown = false;
				playDown = false;
			}
		}*/

        return true;
    }

    @Override
    public void onBackPressed() {

        System.exit(0);
    }
}
