package com.example.nirmal.ilistensinch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Fragment3 extends Fragment {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel3> data;
    TextView noShow2;
    public final static String TAG = Fragment1.class.getSimpleName();
    private View mRootView;


    public Fragment3() {
        // TODO Auto-generated constructor stub
    }

    public static Fragment1 newInstance() {
        return new Fragment1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.listfrag3, container, false);
        noShow2 = (TextView) mRootView.findViewById(R.id.nothingtoshowfrag3);
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setVisibility(View.INVISIBLE);
        noShow2.setVisibility(View.VISIBLE);
        return mRootView;
    }

}
        /*data = new ArrayList<DataModel3>();
        Toast.makeText(getActivity(),MyData.nick.length,Toast.LENGTH_SHORT).show();
        if(MyData.nick.length == 0){
        else {
            for (int i = 0; i < MyData.nick.length; i++) {
                data.add(new DataModel3(MyData.nick[i], MyData.stat[i], MyData.tit[i], MyData.cat[i], MyData.desc[i], MyData.dt[i]));
            }

        adapter = new CustomAdapter3(data);
        recyclerView.setAdapter(adapter);
        */

