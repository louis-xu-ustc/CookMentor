package com.example.larry.cookmentor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import static android.R.layout.simple_list_item_1;

public class LoadRecipeActivity extends AppCompatActivity {

    public final String TAG = "COOKMENTOR";

    private ListView mListView;
    private ArrayList mListItems = new ArrayList();
    private ArrayAdapter<String> mListViewAdapter;
    private String selected = null;
    private int clickCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_recipe);

        mListView = (ListView)findViewById(R.id.lv_recipe);

        // Add dummy data
        mListItems.add("Pasta");
        mListItems.add("Fried rice");
        mListItems.add("Dumplings");

        mListViewAdapter = new ArrayAdapter(this, simple_list_item_1, mListItems);

        // Assign adapter to ListView
        mListView.setAdapter(mListViewAdapter);

        // ListView Item Click Listener
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // ListView Clicked item value
                String itemValue = (String) mListView.getItemAtPosition(position);

                // Show Alert
                Log.i(TAG, "Select: " + itemValue);
            }

        });
    }

    public void addItems(View v) {
        mListItems.add("Clicked : " + clickCounter++);
        mListViewAdapter.notifyDataSetChanged();
        if(mListViewAdapter.getCount() > 5){
            View item = mListViewAdapter.getView(0, null, mListView);
            item.measure(0, 0);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)(5.5*item.getMeasuredHeight()));
            params.addRule(RelativeLayout.BELOW, R.id.btn_control_group);
            mListView.setLayoutParams(params);
        }
    }
}
