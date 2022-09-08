package com.example.smartplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.tv.TvContract;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;


public class LanguageFragment extends Fragment {

    private View view;
    private TextView chinese;
    private TextView english;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private static final  String mFileName="mydata";

    public LanguageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null){
            view=inflater.inflate(R.layout.fragment_language, container, false);
        }
        sp=getContext().getSharedPreferences(mFileName, Context.MODE_PRIVATE);
        editor=sp.edit();
        chinese=view.findViewById(R.id.chinese);
        english=view.findViewById(R.id.english);
        chinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale myLocale=new Locale(Locale.CHINESE.getLanguage());
                editor.putString("language","chinese").commit();
                Resources resources=getResources();
                DisplayMetrics dm=resources.getDisplayMetrics();
                Configuration configuration = resources.getConfiguration();
                configuration.locale=myLocale;
                resources.updateConfiguration(configuration,dm);
                getActivity().recreate();
            }
        });
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale myLocale=new Locale(Locale.ENGLISH.getLanguage());
                editor.putString("language","english").commit();
                Resources resources=getResources();
                DisplayMetrics dm=resources.getDisplayMetrics();
                Configuration configuration = resources.getConfiguration();
                configuration.locale=myLocale;
                resources.updateConfiguration(configuration,dm);
                getActivity().recreate();
            }
        });


        return view;
    }
}