package com.pankaj.maukascholars.util.alarm.databasehandling;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.GregorianCalendar;

/**
 * Created by user on 16-02-2016.
 */
public class DBManipulation extends DatabaseAdapter {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MeetingSchedule";
    private static final String TABLE_NAME = "ScheduleTable";
    public static final String TITLE="TITLE";
    public static final String TIMESCHEDULED="TIMESCHEDULED";
    public static final String ID = "_id";

    public static final String CREATE_TABLE = "create table if not exists "
            + TABLE_NAME
            + " ( _id integer primary key autoincrement,"+TITLE+" text NOT NULL,"+TIMESCHEDULED+" long NOT NULL);";

    public  SQLiteDatabase db;
    Context c;

    public DBManipulation(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        c=context;
    }

    public  DBManipulation open() throws SQLException
    {
        db = this.getWritableDatabase();
        return this;
    }

    public void close()
    {
        db.close();
    }

    public  SQLiteDatabase getDatabaseInstance()
    {
        return db;
    }

    public void insertEntry(String title, long timescheduled )
    {
        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put(TITLE,title);
        newValues.put(TIMESCHEDULED,timescheduled);
        // Insert the row into your table
        db.insert(TABLE_NAME, null, newValues);
    }
    public Cursor getLatestTime()
    {
        String currentTime = String.valueOf(new GregorianCalendar().getTimeInMillis());

        return db.query(TABLE_NAME, null, null, null, null, null, TIMESCHEDULED);
    }

    public Cursor getAllEntries ()
    {

        return db.query(TABLE_NAME, null,null, null, null, null, null);

    }

    public int deleteEntry(int ID)
    {
        String id=String.valueOf(ID);
        String where="_id=?";
        int numberOFEntriesDeleted= db.delete(TABLE_NAME, where, new String[]{id}) ;
        Toast.makeText(c, "Alarm Deleted Successfully : " + numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return numberOFEntriesDeleted;

    }

    public Cursor getSinlgeEntry(int ID)
    {
        String id=String.valueOf(ID);
        Cursor cursor=db.query(TABLE_NAME, null, " _id=?", new String[]{id}, null, null, null);
        if(cursor.getCount()<1) // UserName Not Exist
            return null;
        cursor.moveToFirst();

        return cursor;
    }


    public void updateEntry(String title, long timescheduled,int ID)
    {
        //  create object of ContentValues
        ContentValues updatedValues = new ContentValues();
        // Assign values for each Column.
        updatedValues.put(TITLE,title);
        updatedValues.put(TIMESCHEDULED,timescheduled);
        String id=String.valueOf(ID);
        String where="_id =?";
        int i=db.update(TABLE_NAME,updatedValues, where, new String[]{id});

        Toast.makeText(c, " Updated successfully ", Toast.LENGTH_LONG).show();

    }
}