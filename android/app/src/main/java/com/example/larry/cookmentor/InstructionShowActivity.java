package com.example.larry.cookmentor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static android.R.layout.simple_list_item_2;

public class InstructionShowActivity extends AppCompatActivity {

    public final String TAG = "COOKMENTOR";

    private ListView mListView;
    private ArrayList mListItems;
    private ArrayAdapter<String> mListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_show);

        mListItems = new ArrayList();
        mListView = (ListView)findViewById(R.id.lv_instructions);

        // Add dummy data
        mListItems.add("Heat the pot.");
        mListItems.add("Add sugar.");
        mListItems.add("Add salt.");

        mListViewAdapter = new ArrayAdapter(this, simple_list_item_2, mListItems);

        // Assign adapter to ListView
        mListView.setAdapter(mListViewAdapter);
    }
}
