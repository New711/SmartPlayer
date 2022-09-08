package com.example.smartplayer;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


public class CameraServer extends Service {

    private Camera camera;
    private Activity activity;
    private String base64str;
    private CallService callService;
//    final Messenger Mmessenger=new Messenger(new ServiceHandler());
    private String TAG="TAG";
    boolean imgDataBool=true;
    private static final  String mFileName="mydata";


    public CameraServer() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MsgBinder();
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);

    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(camera!=null){
            camera.closeCamera();
            camera=null;
        }
    }


    public void setCallService(CallService callService){
        this.callService=callService;
    }

    public interface CallService{
        void getImage(String base64);
    }

    public class MsgBinder extends Binder {
        public CameraServer getService(){
            return CameraServer.this;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void  setActivity(Activity activity1){
            camera=new Camera(activity1);
            SharedPreferences sp =activity1.getSharedPreferences(mFileName, activity1.MODE_PRIVATE);
//            String oldCameraId=sp.getString("oldCameraId",null);
//            String cameraId=sp.getString("cameraId",null);
//            Log.e(TAG, "setActivity: "+cameraId );
//            if(oldCameraId!=null&&!oldCameraId.equals(cameraId)){
//                camera.switchCamera(cameraId);
//            }
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putString("oldCameraId",cameraId);
//            editor.commit();
//            Log.e("TAG", "setActivity: sss" );
            camera.setCallImage(new Camera.CallImage() {
                @Override
                public void getImage(String base64) {
                    if(callService!=null){
                        callService.getImage(base64);
                    }

                }
            });

        }
    }

    //    AIDL_Service.Stub stub=new AIDL_Service.Stub() {
//        @Override
//        public void AIDL_Service() throws RemoteException {
//        }
//    };

//    class ServiceHandler extends Handler{
//
//        @RequiresApi(api = Build.VERSION_CODES.M)
//        @Override
//        public void handleMessage(Message msg) {
//            Bundle bundle=new Bundle();
//
////            camera=new Camera();
//            Log.e("TAG", "setActivity: sss" );
//            camera.setCallImage(new Camera.CallImage() {
//                @Override
//                public void getImage(String base64) {
//                    if(base64!=null){
//                        base64str=base64;
//                        Log.e("TAG", "getImage: sss");
//                        Messenger messenger=msg.replyTo;
//                        Message msgReplyTo=Message.obtain();
//                        Bundle base64Bundle=new Bundle();
//                        base64Bundle.putString("base64",base64);
//                        msgReplyTo.setData(base64Bundle);
//                        try {
//                            messenger.send(msgReplyTo);
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//            });
//        }
//    }




}
