package com.example.smartplayer;

public class MusicInformationEvent {
    private boolean isCompletion;
    private boolean isPrepared;
    private int Duration;
    private int CurrentPosition;

    public boolean isCompletion() {
        return isCompletion;
    }

    public void setCompletion(boolean completion) {
        isCompletion = completion;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public void setPrepared(boolean prepared) {
        isPrepared = prepared;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public int getCurrentPosition() {
        return CurrentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        CurrentPosition = currentPosition;
    }
}
