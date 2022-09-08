package com.example.smartplayer;

public class GestureData {
    int fingers;
    String direction;

    public GestureData() {
    }

    public GestureData(int fingers, String direction) {
        this.fingers = fingers;
        this.direction = direction;
    }

    public int getFingers() {
        return fingers;
    }

    public void setFingers(int fingers) {
        this.fingers = fingers;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
