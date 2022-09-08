package com.example.smartplayer;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Setting_list.SettingClick, View.OnClickListener {
    LinearLayout banner;
    LinearLayout maincar;
    LinearLayout mainplay;
    LinearLayout mainsetting;
    LinearLayout fl;
    TextView maincartext;
    TextView mainplaytext;
    TextView mainsettingtext;
    ImageView bgimage;
    private ImageView maincarimg;
    private ImageView mainplayimg;
    private ImageView mainsettingimg;
    int[] settingimglist=new int[]{R.drawable.server,R.drawable.camera_setting,R.drawable.camera_management,R.drawable.help,R.drawable.language};
    String[] settingtextlist;
    Handler handler;
    Runnable runnable;
    SharedPreferences sp;
    private static final  String mFileName="mydata";
    private FragmentTransaction fragmentT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLanguage();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       indata();
        settingtextlist=new String[]{this.getString(R.string.Server),this.getString(R.string.CameraSettings),this.getString(R.string.CameraManagement),this.getString(R.string.Help), this.getString(R.string.Language)};

    }
    void indata(){
        maincar=findViewById(R.id.maincar);
        mainplay=findViewById(R.id.mainplay);
        mainsetting=findViewById(R.id.mainsetting);
        maincartext=findViewById(R.id.mytext);
        mainplaytext=findViewById(R.id.mainplaytext);
        mainsettingtext=findViewById(R.id.mainsettingtext);
        maincarimg=findViewById(R.id.myimg);
        mainplayimg=findViewById(R.id.mainplayimg);
        mainsettingimg=findViewById(R.id.mainsettingimg);
        fl=findViewById(R.id.fl);
        banner=findViewById(R.id.banner);
        bgimage=findViewById(R.id.bgimage);
//        mainfold=findViewById(R.id.mainfold);
        maincar.setOnClickListener(this);
        mainplay.setOnClickListener(this);
        mainsetting.setOnClickListener(this);
//        mainfold.setOnClickListener(this);
        mainplay.setBackgroundColor(getResources().getColor(R.color.navbg));
        mainplayimg.setImageResource(R.drawable.playclick);
        mainplaytext.setTextColor(getResources().getColor(R.color.white));
        handler=new Handler();
        runnable=new Runnable() {
            int i=0;
            @Override
            public void run() {
                if(i>9){
                    i=0;
                }
                switch (i){
                    case 3:bgimage.setImageResource(R.drawable.car2);break;
                    case 6:bgimage.setImageResource(R.drawable.car3);break;
                    case 9:bgimage.setImageResource(R.drawable.car1);break;
                    default:break;
                }
                i++;
                handler.postDelayed(this,1000);
            }
        };
        handler.postDelayed(runnable, 1000);
        replace(new PlayFragment());
    }

    void setLanguage(){
        sp=getSharedPreferences(mFileName,MODE_PRIVATE);
        String language=sp.getString("language",null);
        Log.e("TAG", "setLanguage: "+language );
        if(language!=null){
            if(language.equals("chinese")){
            Locale myLocale=new Locale(Locale.CHINESE.getLanguage());
            Resources resources=getResources();
            DisplayMetrics dm=resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.locale=myLocale;
            resources.updateConfiguration(configuration,dm);
            }else if(language.equals("english")){
            Locale myLocale=new Locale(Locale.ENGLISH.getLanguage());
            Resources resources=getResources();
            DisplayMetrics dm=resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.locale=myLocale;
            resources.updateConfiguration(configuration,dm);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.mainfold:
//                if(mainfold.isSelected()) {
//                    mainfold.setSelected(false);
//                    handler.postDelayed(runnable, 1000);
//                    banner.setVisibility(View.VISIBLE);
//                }else{
//                    mainfold.setSelected(true);
//                    handler.removeCallbacks(runnable);
//                    banner.setVisibility(View.GONE);
//                }
//                break;
            case R.id.maincar:
                maincar.setBackgroundResource(R.drawable.nav_left_click);
                mainplay.setBackgroundColor(getResources().getColor(R.color.navbg));
                mainsetting.setBackgroundResource(R.drawable.nav_right);
                maincartext.setTextColor(getResources().getColor(R.color.white));
                mainplaytext.setTextColor(getResources().getColor(R.color.gray));
                mainsettingtext.setTextColor(getResources().getColor(R.color.gray));
                maincarimg.setImageResource(R.drawable.myclick);
                mainplayimg.setImageResource(R.drawable.play);
                mainsettingimg.setImageResource(R.drawable.setting);
                replace(new PersonalCenterFragment());
                break;
            case R.id.mainplay:
                maincar.setBackgroundResource(R.drawable.nav_left);
                mainplay.setBackgroundColor(getResources().getColor(R.color.navtrue));
                mainsetting.setBackgroundResource(R.drawable.nav_right);
                maincartext.setTextColor(getResources().getColor(R.color.gray));
                mainplaytext.setTextColor(getResources().getColor(R.color.white));
                mainsettingtext.setTextColor(getResources().getColor(R.color.gray));
                maincarimg.setImageResource(R.drawable.my);
                mainplayimg.setImageResource(R.drawable.playclick);
                mainsettingimg.setImageResource(R.drawable.setting);
                replace(new PlayFragment());

                break;
            case R.id.mainsetting:
                maincar.setBackgroundResource(R.drawable.nav_left);
                mainplay.setBackgroundColor(getResources().getColor(R.color.navbg));
                mainsetting.setBackgroundResource(R.drawable.nav_right_click);
                maincartext.setTextColor(getResources().getColor(R.color.gray));
                mainplaytext.setTextColor(getResources().getColor(R.color.gray));
                mainsettingtext.setTextColor(getResources().getColor(R.color.white));
                maincarimg.setImageResource(R.drawable.my);
                mainplayimg.setImageResource(R.drawable.play);
                mainsettingimg.setImageResource(R.drawable.settingclick);
                replace(new SettingFragment(this,settingimglist,settingtextlist));
                break;
            default:break;
        }
    }
    public void replace(Fragment fragment){
        FragmentManager fm=getSupportFragmentManager();
        fragmentT=fm.beginTransaction();
        fragmentT.replace(R.id.fl,fragment);
        fragmentT.addToBackStack(null);
        fragmentT.commit();

    }


    @Override
    public void settingcilck(View view) {
        switch ((Integer)view.getTag()){
            case 0:replace(new ServerSettingFragment());break;
            case 1:replace(new CameraSettingFragment());break;
            case 2:replace(new CameraManagementFragment());break;
            case 3:replace(new HelpFragment());break;
            case 4:replace(new LanguageFragment());break;
            default:break;
        }
    }



}