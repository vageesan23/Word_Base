package com.example.test.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.test.R;
import com.example.test.connections.DatabaseConnection;

import java.util.ArrayList;

// This Activity for the user to edit words and phrases
public class EditPhrase extends AppCompatActivity {
    DatabaseConnection db; //define the database inside this activity
    ArrayList<String> editName=  new ArrayList<>(); // adding the edited names inside arrayList
    static String selectedName; // to pick selected words
    private ListView lvCheckBox; //words to edit in listView
    public static int editId; // choose the id of the word to edit
    Button edit; // button to edit
    ArrayList<Integer> editIds=new ArrayList<>(); // adding edited words using to arrayList

    //Implementing the activity using edit phrase layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phrase);

        db = new DatabaseConnection(this);
        edit=findViewById(R.id.edit);
        setTitle("EDIT PHRASE");

        Cursor cs = db.getAll();

        while (cs.moveToNext()) {
            editName.add(cs.getString(1));

            editIds.add(cs.getInt(0));

        }

        // set the word to edit in listView
        lvCheckBox = (ListView) findViewById(R.id.listview);

        // Adapter view to display words and options
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_single_choice,editName);
        lvCheckBox.setAdapter(arrayAdapter);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Edit.class);
                startActivity(intent);
            }
        });

        lvCheckBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("position",String.valueOf(position));
                editId=editIds.get(position);

            }
        });

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
