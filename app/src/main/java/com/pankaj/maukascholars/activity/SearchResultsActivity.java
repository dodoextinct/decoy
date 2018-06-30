package com.pankaj.maukascholars.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pankaj.maukascholars.R;
import com.pankaj.maukascholars.adapters.SavedEventsAdapter;
import com.pankaj.maukascholars.database.DBHandler;
import com.pankaj.maukascholars.util.EventDetails;

import java.util.List;

/**
 * Created by yashkrishan on 13/6/18.
 */

public class SearchResultsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;//necessary declarations
    private RecyclerView.Adapter mAdapter;
    List<EventDetails> mItems;
    EditText mEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            DBHandler db = new DBHandler(this);
            mItems = db.getSearched(query);//go->to decalration to see the method

            //if null value is returened then empty activity is shown
            if (mItems.size() == 0) {
                RelativeLayout empty_layout = findViewById(R.id.empty_layout);
                empty_layout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                Toast.makeText(this,"Not Found!!Redefine your search!!", Toast.LENGTH_SHORT).show();
            }
            else{
                mAdapter = new SavedEventsAdapter(mItems, this);
                mRecyclerView.setAdapter(mAdapter);
            }
        }

    }
}
