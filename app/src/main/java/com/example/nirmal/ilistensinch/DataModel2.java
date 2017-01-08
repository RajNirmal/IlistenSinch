package com.example.nirmal.ilistensinch;


public class DataModel2 {


    String tit,cat,desc,dt;


    public DataModel2( String tit, String cat, String desc, String dt) {

        this.tit = tit;
        this.cat = cat;
        this.desc = desc;
        this.dt= dt;


    }

    public String getDt() {
        return dt;
    }


    public String getTit() {
        return tit;
    }


    public String getCat() {
        return cat;
    }


    public String getDesc() {
        return desc;
    }



}