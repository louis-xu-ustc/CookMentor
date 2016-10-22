package com.example.larry.cookmentor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FindInstrumentActivity extends AppCompatActivity {
    ArrayList<String> defaultDevices = new ArrayList<String>();
    private TextView currentSelection = null;

//    @// TODO: 10/22/16 use beacon to find devices
    private void initDefaultDevices(){
        defaultDevices.add("nexus");
        defaultDevices.add("iphone");
        defaultDevices.add("pixel");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findinstrument);

        initDefaultDevices();

        Bundle bundle = getIntent().getExtras();
        final TextView currentIngredient = (TextView)findViewById(R.id.title);
        currentIngredient.setText("Current ingredient: " + bundle.getString("ingredient"));

        final TextView currentSelection = (TextView)findViewById(R.id.currentSelection);

        final ListView deviceList = (ListView)findViewById(R.id.deviceList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, defaultDevices);

        deviceList.setAdapter(adapter);

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String deviceName = (String) deviceList.getItemAtPosition(position);

                Log.d("tag", "Position " + position + " device: " + deviceName );
                currentSelection.setText("currnet selection: " + deviceName);
            }
        });


    }

}
