package com.pankaj.maukascholars.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pankaj.maukascholars.R;
import com.pankaj.maukascholars.adapters.VerticalPagerAdapter;
import com.pankaj.maukascholars.application.PreciselyApplication;
import com.pankaj.maukascholars.database.DBHandler;
import com.pankaj.maukascholars.holders.VerticalViewPager;
import com.pankaj.maukascholars.util.Constants;
import com.pankaj.maukascholars.util.CustomTabHelper;
import com.pankaj.maukascholars.util.EventDetails;
import com.pankaj.maukascholars.util.alarm.ScheduleAlarm;
import com.pankaj.maukascholars.util.alarm.databasehandling.DBManipulation;
import com.rey.material.widget.ProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Project Name: 	<Visual Perception For The Visually Impaired>
 * Author List: 		Pankaj Baranwal
 * Filename: 		<>
 * Functions: 		<>
 * Global Variables:	<>
 */
public class VerticalViewPagerActivity extends BaseNavigationActivity implements View.OnClickListener {

    RelativeLayout loading;
    ProgressView progress;
    VerticalViewPager verticalViewPager;
    BroadcastReceiver receiver = null;
    private List<EventDetails> mItems = new ArrayList<>();
    private int page = 0;
    VerticalPagerAdapter adapter;
    boolean updating = false;
    AppBarLayout appBarLayout;
    int previous_position = 0;
    long time = System.currentTimeMillis();
    DBHandler db;
    boolean isVisible = false;
    Handler handler = new Handler();

    com.rey.material.widget.ImageView refresh, share, save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Constants.toolbar_title = " ";

        setContentView(R.layout.inshorts);
        loading = findViewById(R.id.progress_rl);
        progress = findViewById(R.id.progress);
        progress.start();
        loading.setVisibility(View.VISIBLE);
        appBarLayout = findViewById(R.id.appBar_Layout);

