package com.example.smartplayer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
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

public class ServerSettingFragment extends Fragment {

    private EditText serveripedit;
    private EditText serverportedit;
    Button serverbtn;
    Gestures gestures;
    Gestures newgestures;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private static final  String mFileName="mydata";
    private View view;

    public ServerSettingFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null) {
            view=inflater.inflate(R.layout.fragment_server_setting, container, false);
        }
        newgestures=getObject("gestures");
        if(newgestures!=null){
            gestures=newgestures;
        }else {
            gestures=new Gestures();
        }
        intdata(view);
        return view;
    }
    void intdata(View view){
        serveripedit=view.findViewById(R.id.serverip);
        serverportedit=view.findViewById(R.id.serverport);
        serverbtn=view.findViewById(R.id.serverbtn);
        serveripedit.setText(gestures.getServerip());
        serverportedit.setText(gestures.getServerport());
        serverbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestures.setServerip(serveripedit.getText().toString());
                gestures.setServerport(serverportedit.getText().toString());
                Shared("gestures",gestures);
            }
        });
    }

    public void Shared(String key,Object object){
        sp=getContext().getSharedPreferences(mFileName, getContext().MODE_PRIVATE);
        //创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //创建字节对象输出流
        ObjectOutputStream out = null;
        editor=sp.edit();
        try {
            //然后通过将字对象进行64转码，写入sp中
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