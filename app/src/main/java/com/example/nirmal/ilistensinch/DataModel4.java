package com.example.nirmal.ilistensinch;


public class DataModel4 {


    String nick,stat,tit,cat,desc,dt,part;


    public DataModel4(String nick, String stat, String tit, String cat, String desc, String dt,String part) {
        this.nick = nick;
        this.stat =stat;
        this.tit = tit;
        this.cat = cat;
        this.desc = desc;
        this.dt= dt;
        this.part=part;

    }


    public String getNick() {
        return nick;
    }
    public String getStat() {
        return stat;
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

    public String getPart() {
        return part;
    }

}
