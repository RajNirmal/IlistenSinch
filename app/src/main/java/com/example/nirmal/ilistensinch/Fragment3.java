package com.example.nirmal.ilistensinch;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sinch.android.rtc.Sinch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Fragment3 extends Fragment {
    private String MeetingName[],ConCategory[],ConDesc[], Time[],Duration[],Presenter[];
    private int MeetingID[];
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel1> data;
    TextView noShow2;
    public final static String TAG = Fragment1.class.getSimpleName();
    private View mRootView;
    public String UserName;

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
        recyclerView.setHasFixedSize(false);
        data = new ArrayList<DataModel1>();
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setVisibility(View.INVISIBLE);
        noShow2.setVisibility(View.VISIBLE);
        getSharedPrefsUserName();
        getMeetingsCreatedByUserFromHostinger();
        return mRootView;
    }
    private void getSharedPrefsUserName(){
        SharedPreferences sp = getActivity().getSharedPreferences(SinchHolders.SharedPrefName, Context.MODE_PRIVATE);
        UserName = sp.getString(SinchHolders.phpUserName,"xyz");
    }
    private void getMeetingsCreatedByUserFromHostinger(){
        final String URL = "http://www.mazelon.com/iListen/ilisten_get_meetings_by_user.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_SHORT).show();
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
                    for(int i=0;i<jArray.length();i++){
                        JSONObject jobj = jArray.getJSONObject(i);
                        MeetingName[i] = jobj.getString("MeetingName");
                        ConCategory[i] = jobj.getString("ConCategory");
                        ConDesc[i] = jobj.getString("ConDesc");
                        Time[i] = "Time = " + jobj.getString("Time");
                        Duration[i] = jobj.getString("Duration");
                        Presenter[i] = jobj.getString("PID");
                        MeetingID[i] = jobj.getInt("MeetingID");

                    }
                    for(int i=0 ; i < MeetingName.length; i++) {
                        data.add(new DataModel1(Presenter[i], "Active", MeetingName[i], ConCategory[i], ConDesc[i], Time[i], MeetingID[i]));
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter = new CustomAdapter3(data);
                    recyclerView.setAdapter(adapter);
                    noShow2.setVisibility(View.INVISIBLE);
                }catch (JSONException e){
                    Toast.makeText(getActivity(),"Try again after some time",Toast.LENGTH_SHORT).show();
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
                map.put(SinchHolders.phpUserName,UserName);
                return map;
            }
        };
        RequestQueue rs = Volley.newRequestQueue(getActivity());
        rs.add(stringRequest);
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

