package com.example.smartplayer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class CameraSettingFragment extends Fragment {

    private EditText CameraIP;
    private EditText CameraPort;
    private EditText CameraUser;
    private EditText CameraPassword;
    private EditText CameraAgreement;
    private EditText CameraURL;
    private Button camerabtn;
    Gestures gestures;
    Gestures newgestures;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private static final  String mFileName="mydata";

    private View view;

    public CameraSettingFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null){
            view=inflater.inflate(R.layout.fragment_camera_setting,container,false);
        }
        newgestures=getObject("gestures");
        if(newgestures!=null){
            gestures=newgestures;
        }else {
            gestures=new Gestures();
        }
        intdata(view);
        return  view;
    }
    void intdata(View view){
        CameraIP=view.findViewById(R.id.cameraip);
        CameraPort=view.findViewById(R.id.cameraport);
        CameraUser=view.findViewById(R.id.camerauser);
        CameraPassword=view.findViewById(R.id.camerapassword);
        CameraAgreement=view.findViewById(R.id.cameraagreement);
        CameraURL=view.findViewById(R.id.cameraurl);
        camerabtn=view.findViewById(R.id.camerabtn);
        CameraIP.setText(gestures.getCameraip());
        CameraPort.setText(gestures.getCameraport());
        CameraUser.setText(gestures.getCamerauser());
        CameraPassword.setText(gestures.getCamerapassword());
        CameraAgreement.setText(gestures.getCameraAgreement());
        CameraURL.setText(gestures.getCameraURL());
        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestures.setCameraip(CameraIP.getText().toString());
                gestures.setCameraport(CameraPort.getText().toString());
                gestures.setCamerauser(CameraUser.getText().toString());
                gestures.setCamerapassword(CameraUser.getText().toString());
                gestures.setCameraAgreement(CameraAgreement.getText().toString());
                gestures.setCameraURL(CameraURL.getText().toString());
                Shared("gestures", gestures);
            }
        });

    }
    public void Shared(String key,Object object){
        sp=getContext().getSharedPreferences(mFileName, getContext().MODE_PRIVATE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        editor=sp.edit();
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectValue = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            editor = sp.edit();
            editor.putString(key, objectValue);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }

                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public <T> T getObject(String key) {
        SharedPreferences sp =getContext().getSharedPreferences(mFileName, getContext().MODE_PRIVATE);
        if (sp.contains(key)) {
            String objectValue = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectValue, Base64.DEFAULT);
            //一样通过读取字节流，创建字节流输入流，写入对象并作强制转换
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

}