        setSupportActionBar(toolbar);
        getData();

    }
    //search creation
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();//inflating the menu
        menuInflater.inflate(R.menu.menu_search, menu);//menu resource file can be found in menu folder of res

        //initailising search actions
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView =
                (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_search:
            return true;
        }
        return false;
    }

    private void init() {
        db = new DBHandler(this);

        verticalViewPager = findViewById(R.id.verticleViewPager);
        adapter = new VerticalPagerAdapter(this, mItems);
        verticalViewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        verticalViewPager.singleEventDetail = mItems.get(0);
        refresh = findViewById(R.id.refresh);
//        star = findViewById(R.id.star);
        save = findViewById(R.id.save);
        share = findViewById(R.id.share);
        share.setOnClickListener(this);
//        star.setOnClickListener(this);
        save.setOnClickListener(this);
        refresh.setOnClickListener(this);

        loading.setVisibility(View.GONE);
        progress.stop();
        if (receiver == null) {
            IntentFilter filter = new IntentFilter("PreciselyReceiver");
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    getData();
                }
            };
            registerReceiver(receiver, filter);
        }
        verticalViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (db.getEvent(mItems.get(position).getId()) != null && db.getEvent(mItems.get(position).getId()).getStarred() == 1){
                    save.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_white_24dp_filled));
                }else{
                    save.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
                }
                verticalViewPager.singleEventDetail = mItems.get(position);
                if (position + 3> mItems.size() && !updating) {
                    updating = true;
                    page++;
                    getData();
                }
                if (position - previous_position > 0) {
                    if (System.currentTimeMillis() - time > 3000) {
                        time = System.currentTimeMillis();
                        sendViewedInformation(previous_position++, mItems.get(previous_position-1).getId());
                    }
                }
                time = System.currentTimeMillis();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void sendViewedInformation(final int position, final int post_id) {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.url_view_op, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("post_id", String.valueOf(post_id));
                params.put("user_id", Constants.user_id);
                return params;
            }
        };

        PreciselyApplication.getInstance().addToRequestQueue(request, "user_viewed");
    }

    void getData() {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.url_event_details, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    updating = false;
                    try {
                        if (response.contains("[{\""))
                            response = response.substring(response.indexOf("[{\""));
                        if (page == 0)
                            mItems.clear();
                        JSONArray jA = new JSONArray(response);
                        //    0, 1, 2, 7, 12, 8, 13, 9, 3, 4, 5
                        for (int i = 0; i < jA.length(); i++) {
                            JSONObject jO = jA.getJSONObject(i);
//                                                                                                  int id, String title, String description, String deadline, String name, String image, String icon, String link, String eligibility, String requirements, String benefits
                            Log.e("TAGS", jO.getString("TAGS"));
                            mItems.add(new EventDetails(Integer.parseInt(jO.getString("ID")), jO.getString("HEADLINE"), jO.getString("DESCRIPTION"), jO.getString("DEADLINE"), jO.getString("NAME"), jO.getString("IMAGE"), jO.getString("NAMELINK"), jO.getString("LINK"), jO.getString("ELIGIBILITY"), jO.getString("REQUIREMENTS"), jO.getString("BENEFITS"), jO.getString("TAGS")));
//                            mItems.add(new EventDetails(Integer.parseInt(jA.getJSONArray(i).get(0).toString()), jA.getJSONArray(i).get(1).toString(), jA.getJSONArray(i).get(2).toString(), jA.getJSONArray(i).get(7).toString(), jA.getJSONArray(i).get(12).toString(), jA.getJSONArray(i).get(8).toString(), jA.getJSONArray(i).get(13).toString(), jA.getJSONArray(i).get(9).toString(), jA.getJSONArray(i).get(3).toString(), jA.getJSONArray(i).get(4).toString(), jA.getJSONArray(i).get(5).toString()));
                        }
                        if (page == 0)
                            init();
                        else{
                            adapter.cards = mItems;
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        loading.setVisibility(View.GONE);
                        progress.stop();
                        Toast.makeText(VerticalViewPagerActivity.this, "We are facing some internal issues.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loading.setVisibility(View.GONE);
                    progress.stop();
                    Toast.makeText(VerticalViewPagerActivity.this, "Server is no longer speaking to you", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerticalViewPagerActivity.this, "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                getData();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                JSONArray jO = new JSONArray();
                for (int i = 0; i < Constants.clickedFilters.size(); i++) {
                    jO.put(Constants.filters.get(Constants.clickedFilters.get(i)));
                }
                params.put("id", Constants.user_id);
                params.put("page", String.valueOf(page));
                params.put("language_id", String.valueOf(Constants.language_id));
                params.put("tags", jO.toString());
                return params;
            }
        };

        PreciselyApplication.getInstance().addToRequestQueue(request, "dashboard_data");
    }

    @Override
    public void onClick(View view) {
        int position = verticalViewPager.getCurrentItem();
        switch (view.getId()) {
            case R.id.refresh:
                page = 0;
                loading.setVisibility(View.VISIBLE);
                progress.start();
                getData();
                break;
            case R.id.share:
                share(position);
                break;
            case R.id.save:
                starEvent(position);
                break;

        }
    }

    private void open(int position) {
        String url = mItems.get(position).getLink();
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        CustomTabHelper mCustomTabHelper = new CustomTabHelper();
        if (mCustomTabHelper.getPackageName(this).size() != 0) {
            CustomTabsIntent customTabsIntent =
                    new CustomTabsIntent.Builder()
                            .build();
            customTabsIntent.intent.setPackage(mCustomTabHelper.getPackageName(this).get(0));
            customTabsIntent.launchUrl(this, Uri.parse(url));
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    private void share(int position) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Found a great opportunity for you!\n" + mItems.get(position).getLink() + "\nThere are many more where this came from!\nVisit: http://bit.ly/preciselyapp now");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void starEvent(int position) {
        DBHandler db = new DBHandler(this);
        if (db.getEvent(mItems.get(position).getId()) != null && db.getEvent(mItems.get(position).getId()).getStarred() == 1) {
            save.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_border_black_24dp));
            if (db.getEvent(mItems.get(position).getId()).getSaved() == 1)
                mItems.get(position).setSaved(1);
            mItems.get(position).setStarred(0);
            db.addEvent(mItems.get(position));
        } else {
            save.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_white_24dp_filled));
            if (db.getEvent(mItems.get(position).getId()) != null)
                mItems.get(position).setSaved(1);
            mItems.get(position).setStarred(1);
            db.addEvent(mItems.get(position));
            Toast.makeText(this, "Opportunity Saved!", Toast.LENGTH_SHORT).show();
            scheduleReminder(mItems.get(position).getDeadline(), mItems.get(position).getTitle() + "\n" + mItems.get(position).getLink());
        }
    }

    void scheduleReminder(String str_date, String title) {
        str_date += " 18:00:00";
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(str_date);

            long alarmOffset = date.getTime();

            TimeZone.setDefault(TimeZone.getDefault());
            Date d = new Date();
            long current = d.getTime();
            long alarmdiff = alarmOffset - current;
            if (alarmdiff < 0) {
                Toast.makeText(getApplicationContext(), "Cannot set alarm for a past time.", Toast.LENGTH_SHORT).show();
                return;
            }

            DBManipulation databaseAdapter = new DBManipulation(getApplicationContext());
            databaseAdapter.open();

            databaseAdapter.insertEntry(title, alarmOffset);
            databaseAdapter.close();

            ScheduleAlarm obj = new ScheduleAlarm(getApplicationContext());
            obj.schedulealarm();
            Toast.makeText(getApplicationContext(), "Reminder scheduled successfully. " + str_date, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Unable to schedule alarms", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveEvent(int position) {
        DBHandler db = new DBHandler(this);
        if (db.getEvent(mItems.get(position).getId()) != null && db.getEvent(mItems.get(position).getId()).getSaved() == 1) {
            Toast.makeText(this, "Already saved!", Toast.LENGTH_SHORT).show();
        } else {
            if (db.getEvent(mItems.get(position).getId()) != null)
                mItems.get(position).setStarred(1);
            mItems.get(position).setSaved(1);
            db.addEvent(mItems.get(position));
            Toast.makeText(this, "Event Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("PreciselyReceiver");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getData();
            }
        };
        registerReceiver(receiver, filter);

    }

    public void ViewAppBarLay(){
        if (!isVisible)
        {
            isVisible = true;
            appBarLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            isVisible = false;
            appBarLayout.setVisibility(View.GONE);
        }
    }

    public void SwipeUpViewAppBar(){
        AppBarLayout appBarLayout;
        appBarLayout = findViewById(R.id.appBar_Layout);
        if (appBarLayout.getVisibility() == View.VISIBLE)
        {
            appBarLayout.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver!=null)
        unregisterReceiver(receiver);
    }

    public EventDetails getCurrentItemDetails(){
        return verticalViewPager.singleEventDetail;
    }

    /*protected void handleMenuSearch(){
        ActionBar action = getSupportActionBar(); //get the actionbar

        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.search));


            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSearch = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        //starts the search results activity where the query is searched and displayed
                        Intent viewresults = new Intent(VerticalViewPagerActivity.this, SearchResultsActivity.class);
                        startActivity(viewresults);
                        return true;
                    }
                    return false;
                }
            });


            edtSearch.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.cross));

            isSearchOpened = true;
        }*/

}