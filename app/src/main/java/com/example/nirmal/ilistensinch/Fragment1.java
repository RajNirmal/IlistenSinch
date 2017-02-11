package com.example.nirmal.ilistensinch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nirmal.ilistensinch.DBPackage.DBHandler;
import com.example.nirmal.ilistensinch.DBPackage.MeetingList;
import com.rebtel.repackaged.com.google.gson.JsonObject;
import com.sinch.android.rtc.Sinch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class Fragment1 extends Fragment {
    String MeetingName[],ConCategory[],ConDesc[], Time[],Duration[],Presenter[];
    int MeetingID[];
    TextView nothingToShow;
    static int flag = 0;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel1> data;
    String uName;
    public final static String TAG = Fragment1.class.getSimpleName();
    private View mRootView;
    DBHandler db;
    private final static int INTERVAL = 1000 * 60 * 2; //2 minutes
    RequestQueue requestQueue;
    public Fragment1() {
        // TODO Auto-generated constructor stub
    }

    public static Fragment1 newInstance() {
        return new Fragment1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getHostingerData();
        mRootView = inflater.inflate(R.layout.listfrag1, container, false);
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.my_recycler_view);
        nothingToShow = (TextView)mRootView.findViewById(R.id.nothingtoshow);
        recyclerView.setHasFixedSize(false);
        db = new DBHandler(getActivity());
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        data = new ArrayList<DataModel1>();
     /*   for (int i = 0; i < MyData.nick.length; i++) {
            data.add(new DataModel1(MyData.nick[i], MyData.stat[i], MyData.tit[i], MyData.cat[i], MyData.desc[i], MyData.dt[i]));
        }
       final  Handler mhandler = new Handler();
        mhandler.postDelayed(new Runnable() {
            public void run() {
                getHostingerData();
                mhandler.postDelayed(this, 120000);//now is every 2 minutes

            }
        }, 120000); //Every 120000 ms (2 minutes)*/
        try{
            SharedPreferences sp = getActivity().getSharedPreferences(SinchHolders.SharedPrefName, Context.MODE_PRIVATE);
            uName = sp.getString(SinchHolders.phpUserName,"-1");
        }catch (NullPointerException e){
            Toast.makeText(getActivity().getApplicationContext(),"User data not found",Toast.LENGTH_SHORT).show();
            uName = "UserName";
        }
        flag =1;
        return mRootView;
    }
    private void writeinLocalDB(int i){
        Integer x = MeetingName.length;
//        Toast.makeText(getActivity(),x.toString(),Toast.LENGTH_SHORT).show();
//        for (int i=0; i< MeetingName.length;i++){
          db.addMeeting(new MeetingList(MeetingID[i],MeetingName[i],ConDesc[i],Time[i],Duration[i],Time[i],Presenter[i]));
   }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        getHostingerData();
    }

    private void getHostingerData(){
        final String URL = "http://www.mazelon.com/iListen/ilisten_get_all_users.php";
        StringRequest sr =new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray jArray = obj.getJSONArray("result");
                    MeetingName = new String[jArray.length()];
                    ConCategory = new String[jArray.length()];
                    ConDesc = new String[jArray.length()];
                    Time = new String[jArray.length()];
                    Duration = new String[jArray.length()];
                    Presenter = new String[jArray.length()];
                    MeetingID = new int[jArray.length()];
                   if(jArray.length()==0){
                       Toast.makeText(getActivity().getApplicationContext(),"Length is 0",Toast.LENGTH_SHORT).show();
                        nothingToShow.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }else if(jArray.length() == SinchHolders.lastDataCount){
                        //Nothing has changed since the last time
//                       Toast.makeText(getActivity().getApplicationContext(),"Nothing has changed",Toast.LENGTH_SHORT).show();
                       recyclerView.setVisibility(View.VISIBLE);
                       for (int i =0; i < jArray.length(); i++) {
                           JSONObject jobj = jArray.getJSONObject(i);
                           MeetingName[i] = jobj.getString("MeetingName");
                           ConCategory[i] = jobj.getString("ConCategory");
                           ConDesc[i] = jobj.getString("ConDesc");
                           Time[i] = "Time = " + jobj.getString("Time");
                           Duration[i] = jobj.getString("Duration");
                           Presenter[i] = jobj.getString("PID");
                           MeetingID[i] = jobj.getInt("MeetingID");
//                           writeinLocalDB(i);
                       }
                       for (int i = 0; i < MeetingName.length; i++) {
                           data.add(new DataModel1(Presenter[i], "Active", MeetingName[i], ConCategory[i], ConDesc[i], Time[i],MeetingID[i]));
                       }
                   }else {
//                       Toast.makeText(getActivity().getApplicationContext(),"New data received",Toast.LENGTH_SHORT).show();
                        recyclerView.setVisibility(View.VISIBLE);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jobj = jArray.getJSONObject(i);
                            MeetingName[i] = jobj.getString("MeetingName");
                            ConCategory[i] = jobj.getString("ConCategory");
                            ConDesc[i] = jobj.getString("ConDesc");
                            Time[i] = "Time = " + jobj.getString("Time");
                            Duration[i] = jobj.getString("Duration");
                            Presenter[i] = jobj.getString("PID");
                            MeetingID[i] = jobj.getInt("MeetingID");
//                            writeinLocalDB(i);
                        }
                        for (int i = 0; i < MeetingName.length; i++) {
                            data.add(new DataModel1(Presenter[i], "Active", MeetingName[i], ConCategory[i], ConDesc[i], Time[i],MeetingID[i]));
                        }

                    }
                    adapter = new CustomAdapter1(data);
                    recyclerView.setAdapter(adapter);
                    SinchHolders.lastDataCount = jArray.length();
              //      Toast.makeText(getActivity().getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                }catch (JSONException e){
                    Toast.makeText(getActivity().getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(sr);
    }


}

