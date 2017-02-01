package com.example.nirmal.ilistensinch.DBPackage;

/**
 * Created by nirmal on 29/1/17.
 */

public class MeetingList {
    int id;

    public String getMeetingName() {
        return MeetingName;
    }

    public void setMeetingName(String meetingName) {
        MeetingName = meetingName;
    }

    public String getConferenceDesc() {
        return ConferenceDesc;
    }

    public void setConferenceDesc(String conferenceDesc) {
        ConferenceDesc = conferenceDesc;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getPresenter() {
        return Presenter;
    }

    public void setPresenter(String presenter) {
        Presenter = presenter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    String MeetingName,ConferenceDesc;
    String Time,Duration,CreateTime;
    String Presenter;
     public MeetingList(){

     }
    public MeetingList(int mId,String mName, String cDesc,String tTime, String mDuration,String cTime, String pPresenter){
        id = mId;
        MeetingName = mName;
        ConferenceDesc = cDesc;
        Time = tTime;
        Duration = mDuration;
        CreateTime = cTime;
        Presenter = pPresenter;
    }

}
