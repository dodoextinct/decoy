package com.pankaj.maukascholars.util.alarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.text.format.DateUtils;
import android.view.WindowManager;

import com.pankaj.maukascholars.util.alarm.databasehandling.DBManipulation;

/**
 * Project Name: 	<Mauka>
 * Author List: 		Pankaj Baranwal
 * Filename: 		<>
 * Functions: 		<>
 * Global Variables:	<>
 * Date of Creation:    <20/01/2018>
 */
public class Alert extends DialogFragment {
    Ringtone r;
    private DBManipulation databaseAdapter;
    private ScheduleAlarm obj;
    private int id;
    // Get instance of Vibrator from current Context
    private Vibrator v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        databaseAdapter= new DBManipulation(getContext());
        databaseAdapter.open();

        obj= new ScheduleAlarm(getContext());
        Bundle bundle=getArguments();
        id=Integer.parseInt(bundle.getString("ID"));

        final Cursor cursor=databaseAdapter.getSinlgeEntry(id);

        /** Turn Screen On and Unlock the keypad when this alert dialog is displayed */
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        /** Creating a alert dialog builder */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        /** Setting title for the alert dialog */
        builder.setTitle(cursor.getString(cursor.getColumnIndex(DBManipulation.TITLE)));

        /** Setting the content for the alert dialog */
        builder.setMessage("");
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if(alert == null){
            // alert is null, using backup
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if(alert == null) {
                // alert backup is null, using 2nd backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }

        r = RingtoneManager.getRingtone(getContext(),alert);
        r.play();

        v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Start without a delay
        // Vibrate for 100 milliseconds
        // Sleep for 1000 milliseconds
        long[] pattern = {0, 1000, 500};

        // The '0' here means to repeat indefinitely
        // '0' is actually the index at which the pattern keeps repeating from (the start)
        // To repeat the pattern from any other point, you could increase the index, e.g. '1'
        v.vibrate(pattern, 0);

        /** Defining an OK button event listener */
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /** Exit application on click OK */
                databaseAdapter.deleteEntry(id);
                r.stop();
                v.cancel();
                obj.schedulealarm();
                getActivity().finish();
            }
        });
        builder.setPositiveButton("Snooze for 1 hour", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                long currentTimeMillis = System.currentTimeMillis();
                long nextUpdateTimeMillis = currentTimeMillis + 60 * DateUtils.MINUTE_IN_MILLIS;
                String title= cursor.getString(cursor.getColumnIndex(DBManipulation.TITLE));
                databaseAdapter.updateEntry(title, nextUpdateTimeMillis, id);
                r.stop();
                v.cancel();
                obj.schedulealarm();
                getActivity().finish();

            }
        });

        /** Creating the alert dialog window */
        return builder.create();
    }

    /** The application should be exit, if the user presses the back button */
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
        r.stop();
    }

}