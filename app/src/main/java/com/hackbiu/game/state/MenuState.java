package com.hackbiu.game.state;

import android.util.Log;
import android.view.MotionEvent;

import java.util.Calendar;
import java.util.Date;

import com.hackbiu.crocoboy.Assets;
import com.hackbiu.crocoboy.GameMainActivity;
import com.hackbiu.framework.util.Painter;
import com.hackbiu.framework.util.UIButton;

public class MenuState extends State {
	private static final String TAG = "touch on pressed down";
    private UIButton playButton, scoreButton, muteButton, unmuteButton, multipplayerButton;
    private Date arrowDate;
    private boolean arrowIndex;
    private int arrowX, arrowY;
    boolean doubleBackToExitPressedOnce;
    private final int arrowX1 = 227;
    private final int arrowX2 = 227;
    private final int arrowY1 = 227;
    private final int arrowY2 = 305;

    @Override
    public void init() {
        playButton = new UIButton(316, 227, 484, 286, Assets.start, Assets.startDown);
        multipplayerButton = new UIButton(316, 300, 484, 365, Assets.multiplayer, Assets.multiplayerDown);
        muteButton = new UIButton(0, 0, 50, 48, Assets.musicOn,
                Assets.musicOff);
        unmuteButton = new UIButton(0, 0, 50, 48, Assets.musicOff,
                Assets.musicOn);
        arrowDate = Calendar.getInstance().getTime();
        arrowIndex = false;
        arrowX = arrowX1;
        arrowY = arrowY1;
    }

    @Override
    public void update(float delta) {
        // TODO Auto-generated method stub

    }

    @Override
    public void render(Painter g) {
        g.drawImage(Assets.menuBg, 0, 0);
//        g.drawImage(Assets.crocs, 300, 200);
        g.drawImage(Assets.crocs,200,60,400,102);
        if ((Calendar.getInstance().getTime().getSeconds()) - arrowDate.getSeconds() >= 1) {
            if (arrowY == arrowY1)
                arrowY = arrowY2;
            else
                arrowY = arrowY1;
            arrowDate = Calendar.getInstance().getTime();
            arrowIndex = !arrowIndex;
        }
        g.drawImage(Assets.arrow, arrowX, arrowY);
        arrowDate = Calendar.getInstance().getTime();
        playButton.render(g);
        multipplayerButton.render(g);
        if (GameMainActivity.isMuted()) {
            unmuteButton.render(g);
        } else {
            muteButton.render(g);
        }

    }

    @Override
    public boolean onBlink() {
        // if blink == true , do the "key pressed of the play game"
        // if the user blinked in the menu - the state will be starting the game
//        setCurrentState(new PlayState());
        // if playButton was "pressed" by the user
        if (!arrowIndex) {
            playButton.cancel();
            Log.d("MenuState", "Play Button Pressed!");
            setCurrentState(new PlayState());
        } else {
            // HERE WILL BE LOGIC OF MULTIPLAYER MODE.
            setCurrentState(new AIState());
        }
        return true;
    }


    @Override
    public boolean onTouch(MotionEvent e, int scaledX, int scaledY) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            playButton.onTouchDown(scaledX, scaledY);
            multipplayerButton.onTouchDown(scaledX, scaledY);
            if (GameMainActivity.isMuted()) {
                unmuteButton.onTouchDown(scaledX, scaledY);
            } else {
                muteButton.onTouchDown(scaledX, scaledY);
            }
        }

        if (e.getAction() == MotionEvent.ACTION_UP) {
            // if playButton was "pressed" by the user
            if (playButton.isPressed(scaledX, scaledY)) {
                playButton.cancel();
                Log.d("MenuState", "Play Button Pressed!");
                setCurrentState(new PlayState());
            } else if (multipplayerButton.isPressed(scaledX, scaledY)){
                // HERE WILL BE LOGIC OF MULTIPLAYER MODE.
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
                multipplayerButton.cancel();
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
