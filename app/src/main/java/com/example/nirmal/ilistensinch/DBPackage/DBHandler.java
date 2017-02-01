package com.example.nirmal.ilistensinch.DBPackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nirmal on 29/1/17.
 */

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "iListen";
    private static final String TABLE_NAME = "MeetingDetails";
    private static final String KEY_MEETING_NAME = "MeetingName";
    private static final String KEY_CONFERENCE_DESC = "ConferenceDescription";
    private static final String KEY_TIME = "MeetingTime";
    private static final String KEY_DURATION = "MeetingDuration";
    private static final String KEY_CREATE_TIME = "CreationTime";
    private static final String KEY_PRESENTER = "MeetingPresenter";
    private static final String KEY_MEETING_KEY= "MeetingId";
    public DBHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEETING_TABLE = "CREATE TABLE "   + TABLE_NAME + "("
                + KEY_MEETING_KEY + " INTEGER PRIMARY KEY," + KEY_MEETING_NAME+ " TEXT" +KEY_CONFERENCE_DESC+ " TEXT"+ KEY_TIME+ " TEXT"+KEY_DURATION+ " TEXT"+KEY_CREATE_TIME+"TEXT"+
                KEY_PRESENTER+"TEXT"+")";
        db.execSQL(CREATE_MEETING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    public void addMeeting(MeetingList meetingList){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_MEETING_KEY, meetingList.getId());
        cValues.put(KEY_MEETING_NAME, meetingList.getMeetingName());
        cValues.put(KEY_CONFERENCE_DESC,meetingList.getConferenceDesc());
        cValues.put(KEY_TIME,meetingList.getTime());
        cValues.put(KEY_DURATION, meetingList.getDuration());
        cValues.put(KEY_CREATE_TIME, meetingList.getCreateTime());
        cValues.put(KEY_PRESENTER, meetingList.getPresenter());
        db.insert(DATABASE_NAME,null,cValues);
        db.close();
    }
    public MeetingList getMeeting(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.query(TABLE_NAME,new String[]{KEY_MEETING_KEY,KEY_MEETING_NAME, KEY_CONFERENCE_DESC,KEY_TIME,KEY_DURATION,KEY_CREATE_TIME,KEY_PRESENTER},KEY_MEETING_KEY+"=?",new String[]
                {String.valueOf(id)},null,null,null,null);
        if(cr != null)
            cr.moveToFirst();
        MeetingList list = new MeetingList(Integer.parseInt(cr.getString(0)),cr.getString(1),cr.getString(2),cr.getString(3),cr.getString(4),cr.getString(5),cr.getString(6));
        return list;
    }
}

