package com.example.smartplayer;

import  static android.content.Context.MODE_PRIVATE;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PlayFragment extends Fragment implements View.OnClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {


    private View root;
    private ImageView playx;
    private ImageView forward;
    private ImageView back;
    private TextView songtime;
    private TextView musictimemax;
    private TextView songname;
    private TextView singer;
//    private TextView gestureData;
    private SeekBar playseekbar;
    private ImageView laba;
    LinearLayout volumemin;
    LinearLayout volumemax;
    FrameLayout gestureOutline;
    private ImageView gestureview;
    Handler seekbarhandler;
    Handler voicehandler;
    Handler gesturehandler;
    Handler songListHandler;
    MediaPlayer mediaPlayer;
    Lock lock = new ReentrantLock();
    Gestures gestures;
    OkHttpClient okHttpClient;
    AudioManager audioManager;
    Thread getAyncthread;
    SongListAdapter songListAdapter;
    ListView songlistview;
    int plausongtime=0;
    int musictime;
    int time;
    int rousetime;
    int datanum=0;
    int fingersdata1,fingersdata2;
    int nowsong=0;
    int volumesize;
    int volumemute;
    String nowsongname;
    String awakenword;
    String TAG="TAG";
    String directiondata1,directiondata2;
    private static final  String mFileName="mydata";
    boolean lababool=true;
    boolean songlistbool=true;
    boolean mediaplayerbool=false;
    boolean isrun=true;
    private TextView play;
    static List<Songlist> songlists;
    private List<String> directionlist=new ArrayList<>();
    private List<Integer> fingerslist=new ArrayList<>();
    private List<GestureData> historyGesture=new ArrayList<>();
    private GestureData oldGesture=new GestureData(0,null);
    private boolean historyGestureBool=false;
    private boolean GestureState=true;
    private CameraServer mService = null;
    private int rotaion;
    private String Imagebase64;
    private GestureView gestureView;
    private SharedPreferences sp;


    public PlayFragment(){
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        historyGesture.add(new GestureData(0,""));
        historyGesture.add(new GestureData(0,""));
        historyGesture.add(new GestureData(0,""));

        rotaion=getActivity().getWindowManager().getDefaultDisplay().getRotation();
        if(cnn2!=null){
            getActivity().bindService(new Intent(getContext(),CameraServer.class)  , cnn2, getContext().BIND_AUTO_CREATE);
        }
        VoiceHandler();
        GestureHandler();
        setSongListHandler();
        isrun=true;
        getThread();
        getSongThread();
        getAyncthread.start();
    }
    ServiceConnection cnn2 = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService=((CameraServer.MsgBinder) iBinder).getService();
            ((CameraServer.MsgBinder) iBinder).setActivity(getActivity());
            mService.setCallService(new CameraServer.CallService() {
                @Override
                public void getImage(String base64) {
                    Message message=new Message();
                    message.what=1;
                    Bundle bundle=new Bundle();
                    bundle.putString("base64",base64);
                    message.setData(bundle);
                    ImageHandler.sendMessage(message);
                }
            });

        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("###", "onServiceDisconnected");
            mService = null;
        }
    };

    Handler ImageHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    String base64=msg.getData().getString("base64");
                    byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    if(rotaion==0){
                        decodedByte=rotatePicture(decodedByte,180);
                    }else if(rotaion==1){
                        decodedByte=rotatePicture(decodedByte,0);
                    }
                    gestureview.setImageBitmap(decodedByte);
                    gestureview.invalidate();
                    Imagebase64=base64;
                    break;
            }
        }
    };



    public Bitmap rotatePicture(final Bitmap bitmap, final int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        String mCameraID = sp.getString("cameraId",null);
        if(mCameraID==null||mCameraID.equals("1")){
            matrix.postScale(-1, 1);
        }
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBitmap;
    }

    @Override
    public void onStop() {
        super.onStop();
        isrun = false;
        if (seekbarhandler != null) {
            seekbarhandler.removeMessages(1);
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if(cnn2!=null){
            getActivity().unbindService(cnn2);
            cnn2=null;
        }
        historyGesture=new ArrayList<>();
        gestureOutline.removeView(gestureView);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer=null;
        }
        if(songListAdapter!=null){
            songListAdapter.setSonglists(null);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(root==null){
            root=inflater.inflate(R.layout.fragment_play, container, false);
        }
        sp =getContext().getSharedPreferences(mFileName, MODE_PRIVATE);
        Log.e(TAG, "onCreateView: sss");
        Gestures newgestures=getObject("gestures");
        if(newgestures!=null){
            gestures=newgestures;
        }else {
            gestures=new Gestures();
        }
        Inttdata(root);
        okHttpClient=new OkHttpClient.Builder()
                .connectTimeout(50L, TimeUnit.SECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
                .build();
        rousetime=gestures.getAwackentime();
        awakenword=gestures.getAwackenword();
        audioManager= (AudioManager) getContext().getSystemService(Service.AUDIO_SERVICE);
        rundata();
        return root;
    }
    public void rundata(){
        seekbarhandler=new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                boolean isSeekbar=false;
                switch (msg.what){
                    case 1:
                     if(musictime>0){
                        if(plausongtime<=musictime) {
                            plausongtime=mediaPlayer.getCurrentPosition()/1000;
                            if (plausongtime <= 60) {
                                if (plausongtime < 10)
                                    songtime.setText("00:0" + plausongtime);
                                else
                                    songtime.setText("00:" + plausongtime);
                            } else {
                                if (plausongtime % 60 < 10) {
                                    songtime.setText("0" + plausongtime / 60 + ":0" + plausongtime % 60);
                                } else {
                                    songtime.setText("0" + plausongtime / 60 + ":" + plausongtime % 60);
                                }
                            }
                            playseekbar.setProgress(plausongtime);
                            plausongtime++;
                        }
                        else{
                            plausongtime=0;
                            playx.setSelected(false);
                            isSeekbar=true;
                        }
                    }
                        Message message=new Message();
                        message.what=1;
                        this.sendMessageDelayed(message,1000);
                        if(isSeekbar){
                            this.removeMessages(1);
                        }
                     break;
                    case 2:
                        break;
                }


            }
        };
        playseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }@Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                time=playseekbar.getProgress();
                if(mediaPlayer!=null) {
                    mediaPlayer.seekTo(time * 1000);
                }
                if (time<=60){
                    if(time<10)
                        songtime.setText("00:0"+time);
                    else
                        songtime.setText("00:"+time);
                }
                else if (time % 60 < 10) {
                    songtime.setText("0" + time / 60 + ":0" + time % 60);
                }
                else {
                    songtime.setText("0" + time / 60 + ":" + time % 60);
                }
                plausongtime=time;
            }
        });
        volumesize=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        SetVolumesize(volumesize);
        getsonglist();
        setSonglists();
    }

    public void Inttdata(View view){
        play=view.findViewById(R.id.play);
        playx=view.findViewById(R.id.playx);
        forward=view.findViewById(R.id.forward);
        back=view.findViewById(R.id.back);
        songtime=view.findViewById(R.id.songtime);
        playseekbar=view.findViewById(R.id.playseekbar);
        laba=view.findViewById(R.id.labaimage);
        musictimemax=view.findViewById(R.id.musictimemax);
        volumemin=view.findViewById(R.id.volumemin);
        volumemax=view.findViewById(R.id.volumemax);
        singer=view.findViewById(R.id.singer);
        songname=view.findViewById(R.id.songname);
        songlistview=view.findViewById(R.id.songlist);
        gestureview=view.findViewById(R.id.gestureview);
        gestureOutline=view.findViewById(R.id.gestureOutline);
//        gestureData=view.findViewById(R.id.gestureData);
        gestureview.setOnClickListener(this);
        laba.setOnClickListener(this);
        volumemax.setOnClickListener(this);
        volumemin.setOnClickListener(this);
        playx.setOnClickListener(this);
        forward.setOnClickListener(this);
        back.setOnClickListener(this);
        songlists=new ArrayList<>();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        songlists=null;
    }

    @Override
    public void onClick(View view) {
        selectview(view);
    }
     public void selectview(View view){
         switch (view.getId()){
             case R.id.playx:
                 if (playx.isSelected()) {
                     play.setText(getContext().getString(R.string.Pause));
                     playx.setSelected(false);
                     seekbarhandler.removeMessages(1);
                     if(mediaPlayer!=null) {
                         mediaPlayer.pause();
                     }else{
                         inttMediaplayer();
                         mediaPlayer.pause();
                     }
                 }else{
                     play.setText(getContext().getString(R.string.Play));
                     playx.setSelected(true);
                     Message message=new Message();
                     message.what=1;
                     seekbarhandler.sendMessage(message);
                     if(mediaPlayer!=null) {
                         mediaPlayer.start();
                     }else{
                         inttMediaplayer();
                         mediaPlayer.start();
                     }
                 }break;
             case R.id.forward:forward.setImageResource(R.drawable.forwardon);
                 new Handler().postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         forward.setImageResource(R.drawable.forward);
                     }
                 },1000);
                 play.setText(getContext().getString(R.string.Play));
                 if(songlists.size()!=0){
                     if (nowsong==1) {
                         nowsong = songlists.size();
                         nowsongname="music/"+songlists.get(nowsong-1).getSongFilename();
                     }
                     else {
                         nowsong -= 1;
                         nowsongname="music/"+songlists.get(nowsong-1).getSongFilename();
                     }
                     songlistview.setSelection(nowsong-1);
                     songname.setText(songlists.get(nowsong-1).getSongname());
                     singer.setText(songlists.get(nowsong-1).getSinger());
                     startsong();
                 }
                 break;
             case R.id.back:back.setImageResource(R.drawable.backon);
                 new Handler().postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         back.setImageResource(R.drawable.back);
                     }
                 },1000);
                 play.setText(getContext().getString(R.string.Play));
                 if(songlists.size()!=0) {
                     if (nowsong==songlists.size()) {
                         nowsong =1;
                         nowsongname="music/"+songlists.get(nowsong-1).getSongFilename();
                     }
                     else {
                         nowsong += 1;
                         nowsongname="music/"+songlists.get(nowsong-1).getSongFilename();
                     }
                     songlistview.setSelection(nowsong-1);
                     songname.setText(songlists.get(nowsong-1).getSongname());
                     singer.setText(songlists.get(nowsong-1).getSinger());
                     startsong();
                 }
                 break;
             case R.id.labaimage:
                 if (lababool){
                     lababool=false;
                 volumemin.setVisibility(View.VISIBLE);
                 volumemax.setVisibility(View.VISIBLE);
                 }else{
                     lababool=true;
                     volumemin.setVisibility(View.GONE);
                     volumemax.setVisibility(View.GONE);
                 }
                 break;
             case R.id.volumemin:
                 if(volumesize>=1) {
                     volumesize -= 1;
                 }
                         SetVolumesize(volumesize);
                         audioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,AudioManager.FLAG_SHOW_UI);
                     break;
             case R.id.volumemax:
                     if(volumesize<=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)){
                         volumesize+=1;
                     }
                         SetVolumesize(volumesize);
                         audioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,AudioManager.FLAG_SHOW_UI);
                 break;
             case R.id.gestureview:
                 Log.e(TAG, "selectview: sss");
                 if(historyGestureBool){
                     historyGestureBool=false;
                     GestureState=true;
                 }else {
                     historyGestureBool=true;
                     GestureState=true;
                 }
             default:break;
         }
    }
    public void setSonglists(){
        songListAdapter=new SongListAdapter(songlists,getContext());
        songlistview.setAdapter(songListAdapter);
        songlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nowsong=position+1;
                nowsongname="music/"+songlists.get(nowsong-1).getSongFilename();
                startsong();
                songname.setText(songlists.get(position).getSongname());
                singer.setText(songlists.get(position).getSinger());
            }
        });
    }
    void startsong(){
        if(mediaPlayer!=null){
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        }
        inttMediaplayer();
        plausongtime=0;
        playx.setSelected(true);
        mediaplayerbool=true;
        Message message=new Message();
        message.what=1;
        seekbarhandler.sendMessage(message);
    }

    public void getMusictime() {
        if(musictime>0) {
            if(musictime<=60){
                if(musictime<10)
                    musictimemax.setText("00:0"+musictime);
                else
                    musictimemax.setText("00:"+musictime);
            }
            else if (musictime % 60 < 10) {
                musictimemax.setText("0" + musictime / 60 + ":0" + musictime % 60);
            }
            else {
                musictimemax.setText("0" + musictime / 60 + ":" + musictime % 60);
            }
        }
        if(musictime!=0){
            playseekbar.setMax(musictime);
        }
    }

    public void SetVolumesize(float volumesize) {
        if(volumesize==0){
            laba.setImageResource(R.drawable.laba1);
        }else if(volumesize>0&&volumesize<=5){
            laba.setImageResource(R.drawable.laba2);
        }else if(volumesize>5&&volumesize<=10){
            laba.setImageResource(R.drawable.laba3);
        }else if(volumesize>10&&volumesize<=15){
            laba.setImageResource(R.drawable.laba4);
        }
    }



    public <T> T getObject(String key) {
        
        if (sp.contains(key)) {
            String objectValue = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectValue, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public void getsonglist(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                Request request=new Request.Builder().url("http://"+gestures.getServerip()+":"+gestures.getServerport()+"/musicList").build();
                Call call=okHttpClient.newCall(request);
                try {
                    Response response=call.execute();
                    if(response.isSuccessful()){
                        String songlistdata=response.body().string();
                        JSONArray jsonArray=new JSONArray(songlistdata);
                        for (int i = 0; i <jsonArray.length() ; i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            Songlist songlist = new Songlist();
                            songlist.setSongid(jsonObject.getString("id"));
                            songlist.setSinger(jsonObject.getString("author"));
                            songlist.setSongname(jsonObject.getString("songName"));
                            songlist.setSongFilename(jsonObject.getString("songFilename"));
                            songlist.setDuration(getSongDuration(jsonObject.getInt("duration")));
                            songlists.add(songlist);
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    String getSongDuration(int duration) {
        String newduration;
        if (duration <= 60) {
            if (duration < 10)
                newduration="00:0" + duration;
            else
               newduration="00:" + duration;
        } else {
            if (duration % 60 < 10) {
                newduration = "0" + duration / 60 + ":0" + duration % 60;
            } else {
                newduration = "0" + duration / 60 + ":" + duration % 60;
            }
        }
        return newduration;
    }

    void getSongThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isData=true;
                while (isData){
                    if(songlists.size()!=0&&songlistbool){
                        songlistbool=false;
                        isData=false;
                        Message message=new Message();
                        songListHandler.sendMessage(message);
                    }
                }
            }
        }).start();
    }
    void setSongListHandler(){
        songListHandler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                songListAdapter.notifyDataSetChanged();
                nowsongname="music/"+songlists.get(0).getSongFilename();
                songname.setText(songlists.get(0).getSongname());
                singer.setText(songlists.get(0).getSinger());
            }
        };
    }
    void getThread(){
        getAyncthread=new Thread(new Runnable() {
            @Override
            public void run() {
                int size;
                String direction;
                int fingers;
                boolean gesturebool;
                float[] floats = null;
//                Request request=new Request.Builder().url("http://"+gestures.getServerip()+":"+gestures.getServerport()+"/getGestureX.json?ip="+gestures.getCameraip()+"&port="+gestures.getCameraport()+"&uname="+gestures.getCamerauser()+"&pwd="+gestures.getCamerapassword()+"&protocol="+gestures.getCameraAgreement()+"&uri="+gestures.getCameraURL()+"&base64=").build();
                while (isrun){
                    try {
                        if(Imagebase64!=null) {
                            RequestBody formBody = new FormBody.Builder()
                                    .add("ip", gestures.getCameraip() + "")
                                    .add("port", gestures.getCameraport() + "")
                                    .add("uname", gestures.getCamerauser() + "")
                                    .add("pwd", gestures.getCamerapassword() + "")
                                    .add("protocol", gestures.getCameraAgreement() + "")
                                    .add("uri", gestures.getCameraURL() + "")
                                    .add("base64", Imagebase64 + "")
                                    .build();
                            String path="http://"+gestures.getServerip()+":"+gestures.getServerport()+"/getGestureXX.json";
                            Request request=new Request.Builder().url(path).post(formBody).build();
                            Call call=okHttpClient.newCall(request);
                            Response response=call.execute();
                            if(response.isSuccessful()){
                                String responsedate =response.body().string();
                                try {
                                    gesturebool=false;
                                    JSONObject jsonObject = new JSONObject(responsedate);
                                    fingers = jsonObject.getInt("fingers");
                                    direction = jsonObject.getString("direction");
                                    size = jsonObject.getInt("size");
                                    floats=getgesture(jsonObject,floats);
                                    fingerslist.add(fingers);
                                    directionlist.add(direction);
                                    if (directionlist.size() == 5 && fingerslist.size() == 5) {
                                        boolean directionbool = false;
                                        boolean fingersbool = false;
                                        for (int i = 0; i < directionlist.size(); i++) {
                                            for (int j = 0; j < directionlist.size(); j++) {
                                                directionbool = directionlist.get(i).equals(directionlist.get(j));
                                                if (!directionbool) {
                                                    break;
                                                }
                                            }
                                        }
                                        for (int x = 0; x < fingerslist.size(); x++) {
                                            for (int y = 0; y < fingerslist.size(); y++) {
                                                fingersbool = fingerslist.get(x) == fingerslist.get(y);
                                                if (!fingersbool) {
                                                    break;
                                                }
                                            }
                                        }
                                        getHistoryGesture();
                                        Log.e(TAG, "run: "+historyGesture.size() );
                                        if (directionbool && fingersbool) {
                                            Log.e(TAG, "run: "+direction+"  "+fingers );
                                            directionlist.clear();
                                            fingerslist.clear();
                                            if(historyGesture.size()>=2){
                                                if((direction.equals("left")&&fingers==2)||(direction.equals("right")&&fingers==2)){
                                                    gestureVolume(direction,fingers);
                                                }else {
                                                    String historyDirection=historyGesture.get(historyGesture.size()-1).getDirection();
                                                    int historyFingers=historyGesture.get(historyGesture.size()-1).getFingers();
                                                    if(!direction.equals(historyDirection)||fingers!=historyFingers){
                                                        historyGesture.add(new GestureData(fingers,direction));
                                                    }
                                                }
                                            }


                                        }else {
                                            gesturebool = false;
                                            directionlist.remove(0);
                                            fingerslist.remove(0);
                                        }
                                    }

//                                    Bundle voicebundle=new Bundle();
//                                    voicebundle.putString("direction",direction);
//                                    voicebundle.putBoolean("gesturebool",gesturebool);
//                                    voicebundle.putInt("fingers",fingers);
//                                    Message voicemessage=new Message();
//                                    voicemessage.what=1;
//                                    voicemessage.setData(voicebundle);
//                                    voicehandler.sendMessage(voicemessage);
//                                    voicebundle=null;
//                                    voicemessage=null;

                                    Bundle gesturebundle=new Bundle();
                                    gesturebundle.putFloatArray("floats",floats);
                                    Message gesturemessage=new Message();
                                    gesturemessage.what=1;
                                    gesturemessage.setData(gesturebundle);
                                    if(floats!=null){
                                        gesturehandler.sendMessage(gesturemessage);
                                    }
                                    response=null;
                                    jsonObject=null;
                                    floats=null;
                                    gesturebundle=null;
                                    gesturemessage=null;

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    void gestureVolume(String direction,int fingers){
            Bundle voicebundle = new Bundle();
            voicebundle.putString("direction",direction );
            voicebundle.putInt("fingers", fingers);
            Message voicemessage = new Message();
            voicemessage.what = 1;
            voicemessage.setData(voicebundle);
            voicehandler.sendMessage(voicemessage);
            voicemessage = null;
    }

    float[] getgesture(JSONObject jsonObject, float[] floats) throws JSONException {
        if (!jsonObject.isNull("contours")) {//轮廓数据处理

            JSONArray contoursarray = jsonObject.getJSONArray("contours");
            floats = new float[contoursarray.length()*2];
            JSONObject contours;
            for (int i = 0; i < contoursarray.length(); i++) {
                contours = contoursarray.getJSONObject(i);
                int x = contours.getInt("x");
                int y = contours.getInt("y");
                x = 639 - x;
                if (x < 1) {
                    x = 1;
                }
                if (x > 638) {
                    x = 638;
                }
                if (y < 1) {
                    y = 1;
                }
                if (y > 478) {
                    y = 478;
                }
                floats[i*2]=x;
                floats[i*2+1]=y;
            }
            contoursarray=null;
            contours=null;
            jsonObject=null;
        }//轮廓数据处理
        return floats;
    }

     void getHistoryGesture(){
        if(historyGesture.size()==3){
            Log.e(TAG, "getHistoryGesture: "+historyGesture.get(0).getDirection()+"  "+historyGesture.get(1).getFingers()+"  "+historyGesture.get(2).getDirection() );
            if((historyGesture.get(0).getFingers()==4)&&
                    (historyGesture.get(1).getFingers()==0)&&
                    (historyGesture.get(2).getFingers()==4)
            ){
                if(historyGestureBool){
                    historyGestureBool=false;
                    GestureState=true;
                }else {
                    historyGestureBool=true;
                    GestureState=true;
                }
            }


                Bundle voicebundle = new Bundle();
                voicebundle.putString("direction", historyGesture.get(2).getDirection());
                voicebundle.putInt("fingers", historyGesture.get(2).getFingers());
                Message voicemessage = new Message();
                voicemessage.what = 1;
                voicemessage.setData(voicebundle);
                voicehandler.sendMessage(voicemessage);
                voicemessage = null;
            historyGesture.remove(0);
        }
    }

    void VoiceHandler(){
        voicehandler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        Bundle bundle=msg.getData();
//                        gesturesShow(bundle.getString("direction"), bundle.getInt("fingers"));
                        if(historyGestureBool){
                        switchgesture(bundle.getString("direction"),bundle.getInt("fingers"));
                            bundle=null;
                        }
                        break;
                    case 2:
                        break;
                }

            }
        };
    }
//    void gesturesShow(String direction,int fingers){
//        if(isAdded()){
//            switch (direction){
//                case "upper":
//                    gestureData.setText(getResources().getString(R.string.DirectionUpper)+" 手指数:"+fingers);
//                    break;
//                case "right":
//                    gestureData.setText(getResources().getString(R.string.DirectionRight)+" 手指数:"+fingers);
//                    break;
//                case "left":
//                    gestureData.setText(getResources().getString(R.string.DirectionLeft)+" 手指数:"+fingers);
//                    break;
//                case "rightUpper":
//                    gestureData.setText(getResources().getString(R.string.DirectionRightUpper)+" 手指数:"+fingers);
//                    break;
//                case "leftUpper":
//                    gestureData.setText(getResources().getString(R.string.DirectionLeftUpper)+" 手指数:"+fingers);
//                    break;
//            }
//            if(direction==null){
//                gestureData.setText(getResources().getString(R.string.DefaultGestrue));
//            }
//        }
//    }
    void GestureHandler(){

        gestureView=new GestureView(getContext());
        gestureOutline.addView(gestureView);

        gesturehandler=new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        if(historyGestureBool&&GestureState){
                            GestureState=false;
                            if(getContext()!=null){
                                gestureview.setBackgroundResource(R.drawable.awaken_on);
                                Toast.makeText(getContext(),"手势已唤醒",Toast.LENGTH_LONG).show();
                            }
                        }
                        if(!historyGestureBool&&GestureState){
                            GestureState=false;
                            if(getContext()!=null) {
                                gestureview.setBackgroundResource(R.drawable.awaken_off);
                                Toast.makeText(getContext(), "手势未唤醒", Toast.LENGTH_LONG).show();
                            }
                        }
                        float[] floats=msg.getData().getFloatArray("floats");
                        try {
                            lock.lock();
                            gestureView.setFloats(floats);
                            gestureView.invalidate();
                        }finally {
                            lock.unlock();
                        }

                        floats=null;

                        break;
                    case 2: break;
                }

            }
        };
    }

    boolean switchgesture(String direction,int fingers){
        if(direction!=null){
            switch (direction) {
                case "upper":
                    if(fingers==2){
                        if(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)==0){
                            volumesize=volumemute;
                            SetVolumesize(volumesize);
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volumesize,AudioManager.FLAG_SHOW_UI);
                        }else {
                            volumemute =audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            volumesize=0;
                            SetVolumesize(volumesize);
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,0,AudioManager.FLAG_SHOW_UI);
                        }
                    }else if(fingers==1){
                        if(playx.isSelected()){
                            play.setText(getContext().getString(R.string.Pause));
                            playx.setSelected(false);
                            seekbarhandler.removeMessages(1);
                            if(mediaPlayer!=null){
                                mediaPlayer.pause();
                            }else{
                                inttMediaplayer();
                                mediaPlayer.pause();
                            }
                        }else {
                            play.setText(getContext().getString(R.string.Play));
                            playx.setSelected(true);
                            Message message=new Message();
                            message.what=1;
                            seekbarhandler.sendMessage(message);
                            if(mediaPlayer!=null){
                                mediaPlayer.start();
                            }else{
                                inttMediaplayer();
                                mediaPlayer.start();
                            }
                        }
                    }
                    break;
                case "left":
                    if(fingers==1) {
                        forward.setImageResource(R.drawable.forwardon);
                        play.setText(getContext().getString(R.string.Play));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                forward.setImageResource(R.drawable.forward);
                            }
                        }, 1000);
                        if (nowsong==1) {
                            nowsong = songlists.size();
                            nowsongname="music/"+songlists.get(nowsong-1).getSongFilename();
                        }
                        else {
                            nowsong -= 1;
                            nowsongname="music/"+songlists.get(nowsong-1).getSongFilename();
                        }
                        songlistview.setSelection(nowsong-1);
                        songname.setText(songlists.get(nowsong-1).getSongname());
                        singer.setText(songlists.get(nowsong-1).getSinger());
                        startsong();
                    }else if(fingers==2){
                        if(volumesize>=1){
                            volumesize-=1;
                        }
                            SetVolumesize(volumesize);
                            audioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,AudioManager.FLAG_SHOW_UI);
                    }
                    break;
                case "right":
                    if(fingers==1) {
                        play.setText(getContext().getString(R.string.Play));
                        back.setImageResource(R.drawable.backon);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                back.setImageResource(R.drawable.back);
                            }
                        }, 1000);
                        if (nowsong==songlists.size()) {
                            nowsong =1;
                            nowsongname="music/"+songlists.get(nowsong-1).getSongFilename();
                        }
                        else {
                            nowsong += 1;
                            nowsongname="music/"+songlists.get(nowsong-1).getSongFilename();
                        }
                        songlistview.setSelection(nowsong-1);
                        songname.setText(songlists.get(nowsong-1).getSongname());
                        singer.setText(songlists.get(nowsong-1).getSinger());
                        startsong();
                    }else if(fingers==2){
                        if(volumesize<=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)){
                            volumesize+=1;
                        }
                        SetVolumesize(volumesize);
                        audioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,AudioManager.FLAG_SHOW_UI);
                    }
                    break;
            }
        }
        return true;
    }
    void inttMediaplayer(){
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        try {
            Log.e(TAG, "inttMediaplayer: "+nowsongname );
            String url="http://"+gestures.getServerip()+":"+gestures.getServerport()+"/"+nowsongname+"";
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
                e.printStackTrace();
            }
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        if(mediaPlayer!=null) {
            musictime = mediaPlayer.getDuration() / 1000;
        }
        getMusictime();
        if(mediaplayerbool) {
            mediaplayerbool=false;
            mediaPlayer.start();
        }

    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        if(songlists.size()!=0){
            if (nowsong==songlists.size()) {
                nowsong =1;
                nowsongname="music/"+songlists.get(nowsong-1).getSongFilename();
            }
            else {
                nowsong += 1;
                nowsongname="music/"+songlists.get(nowsong-1).getSongFilename();
            }
            songname.setText(songlists.get(nowsong-1).getSongname());
            singer.setText(songlists.get(nowsong-1).getSinger());
            startsong();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaPlayer.stop();
        mediaPlayer.reset();
        return false;
    }
}