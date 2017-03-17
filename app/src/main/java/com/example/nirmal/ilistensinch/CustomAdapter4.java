package com.example.nirmal.ilistensinch;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nirmal.ilistensinch.DBPackage.MeetingList;

import java.util.ArrayList;


public class CustomAdapter4 extends RecyclerView.Adapter<CustomAdapter4.MyViewHolder>  {
    private Fragment4 myFrag;
    private ArrayList<MeetingList> dataSet;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nick,stat,tit,cat,desc,dt,join;
        public CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            this.nick = (TextView) itemView.findViewById(R.id.nick);
            this.stat = (TextView) itemView.findViewById(R.id.stat);
            this.tit = (TextView) itemView.findViewById(R.id.tit);
            this.cat = (TextView) itemView.findViewById(R.id.cat);
            this.desc = (TextView) itemView.findViewById(R.id.desc);
            this.dt = (TextView) itemView.findViewById(R.id.dt);
            this.join = (TextView) itemView.findViewById(R.id.joinnowbutton);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
            join.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == join.getId()){
                String x = dataSet.get(getAdapterPosition()).getMeetingName();
                String y = dataSet.get(getAdapterPosition()).getTime();
                String z = dataSet.get(getAdapterPosition()).getDuration();
                myFrag.startMeeting2(x,y,z);
            }
            int position=getAdapterPosition();
            String pos=String.valueOf(position);
        }

    }

    public CustomAdapter4(ArrayList<MeetingList> data, Fragment4 frag) {
        this.dataSet = data;
        this.myFrag = frag;
    }
    public String Category;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardfrag4, parent, false);
        SharedPreferences prefs = parent.getContext().getSharedPreferences(SinchHolders.SharedPrefName, Context.MODE_PRIVATE);
        Category = prefs.getString(SinchHolders.phpUserProfession,"xyz");
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,final int listPosition) {

        TextView nick = holder.nick;
        TextView stat = holder.stat;
        TextView tit = holder.tit;
        TextView cat = holder.cat;
        TextView desc = holder.desc;
        TextView dt= holder.dt;
        TextView join= holder.join;
        nick.setText(dataSet.get(listPosition).getPresenter());
        stat.setText("Scheduled");
        tit.setText(dataSet.get(listPosition).getMeetingName());
        cat.setText(Category);
        desc.setText(dataSet.get(listPosition).getConferenceDesc());
        dt.setText(dataSet.get(listPosition).getTime());
        if((dataSet.get(listPosition).getStatus()) == 2){
            join.setText("Meeting Over");
        }else if((dataSet.get(listPosition).getStatus()) == 3){
            join.setText("Meeting in Progress");
        }
//        join
//        part.setText(dataSet.get(listPosition).getPart());
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
