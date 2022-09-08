package com.example.smartplayer;

import java.io.Serializable;

public class Gestures implements Serializable {
    private String play="播放";
    private String suspend="暂停";
    private String amplification="音量增大";
    private String reduce="音量减小";
    private String forward="上一首";
    private String back="下一首";
    private String mute="静音";
    private String maxvolume="音量最大";
    private int awackentime=2000;
    private int requesttime=500;
    private String awackenword="你好";
    private String serverip="10.0.2.2";
    private String serverport="8080";
    private String cameraip;
    private String cameraport="8081";
    private String camerauser="admin";
    private String camerapassword="admin";
    private String CameraAgreement="http";
    private String CameraURL="video";
    private static final long serialVersionUID = 1L;
    public Gestures(){

    }
    public String getPlay() {
        return play;
    }

    public void setPlay(String play) {
        this.play = play;
    }

    public String getSuspend() {
        return suspend;
    }

    public void setSuspend(String suspend) {
        this.suspend = suspend;
    }

    public String getAmplification() {
        return amplification;
    }

    public void setAmplification(String amplification) {
        this.amplification = amplification;
    }

    public String getReduce() {
        return reduce;
    }

    public void setReduce(String reduce) {
        this.reduce = reduce;
    }

    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public String getMute() {
        return mute;
    }

    public void setMute(String mute) {
        this.mute = mute;
    }

    public String getMaxvolume() {
        return maxvolume;
    }

    public void setMaxvolume(String maxvolume) {
        this.maxvolume = maxvolume;
    }

    public int getAwackentime() {
        return awackentime;
    }

    public void setAwackentime(int awackentime) {
        this.awackentime = awackentime;
    }

    public String getAwackenword() {
        return awackenword;
    }

    public void setAwackenword(String awackenword) {
        this.awackenword = awackenword;
    }

    public String getServerip() {
        return serverip;
    }

    public void setServerip(String serverip) {
        this.serverip = serverip;
    }

    public String getServerport() {
        return serverport;
    }

    public void setServerport(String serverport) {
        this.serverport = serverport;
    }

    public String getCameraip() {
        return cameraip;
    }

    public void setCameraip(String cameraip) {
        this.cameraip = cameraip;
    }

    public String getCameraport() {
        return cameraport;
    }

    public void setCameraport(String cameraport) {
        this.cameraport = cameraport;
    }

    public String getCamerauser() {
        return camerauser;
    }

    public void setCamerauser(String camerauser) {
        this.camerauser = camerauser;
    }

    public String getCamerapassword() {
        return camerapassword;
    }

    public void setCamerapassword(String camerapassword) {
        this.camerapassword = camerapassword;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getRequesttime() {
        return requesttime;
    }

    public void setRequesttime(int requesttime) {
        this.requesttime = requesttime;
    }

    public String getCameraAgreement() {
        return CameraAgreement;
    }

    public void setCameraAgreement(String cameraAgreement) {
        CameraAgreement = cameraAgreement;
    }

    public String getCameraURL() {
        return CameraURL;
    }

    public void setCameraURL(String cameraURL) {
        CameraURL = cameraURL;
    }

    @Override
    public String toString() {
        return "Gestures{" +
                "play='" + play + '\'' +
                ", suspend='" + suspend + '\'' +
                ", amplification='" + amplification + '\'' +
                ", reduce='" + reduce + '\'' +
                ", forward='" + forward + '\'' +
                ", back='" + back + '\'' +
                ", mute='" + mute + '\'' +
                ", maxvolume='" + maxvolume + '\'' +
                ", awackentime=" + awackentime +
                ", serverrequest=" + requesttime +
                ", awackenword='" + awackenword + '\'' +
                ", serverip='" + serverip + '\'' +
                ", serverport='" + serverport + '\'' +
                ", cameraip='" + cameraip + '\'' +
                ", cameraport='" + cameraport + '\'' +
                ", camerauser='" + camerauser + '\'' +
                ", camerapassword='" + camerapassword + '\'' +
                '}';
    }
}
