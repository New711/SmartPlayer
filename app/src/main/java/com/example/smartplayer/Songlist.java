package com.example.smartplayer;

public class Songlist {
    private String songid;
    private String singer;
    private String songname;
    private String songFilename;
    private String Duration;

    public String getSongid() {
        return songid;
    }

    public void setSongid(String songid) {
        this.songid = songid;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public String getSongFilename() {
        return songFilename;
    }

    public void setSongFilename(String songFilename) {
        this.songFilename = songFilename;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }
}
