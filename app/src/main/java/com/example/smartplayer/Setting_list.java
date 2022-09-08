package com.example.smartplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Setting_list extends BaseAdapter implements View.OnClickListener {
    Context context;
    ImageView settingimg;
    TextView settingtext;
    int[] imglist;
    String[] textlist;
    LinearLayout settinglayout;
    ImageView setting_off;
    SettingClick myClick;

    @Override
    public void onClick(View view) {
        myClick.settingcilck(view);
    }

    public interface SettingClick{
        public void settingcilck(View view);
    }

    public Setting_list(Context context, int[] imglist, String[] textlist, SettingClick Click) {
        this.context = context;
        this.imglist=imglist;
        this.textlist=textlist;
        this.myClick=Click;
    }

    @Override
    public int getCount() {
        if(textlist!=null) {
            return textlist.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return textlist[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.setting_list_mian, viewGroup, false);
        settingimg=view.findViewById(R.id.settingimg);
        settingtext=view.findViewById(R.id.settingtext);
        settinglayout=view.findViewById(R.id.settinglayout);
        setting_off=view.findViewById(R.id.setting_off);
        settingimg.setImageResource(imglist[i]);
        settingtext.setText(textlist[i]);
        setting_off.setOnClickListener(this);
        Layoutbackground(i);
        ViewHolder holder=new ViewHolder();
        holder.imageView=setting_off;

        setting_off.setTag(i);
        view.setTag(holder);
        return view;
    }
    public  class   ViewHolder{
        public ImageView imageView;
    }
    void Layoutbackground(int i){
        if(textlist.length<=1){
            settinglayout.setBackgroundResource(R.drawable.list_border);
        }else{
            if(i==0){
                settinglayout.setBackgroundResource(R.drawable.list_bordertop);
            }else if (i==textlist.length-1){
                settinglayout.setBackgroundResource(R.drawable.list_borderbotton);
            }
        }
    }
}