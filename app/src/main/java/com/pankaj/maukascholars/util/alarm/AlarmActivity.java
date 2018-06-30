package com.pankaj.maukascholars.util.alarm;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Project Name: 	<Mauka>
 * Author List: 		Pankaj Baranwal
 * Filename: 		<>
 * Functions: 		<>
 * Global Variables:	<>
 * Date of Creation:    <20/01/2018>
 */
public class AlarmActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Creating an Alert Dialog Window */

        Alert alert = new Alert();
        Bundle bundle = new Bundle();
        bundle.putString("ID", getIntent().getStringExtra("ID"));
        alert.setArguments(bundle);
        /** Opening the Alert Dialog Window */
        alert.show(getSupportFragmentManager(), "Alert");
    }
}
