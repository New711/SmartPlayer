package com.example.smartplayer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

public class SongListAdapter extends BaseAdapter {
    private List<Songlist> songlists;
    private Context context;

    public SongListAdapter(List<Songlist> songlists, Context context) {
        this.songlists = songlists;
        this.context = context;
    }

    public List<Songlist> getSonglists() {
        return songlists;
    }

    public void setSonglists(List<Songlist> songlists) {
        this.songlists = songlists;
    }

    @Override
    public int getCount() {
        return songlists.size();
    }

    @Override
    public Object getItem(int position) {
        return songlists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null) {
            viewHolder=new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.songlist_adapter,parent,false);
            viewHolder.song=convertView.findViewById(R.id.songtext);
            viewHolder.singer=convertView.findViewById(R.id.singertext);
            viewHolder.songtime=convertView.findViewById(R.id.songtime);
            viewHolder.songadapter=convertView.findViewById(R.id.songadapter);
            convertView.setTag(viewHolder);
        }else{
           viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.song.setText(songlists.get(position).getSongname());
        viewHolder.singer.setText(songlists.get(position).getSinger());
        viewHolder.songtime.setText(songlists.get(position).getDuration());
        return convertView;
    }
    public class ViewHolder{
        TextView song;
        TextView singer;
        TextView songtime;
        LinearLayout songadapter;
    }
}
