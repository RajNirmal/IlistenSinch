package com.example.nirmal.ilistensinch;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nirmal.ilistensinch.DBPackage.DBHandler;
import com.example.nirmal.ilistensinch.DBPackage.MeetingList;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class CustomAdapter1 extends RecyclerView.Adapter<CustomAdapter1.MyViewHolder>  {
    private Fragment1 myFrag;
    private ArrayList<DataModel1> dataSet;
    DBHandler db;
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView butAccept,butReject;
        TextView nick,stat,tit,cat,desc,dt;
        public CardView cardView;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.nick = (TextView) itemView.findViewById(R.id.nick);
            this.stat = (TextView) itemView.findViewById(R.id.stat);
            this.tit = (TextView) itemView.findViewById(R.id.tit);
            this.cat = (TextView) itemView.findViewById(R.id.cat);
            this.desc = (TextView) itemView.findViewById(R.id.desc);
            this.dt = (TextView) itemView.findViewById(R.id.dt);
            this.butAccept = (TextView)itemView.findViewById(R.id.accept_textview);
            this.butReject = (TextView)itemView.findViewById(R.id.reject_textview);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);
            butAccept.setOnClickListener(this);
            butReject.setOnClickListener(this);
            db = new DBHandler(itemView.getContext());
        }

        @Override
        public void onClick(View v) {
            if( v.getId() == butAccept.getId()){
//                Toast.makeText(v.getContext(),"Meeting ID = "+ dataSet.get(getAdapterPosition()).getMeetId()+" Meeting Name = "+dataSet.get(getAdapterPosition()).getTit(),Toast.LENGTH_SHORT).show();
                myFrag.showToast(dataSet.get(getAdapterPosition()).getTit());

            }else if (v.getId() == butReject.getId()){
                Toast.makeText(v.getContext(),"Button reject clicked on position"+ String.valueOf(getAdapterPosition()),Toast.LENGTH_SHORT).show();
            }

            /*int position=getAdapterPosition();
            String pos=String.valueOf(position);
            Intent intent = new Intent(v.getContext(),Review.class);
            intent.putExtra("pos",pos);
            v.getContext().startActivity(intent);
*/
        }

    }
    private void writeinLocalDB(int i){
        /*Integer x = MeetingName.length;
        Toast.makeText(getActivity(),x.toString(),Toast.LENGTH_SHORT).show();
        for (int i=0; i< MeetingName.length;i++){*/
//        db.addMeeting(new MeetingList(MeetingID[i],MeetingName[i],ConDesc[i],Time[i],Duration[i],Time[i],Presenter[i]));
    }
    public CustomAdapter1(ArrayList<DataModel1> data,Fragment1 frag) {
        this.dataSet = data;
        myFrag = frag;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardfrag1, parent, false);
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
        nick.setText(dataSet.get(listPosition).getNick());
        stat.setText(dataSet.get(listPosition).getStat());
        tit.setText(dataSet.get(listPosition).getTit());
        cat.setText(dataSet.get(listPosition).getCat());
        desc.setText(dataSet.get(listPosition).getDesc());
        dt.setText(dataSet.get(listPosition).getDt());
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
