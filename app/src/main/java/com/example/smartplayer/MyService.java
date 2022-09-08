package com.example.smartplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

public class MyService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private static final String TAG = "TAG";
    private MediaPlayer mediaPlayer;
    private boolean IsPlayer=false;
    private boolean isSetData=false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        EventBus.getDefault().register(this);
        Message message=new Message();
        message.what=1;
        handler.sendMessage(message);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchMusicEventBus(SwitchMusicEvent musicEventBus){
        switchto(musicEventBus.isSwitchto());
        IsPlayer=musicEventBus.isMpbool();
        if(musicEventBus.getUrl()!=null){
            Log.e(TAG, "onMusicEventBus: " );
            IntMediaPlayer(musicEventBus.getUrl());
        }
        mpSeek(musicEventBus.getSeek());
        mpStart(musicEventBus.isStart());
        mpPause(musicEventBus.isPause());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer=null;
        Message message=new Message();
        message.what=2;
        handler.sendMessage(message);
        EventBus.getDefault().unregister(this);
    }
    void IntMediaPlayer(String url){
        try {
            Log.e(TAG, "inttMediaplayer: "+url );
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            isSetData = false;
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
               case  1:
                   if(mediaPlayer.isPlaying()){
                       MusicInformationEvent mie=new MusicInformationEvent();
                       mie.setCurrentPosition(mediaPlayer.getCurrentPosition());
                       EventBus.getDefault().post(mie);
                   }
                Message message=new Message();
                message.what=1;
                this.sendMessageDelayed(message,500);
                break;
                case 2:break;

            }
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        MusicInformationEvent mie=new MusicInformationEvent();
        mie.setCompletion(true);
        EventBus.getDefault().post(mie);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        MusicInformationEvent mie=new MusicInformationEvent();
        mie.setPrepared(true);
        mie.setDuration(mediaPlayer.getDuration()/1000);
        EventBus.getDefault().post(mie);
//        getMusictime();
        if(IsPlayer) {
            IsPlayer=false;
            mediaPlayer.start();
        }
    }
    void mpSeek(int seek){
        if(seek>0){
            mediaPlayer.seekTo(seek);
        }
    }
    void mpStart(boolean start){
        if(start){
            mediaPlayer.start();
        }
    }
    void mpPause(boolean pause){
        if(pause){
            mediaPlayer.pause();
        }
    }
    void switchto(boolean switchto){
        if(switchto){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }
}
