/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wangwenchao.accessibility;

import android.graphics.PointF;

//import com.google.android.gms.samples.vision.face.googlyeyes.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.wangwenchao.accessibility.ui.camera.GraphicOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tracks the eye positions and state over time, managing an underlying graphic which renders googly
 * eyes over the source video.<p>
 *
 * To improve eye tracking performance, it also helps to keep track of the previous landmark
 * proportions relative to the detected face and to interpolate landmark positions for future
 * updates if the landmarks are missing.  This helps to compensate for intermediate frames where the
 * face was detected but one or both of the eyes were not detected.  Missing landmarks can happen
 * during quick movements due to camera image blurring.
 */
public class GooglyFaceTracker extends Tracker<Face> {
    //TODO: del it
    public static boolean test = false;

    private static final float EYE_CLOSED_THRESHOLD = 0.6f;

    private GraphicOverlay mOverlay;
    private GooglyEyesGraphic mEyesGraphic;

    //listners for the events
    private List<FaceListener> leftWtikleLisner;
    private List<FaceListener> rightWtikleLisner;
    private List<FaceListener> blinkListener;

    // Record the previously seen proportions of the landmark locations relative to the bounding box
    // of the face.  These proportions can be used to approximate where the landmarks are within the
    // face bounding box if the eye landmark is missing in a future update.
    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();

    // Similarly, keep track of the previous eye open state so that it can be reused for
    // intermediate frames which lack eye landmarks and corresponding eye state.
    private boolean mPreviousIsLeftOpen = true;
    private boolean mPreviousIsRightOpen = true;


    //==============================================================================================
    // Methods
    //==============================================================================================

    public GooglyFaceTracker(GraphicOverlay overlay) {
        mOverlay = overlay;
        rightWtikleLisner = new ArrayList<>();
        leftWtikleLisner = new ArrayList<>();
        blinkListener = new ArrayList<>();
        addBlinkListener(new TestListener());
    }

    /**
     * Resets the underlying googly eyes graphic and associated physics state.
     */
    @Override
    public void onNewItem(int id, Face face) {
        mEyesGraphic = new GooglyEyesGraphic(mOverlay);
    }

    /**
     * Updates the positions and state of eyes to the underlying graphic, according to the most
     * recent face detection results.  The graphic will render the eyes and simulate the motion of
     * the iris based upon these changes over time.
     */
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        mOverlay.add(mEyesGraphic);

        updatePreviousProportions(face);

        PointF leftPosition = getLandmarkPosition(face, Landmark.LEFT_EYE);
        PointF rightPosition = getLandmarkPosition(face, Landmark.RIGHT_EYE);

        float leftOpenScore = face.getIsLeftEyeOpenProbability();

        boolean isLeftTwikle = false;
        boolean isRightTwikle = false;

        boolean isLeftOpen;
        if (leftOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            isLeftOpen = mPreviousIsLeftOpen;
        } else {
            isLeftOpen = (leftOpenScore > EYE_CLOSED_THRESHOLD);
            if (mPreviousIsLeftOpen && ! isLeftOpen){
                isLeftTwikle = true;
            }
            mPreviousIsLeftOpen = isLeftOpen;
        }


        float rightOpenScore = face.getIsRightEyeOpenProbability();
        boolean isRightOpen;
        if (rightOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            isRightOpen = mPreviousIsRightOpen;
        } else {
            isRightOpen = (rightOpenScore > EYE_CLOSED_THRESHOLD);
            if (mPreviousIsRightOpen && ! isRightOpen){
                isRightTwikle = true;
            }
            mPreviousIsRightOpen = isRightOpen;
        }

        //notify the listeners
        if (isLeftTwikle && isRightTwikle){
            notifyAll(blinkListener);
        }else if(isLeftTwikle){
            notifyAll(leftWtikleLisner);
        }else if(isRightTwikle){
            notifyAll(rightWtikleLisner);
        }


        mEyesGraphic.updateEyes(leftPosition, isLeftOpen, rightPosition, isRightOpen);
    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily (e.g., if the face was momentarily blocked from
     * view).
     */
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        mOverlay.remove(mEyesGraphic);
    }

    /**
     * Called when the face is assumed to be gone for good. Remove the googly eyes graphic from
     * the overlay.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mEyesGraphic);
    }


    //add the listners
    public void addLeftTwikleLisner(FaceListener listner){
        leftWtikleLisner.add(listner);
    }
    public void addRighTwikleLisner(FaceListener listner){
        rightWtikleLisner.add(listner);
    }

    public void addBlinkListener(FaceListener listener){
        blinkListener.add(listener);
    }


    //==============================================================================================
    // Private
    //==============================================================================================

    private void updatePreviousProportions(Face face) {
        for (Landmark landmark : face.getLandmarks()) {
            PointF position = landmark.getPosition();
            float xProp = (position.x - face.getPosition().x) / face.getWidth();
            float yProp = (position.y - face.getPosition().y) / face.getHeight();
            mPreviousProportions.put(landmark.getType(), new PointF(xProp, yProp));
        }
    }

    /**
     * Finds a specific landmark position, or approximates the position based on past observations
     * if it is not present.
     */
    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }

        PointF prop = mPreviousProportions.get(landmarkId);
        if (prop == null) {
            return null;
        }

        float x = face.getPosition().x + (prop.x * face.getWidth());
        float y = face.getPosition().y + (prop.y * face.getHeight());
        return new PointF(x, y);
    }

    //notify the lisners on the events
    private void notifyAll(List<FaceListener> listners){
        happand();
        for (FaceListener listner : listners){
            listner.onBlink();
        }
    }


    //for tests
    private void happand(){
        test = !test;
    }

}





