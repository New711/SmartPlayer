package com.example.smartplayer;

import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class CameraManagementFragment extends Fragment implements View.OnClickListener {

    private View view;
    private int cameraId;
    private TextView cameraBack;
    private TextView cameraFront;
    private TextView cameraExternal;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private static final  String mFileName="mydata";

    public CameraManagementFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null) {
            view = inflater.inflate(R.layout.fragment_camera_management, container, false);
        }
        sp=getContext().getSharedPreferences(mFileName, getContext().MODE_PRIVATE);
        cameraBack=view.findViewById(R.id.cameraBack);
        cameraFront=view.findViewById(R.id.cameraFront);
        cameraExternal=view.findViewById(R.id.cameraExternal);
        cameraBack.setOnClickListener(this);
        cameraFront.setOnClickListener(this);
        cameraExternal.setOnClickListener(this);
        int numberOfCameras = Camera.getNumberOfCameras();
        if(numberOfCameras==2){
            cameraExternal.setVisibility(View.GONE);
        }else if(numberOfCameras==1){
            cameraExternal.setVisibility(View.GONE);
            cameraBack.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cameraBack:
                Shared("cameraId",0+"");
                break;
            case R.id.cameraFront:
                Shared("cameraId",1+"");
                break;
            case R.id.cameraExternal:
                Shared("cameraId",2+"");
                break;
            default:
                break;
        }
    }
    public void Shared(String key,String Id){
        Toast.makeText(getContext(),Id,Toast.LENGTH_LONG).show();
            editor = sp.edit();
            editor.putString(key, Id);
            editor.commit();
    }
}