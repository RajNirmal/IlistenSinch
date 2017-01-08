package com.example.nirmal.ilistensinch;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomAdapter2 extends RecyclerView.Adapter<CustomAdapter2.MyViewHolder>  {

    private ArrayList<DataModel2> dataSet;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tit,cat,desc,dt;
        public CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tit = (TextView) itemView.findViewById(R.id.title);
            this.cat = (TextView) itemView.findViewById(R.id.cat);
            this.desc = (TextView) itemView.findViewById(R.id.desc);
            this.dt = (TextView) itemView.findViewById(R.id.dt);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int position=getAdapterPosition();
            String pos=String.valueOf(position);
         /*  Intent intent = new Intent(v.getContext(),Review.class);
            intent.putExtra("pos",pos);
            v.getContext().startActivity(intent);
*/
        }

    }

    public CustomAdapter2(ArrayList<DataModel2> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardfrag2, parent, false);


        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,final int listPosition) {

        TextView tit = holder.tit;
        TextView cat = holder.cat;
        TextView desc = holder.desc;
        TextView dt= holder.dt;

        tit.setText("");
        cat.setText("");
        desc.setText("");
        dt.setText("");
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
