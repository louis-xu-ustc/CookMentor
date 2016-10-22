package com.example.larry.cookmentor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by windsing on 10/22/16.
 */

public class IngredientActivity extends AppCompatActivity {
    private HashMap<String, String> defaultMenu = new HashMap<String, String>();
    public TextView currentSelection = null;

    private void initDefaultMenu(){
        defaultMenu.put("sugar", "12");
        defaultMenu.put("salt", "45");
        defaultMenu.put("oil", "5");
        defaultMenu.put("pan", "null");
        defaultMenu.put("oven", "null");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        initDefaultMenu();

        Bundle bundle = getIntent().getExtras();

        TextView recipeName = (TextView) findViewById(R.id.recipeName);

        currentSelection = (TextView) findViewById(R.id.currentSelection);

        recipeName.setText("Current recipe: " + bundle.getString("recipeName"));

        final ListView ingredient_menu = (ListView) findViewById(R.id.ingredient_menu);

        ArrayList<String> ingredient_names = new ArrayList<String>();

        Iterator it = defaultMenu.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String ingredient_name = (String) pair.getKey();
            String ingredient_quantity = (String) pair.getValue();
            ingredient_names.add(ingredient_name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, ingredient_names);

        ingredient_menu.setAdapter(adapter);

        ingredient_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String ingredient_name = (String) ingredient_menu.getItemAtPosition(position);

                Log.d("tag", "Position " + position + " Ingredient name: " + ingredient_name );
                currentSelection.setText(ingredient_name);
            }
        });
    }

    public void switchToFindInstrumentsActivity(View view) {
        Intent intent = new Intent(this, FindInstrumentActivity.class);
        if (currentSelection.getText().equals(""))
            return;
        intent.putExtra("ingredient", currentSelection.getText());
        startActivity(intent);
//        Log.d(TAG, "ingredient: " + currentSelection.getText());
    }

}
