package com.pankaj.maukascholars.util.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.pankaj.maukascholars.util.alarm.databasehandling.DBManipulation;

/**
 * Project Name: 	<Mauka>
 * Author List: 		Pankaj Baranwal
 * Filename: 		<>
 * Functions: 		<>
 * Global Variables:	<>
 * Date of Creation:    <20/01/2018>
 */
public class ScheduleAlarm {
    Context context;
    private DBManipulation databaseAdapter;
    public ScheduleAlarm(Context context)
    {
        this.context=context;
    }

    public void schedulealarm()
    {
        databaseAdapter = new DBManipulation(context);
        databaseAdapter.open();
        //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        Cursor cursor=databaseAdapter.getLatestTime();
        if(cursor.getCount()<1)
            return;

        cursor.moveToFirst();

        long scheduledtime = Long.parseLong(cursor.getString(cursor.getColumnIndex(DBManipulation.TIMESCHEDULED)));
        String id = cursor.getString(cursor.getColumnIndex(DBManipulation.ID));

        Intent intentAlarm = new Intent(context, AlarmActivity.class);
        intentAlarm.putExtra("ID", id);
        // create the object
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.set(AlarmManager.RTC_WAKEUP, scheduledtime, PendingIntent.getActivity(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        databaseAdapter.close();
    }
}