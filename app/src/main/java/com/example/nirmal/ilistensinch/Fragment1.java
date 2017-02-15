package com.example.nirmal.ilistensinch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.Map;

public class Fragment1 extends Fragment {
    private String MeetingName[],ConCategory[],ConDesc[], Time[],Duration[],Presenter[];
    private int MeetingID[];
    private TextView nothingToShow;
    private static int flag = 0;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel1> data;
    private String uName;
    public final static String TAG = Fragment1.class.getSimpleName();
    private View mRootView;
    private DBHandler db;
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
        getAllMeetingsFromHostingerDB();
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
        }*/
      /* final  Handler mhandler = new Handler();
        mhandler.postDelayed(new Runnable() {
            public void run() {
                mhandler.postDelayed(this, 5000);//now is every 2 minutes
                checkTheAcceptStatus();
            }
        }, 5000); //Every 120000 ms (2 minutes)*/
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
    private void checkTheAcceptStatus(int x){
        int status = db.getMeetingStatus(x);
        Toast.makeText(getActivity(),String.valueOf(status),Toast.LENGTH_SHORT).show();
        if(status == 1){

        }

    }
    private void getDBdata(int id){
        MeetingList myList = db.getMeeting(id);
        Toast.makeText(getActivity(),"Meeting Status = "+myList.getStatus()+" Meeting Name = "+myList.getMeetingName(),Toast.LENGTH_SHORT).show();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        getHostingerData();
    }
    public void showToast(String x){
        Integer MeetId = Integer.parseInt(x);
//        Toast.makeText(getActivity().getApplicationContext(),MeetId.toString(),Toast.LENGTH_SHORT).show();
        getTheMeetingDataFromHostinger(MeetId);
    }

    private long writeinLocalDB(int i){
        long out = db.addMeeting(new MeetingList(MeetingID[i],MeetingName[i],ConDesc[i],Time[i],Duration[i],Time[i],Presenter[i],-1));
        Toast.makeText(getActivity(),String.valueOf(out),Toast.LENGTH_SHORT).show();
//        getDBdata(i);
        return out;
   }

    private void getTheMeetingDataFromHostinger(final int Id){
        final String URL = "http://www.mazelon.com/iListen/ilisten_get_meetings_by_id.php";
        StringRequest stringRequestr = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_SHORT).show();
                final MeetingList myList ;
                String tMeetingName,tConCategory,tConDesc,tTime,tDuration,tPresenter;
                int tMeetingID;
                try{
                    JSONObject obj = new JSONObject(response);
                    JSONArray jsonArray = obj.getJSONArray("result");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    tMeetingName = jsonObject.getString("MeetingName");
                    tConCategory = jsonObject.getString("ConCategory");
                    tConDesc = jsonObject.getString("ConDesc");
                    tTime = "Time : " + jsonObject.getString("Time");
                    tDuration = jsonObject.getString("Duration");
                    tPresenter = jsonObject.getString("PID");
                    tMeetingID = jsonObject.getInt("MeetingID");
                    myList = new MeetingList(tMeetingID,tMeetingName,tConDesc,tTime,tDuration,tTime,tPresenter,1);
                    getDBdata(tMeetingID);
                    long out = db.updateMeetingStatus(myList);
                    Toast.makeText(getActivity(),String.valueOf(out),Toast.LENGTH_SHORT).show();
                    getDBdata(tMeetingID);
                }catch (JSONException e){
                    Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put(SinchHolders.phpMeetingId,String.valueOf(Id));
                return map;
            }
        };
        requestQueue.add(stringRequestr);
    }
    private void getAllMeetingsFromHostingerDB(){
        final String URL = "http://www.mazelon.com/iListen/ilisten_get_all_meetings.php";
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
                        //Nothing has changed since the last time so no need to update the DB just keep the recycler view persistent
                       recyclerView.setVisibility(View.VISIBLE);
                       for (int i =0; i < jArray.length(); i++) {
                           JSONObject jobj = jArray.getJSONObject(i);
                           MeetingName[i] = jobj.getString("MeetingName");
                           ConCategory[i] = jobj.getString("ConCategory");
                           ConDesc[i] = jobj.getString("ConDesc");
                           Time[i] = "Time : " + jobj.getString("Time");
                           Duration[i] = jobj.getString("Duration");
                           Presenter[i] = jobj.getString("PID");
                           MeetingID[i] = jobj.getInt("MeetingID");
                       }
                       for (int i = 0; i < MeetingName.length; i++) {
                           data.add(new DataModel1(Presenter[i], "Active", MeetingName[i], ConCategory[i], ConDesc[i], Time[i],MeetingID[i]));
                       }
                   }else {
//                      New data has been found in the server so call the local Db and update it accordingly
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
                            writeinLocalDB(i);
                            checkTheAcceptStatus(MeetingID[i]);
//                            Toast.makeText(getActivity(),String.valueOf(SuccessorNot),Toast.LENGTH_SHORT).show();
                        }
                        for (int i = 0; i < MeetingName.length; i++) {
                            data.add(new DataModel1(Presenter[i], "Active", MeetingName[i], ConCategory[i], ConDesc[i], Time[i],MeetingID[i]));
                        }

                    }
                    adapter = new CustomAdapter1(data,Fragment1.this);
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

