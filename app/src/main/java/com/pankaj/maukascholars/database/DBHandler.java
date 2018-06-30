package com.pankaj.maukascholars.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pankaj.maukascholars.util.EventDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pankaj on 23/7/16.
 */
public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "mauka";
    private static final String TABLE_EVENTS = "events";

    private static final String KEY_ID = "id";
    private static final String STARRED = "starred";
    private static final String SAVED = "saved";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String PUBLISHER_NAME = "name";
    private static final String DEADLINE = "deadline";
    private static final String IMAGE = "image";
    private static final String ICON = "icon";
    private static final String LINK = "link";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_EVENT = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + "("
                + KEY_ID + " INTEGER NOT NULL," + STARRED+ " INTEGER NOT NULL," + SAVED + " INTEGER NOT NULL,"
                + TITLE + " TEXT NOT NULL," + DESCRIPTION + " TEXT NOT NULL," + PUBLISHER_NAME + " TEXT, "
                + DEADLINE + " TEXT," + IMAGE + " TEXT," + ICON + " TEXT," + LINK + " TEXT NOT NULL, UNIQUE(" + KEY_ID + ")" + ")";

        db.execSQL(CREATE_TABLE_EVENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public void addEvent(EventDetails table){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, table.getId());
        values.put(STARRED, table.getStarred());
        values.put(SAVED, table.getSaved());
        values.put(TITLE, table.getTitle());
        values.put(DESCRIPTION, table.getDescription());
        values.put(PUBLISHER_NAME, table.getName());
        values.put(DEADLINE, table.getDeadline());
        values.put(IMAGE, table.getImage());
        values.put(ICON, table.getIcon());
        values.put(LINK, table.getLink());
        db.insertWithOnConflict(TABLE_EVENTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public EventDetails getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String getQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + KEY_ID + " = ?";
        Cursor cursor = db.rawQuery(getQuery, new String[]{String.valueOf(id)});

        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            EventDetails eventDetails =  new EventDetails(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
            cursor.close();
            return eventDetails;
        }else{
            return null;
        }
    }

    public void deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<EventDetails> getAllStarredEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        String getQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + STARRED + " = ?";
        Cursor cursor = db.rawQuery(getQuery, new String[]{String.valueOf(1)});
        List<EventDetails> eventTables = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                eventTables.add(new EventDetails(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9)));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return eventTables;
    }

    public List<EventDetails> getAllSavedEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        String getQuery = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + SAVED + " = ?";
        Cursor cursor = db.rawQuery(getQuery, new String[]{String.valueOf(1)});
        List<EventDetails> eventTables = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                eventTables.add(new EventDetails(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9)));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return eventTables;
    }
    public List<EventDetails> getSearched(String search){
        //have written a sql query but change it to php query, I am not able to do that
        SQLiteDatabase db =this.getReadableDatabase();
        search = search.toUpperCase();
        String getQuery = "SELECT * FROM " + TABLE_EVENTS + " TITLE LIKE " + "%"+search+"%"+" OR DESCRIPTION LIKE "+ "%"+search+"%" +
                " OR PUBLISHER_NAME LIKE "+ "%"+search+"%";
        Cursor cursor = db.rawQuery(getQuery, new String[]{String.valueOf(1)});
        List<EventDetails> eventTables = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                eventTables.add(new EventDetails(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9)));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return eventTables;

    }
}