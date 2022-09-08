package com.example.smartplayer;

import android.media.MediaPlayer;

public class SwitchMusicEvent {


    private String url=null;
    private int seek=0;
    private boolean start=false;
    private boolean pause=false;
    private boolean switchto=false;
    private boolean mpbool=false;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSeek() {
        return seek;
    }

    public void setSeek(int seek) {
        this.seek = seek;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }
    public boolean isSwitchto() {
        return switchto;
    }

    public void setSwitchto(boolean switchto) {
        this.switchto = switchto;
    }

    public boolean isMpbool() {
        return mpbool;
    }

    public void setMpbool(boolean mpbool) {
        this.mpbool = mpbool;
    }
}
