package com.wangwenchao.accessibility;

public class TestListner implements FaceListner {
    public static boolean test;
    @Override
    public void onBlink(){
        test = !test;
    }
}
