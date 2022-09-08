package com.example.smartplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;


public class SettingFragment extends Fragment {
    ListView listView;
    int[] imglist;
    String[] textlist;
    Setting_list.SettingClick SettingClick;
    private View root;


    public SettingFragment(){

    }

    public SettingFragment(MainActivity myClick, int[] imglist, String[] textlist) {
        this.SettingClick=myClick;
        this.imglist=imglist;
        this.textlist=textlist;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         if(root==null){
             root=inflater.inflate(R.layout.fragment_setting, container, false);
         }
         listView=root.findViewById(R.id.settinglist);
         listView.setAdapter(new Setting_list(getContext(),imglist,textlist,SettingClick));
         return root;
    }
}