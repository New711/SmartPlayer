package com.example.smartplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;

public class Camera {
    private Handler mCameraHandler;
    private HandlerThread mHandlerThread;
    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private String mCameraID;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private CaptureRequest mCaptureRequest;
    private CameraCaptureSession mCameraCaptureSession;
    private ImageReader imageReader;
    private static final SparseArray ORIENTATION=new SparseArray();
    private Activity activity;
    private CallImage callImage;
    private String base64;
    Size PreviewSize;
    private static final  String mFileName="mydata";

    static {
        ORIENTATION.append(Surface.ROTATION_0,90);
        ORIENTATION.append(Surface.ROTATION_90,0);
        ORIENTATION.append(Surface.ROTATION_180,270);
        ORIENTATION.append(Surface.ROTATION_270,180);
    }

    private String TAG="TAG";



    @RequiresApi(api = Build.VERSION_CODES.M)
    public Camera(Activity activity) {
        this.activity=activity;
        startCameraThread();
        setupCamera(640,480);
        openCamera();
    }


    private void startCameraThread() {
        mHandlerThread = new HandlerThread("CameraThread");
        mHandlerThread.start();
        mCameraHandler = new Handler(mHandlerThread.getLooper());
    }

    //设置摄像头参数
    private void setupCamera(int width, int height) {
        cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);//获取摄像头信息
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);//获取摄像头朝向
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    continue;
                }
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map != null) {   //  找到屏幕界面显示最适合分辨率
                    PreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);

                }
                setupImageReader();
                SharedPreferences sp =activity.getSharedPreferences(mFileName, activity.MODE_PRIVATE);
                mCameraID = sp.getString("cameraId",null);
                if(mCameraID==null){
                    mCameraID="1";
                }
                break;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private Size getOptimalSize(Size[] outputSizes, int width, int height) {
        List<Size> sizeList = new ArrayList<>();
        for (Size option : outputSizes) {
            if (width > height) {  //横屏
                if (option.getWidth() > width && option.getHeight() > height) {
                    sizeList.add(option);
                }
            } else {  //竖屏
                if (option.getWidth() > width && option.getHeight() > height) {
                    sizeList.add(option);
                }
            }
        }
        if (sizeList.size() > 1) {
            return Collections.min(sizeList, new Comparator<Size>() {
                @Override
                public int compare(Size o1, Size o2) {
                    return Long.signum(o1.getWidth() * o1.getHeight() - o2.getWidth() * o2.getHeight());
                }
            });
        }
        return outputSizes[0];
    }

    private void setupImageReader() {
        imageReader=ImageReader.newInstance(640,480,ImageFormat.JPEG,40);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onImageAvailable(ImageReader reader) {
                final Image image=reader.acquireLatestImage();
                if(image!=null){
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    base64 = Base64.getEncoder().encodeToString(bytes);
                    if(callImage!=null) {
                        callImage.getImage(base64);
                    }
                    image.close();
                }
            }
        },mCameraHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void openCamera() {

        String[] permissions={Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int i=0;
        for (String permission:permissions) {
            if (activity.checkSelfPermission(permission)!= PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(permissions,i++);
            }
        }
        try {
            cameraManager.openCamera(mCameraID, mStateCallback, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }


    CameraDevice.StateCallback mStateCallback=new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {//摄像头打开
            Log.e(TAG, "onOpened:ssss " );
            cameraDevice=camera;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) { //摄像头销毁
            cameraDevice.close();
            cameraDevice=null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) { //摄像头报错
            cameraDevice.close();
            cameraDevice=null;
        }
    };

    private void startPreview() {
        try {
            mCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(imageReader.getSurface());
            int rotaion=activity.getWindowManager().getDefaultDisplay().getRotation();
            Log.e(TAG, "startPreview: "+rotaion );
            mCaptureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION,(Integer) ORIENTATION.get(rotaion));
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            cameraDevice.createCaptureSession(Arrays.asList(imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try {
                        mCaptureRequest=mCaptureRequestBuilder.build();
                        mCameraCaptureSession=session;
                        mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(),null,mCameraHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }




    private void unLockFocus() {
        try {
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(),null,mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void setCallImage(CallImage callImage){
        this.callImage=callImage;
    }

    public interface CallImage{
        void getImage(String base64);
    }

    public void closeCamera() {
        if (null != mCameraCaptureSession) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice= null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader= null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void switchCamera(String id) {
        mCameraID = id;
        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
            if (mCameraID.equals(String.valueOf(CameraCharacteristics.LENS_FACING_BACK)) && characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                backOrientation();

            } else if (mCameraID.equals(String.valueOf(CameraCharacteristics.LENS_FACING_FRONT)) && characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                frontOrientation();
            }
            cameraDevice.close();
            openCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void frontOrientation() {
        //前置时，照片旋转270
        ORIENTATION.append(Surface.ROTATION_0, 270);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 90);
        ORIENTATION.append(Surface.ROTATION_270, 180);
    }

    private static void backOrientation() {
        //后置时，照片旋转90
        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 180);
    }



//
//    void TakePictures(){
//        try {
//            Log.e(TAG, "TakePictures:ssss " );
//            //获取摄像头请求
//            CaptureRequest.Builder cameraBuilder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
//            cameraBuilder.addTarget(imageReader.getSurface());
//            //获取摄像头方向
//            int rotaion=activity.getWindowManager().getDefaultDisplay().getRotation();
//            //设置拍照方向
//            cameraBuilder.set(CaptureRequest.JPEG_ORIENTATION,(Integer) ORIENTATION.get(rotaion));
//            CameraCaptureSession.CaptureCallback captureCallback=new CameraCaptureSession.CaptureCallback() {
//                @Override
//                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
//                    Log.e("TAG", "拍照成功 " );
//                    Toast.makeText(activity,"拍照成功",Toast.LENGTH_LONG);
//                    unLockFocus();
//                    super.onCaptureCompleted(session, request, result);
//
//                }
//            };
//            mCameraCaptureSession.capture(cameraBuilder.build(),captureCallback,mCameraHandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }

}
