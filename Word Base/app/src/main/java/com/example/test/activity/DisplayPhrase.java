package com.example.test.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.connections.DatabaseConnection;

import java.util.ArrayList;

// This Activity for the user to view words and phrases
public class DisplayPhrase extends AppCompatActivity {
    DatabaseConnection db; // Defining the database connection
    ArrayList<String> namesFromDB; // Retrieving the name array from database
    ListView listView; // List view to display the names
    ArrayAdapter<String> adapter; // Adapter view to display words

    // Implementing the activity using display phrase layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_phrase);

        // Get all the words from database
        db = new DatabaseConnection(this);
        listView=findViewById(R.id.listview);

        Cursor cs = db.getAll();


        namesFromDB = new ArrayList<>();
        setTitle("DISPLAY PHRASE");


        while (cs.moveToNext()) {
            namesFromDB.add(cs.getString(1));

        }
        adapter = new ArrayAdapter<>(DisplayPhrase.this,android.R.layout.simple_list_item_1,namesFromDB);
        listView.setAdapter(adapter);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    // exit from the activity and returns to the home
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        if(id== android.R.id.home){
            this.finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
