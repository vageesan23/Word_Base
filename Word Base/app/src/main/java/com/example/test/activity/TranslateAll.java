package com.example.test.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.example.test.R;
import com.example.test.connections.DatabaseConnection;
import com.example.test.connections.ServiceClass;
import com.ibm.cloud.sdk.core.service.exception.RequestTooLargeException;
import com.ibm.cloud.sdk.core.service.exception.ServiceResponseException;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This Activity for the user to translate all words and use it offline
public class TranslateAll extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ArrayList<String> languages=new ArrayList<>(); // array list to show the languages
    ListView list; // show the words inside list
   SimpleAdapter adapter; // use adapter view to the list
    Button search,translateall; // defining the translateAll and view buttons
    String languageString = ""; // getting the translated language
    String languagetype; // set language type string
    public List<Map<String, String>> cycleData1 = new ArrayList<>(); // defining a hashMap
    ProgressBar progressBar; //display the processing bar


    //Implementing the activity using translate all layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate_all); DatabaseConnection db= new DatabaseConnection(this);
        Cursor cursor =db.getSubscriptionAll();
        list = findViewById(R.id.list);
        progressBar=findViewById(R.id.progressBar2);

        setTitle("TRANSLATE ALL");
        while (cursor.moveToNext()){
            Log.d("items",cursor.getString(1));
            languages.add(cursor.getString(1));

        }

        Spinner spin = (Spinner) findViewById(R.id.search);
        search=findViewById(R.id.search2);

        translateall=findViewById(R.id.translateall);

        translateall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TranslateAsync().execute();
            }
        });

        // display all the translated words from the database
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseConnection db= new DatabaseConnection(TranslateAll.this);
                Cursor cursor =db.getPhrase(languageString);
                           cycleData1.clear();
                while (cursor.moveToNext()){
                    Map<String, String> record = new HashMap<String, String>(2);
                    record.put("name", cursor.getString(1));
                    record.put("language",cursor.getString(3));
                   cycleData1.add(record);
                }
                // show all the translated words in adapter view
                adapter = new SimpleAdapter(TranslateAll.this, cycleData1,
                        R.layout.view,
                        new String[]{ "name", "language"},
                        new int[]{R.id.rowTextview1, R.id.rowTextView2}
                );

                list.setAdapter(adapter);
            }
        });

        // set all the subscribed languages inside the dropdown
        spin.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,languages);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private class TranslateAsync extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() { // show the processing bar
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();

        }

        //TO DO the all word translation
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            String firstTranslation = "";
            DatabaseConnection db=new DatabaseConnection(TranslateAll.this);
            Cursor cursor1 = db.getAll();
            while (cursor1.moveToNext()) {
                Log.d("kakakka",cursor1.getString(1));

                try {

                    TranslateOptions translateOptions = new TranslateOptions.Builder()
                            .addText(cursor1.getString(1))
                            .modelId("en-" + languagetype)
                            .build();


                    TranslationResult result = ServiceClass.initLanguageTranslatorService().translate(translateOptions)
                            .execute().getResult();
                    Log.d("result", result.getWordCount().toString());

                    String translatedWord = result.getTranslations().get(0).getTranslation();
                    Log.d("translatedWord", translatedWord);
                    int count = 0;
                    Cursor cursor = db.getPhrase(languageString);
                    while (cursor.moveToNext()) {
                        if (cursor.getString(3).equals(translatedWord)) {
                            count++;
                        }

                    }

                    if (count < 1) {
                        db.addWords(cursor1.getString(1), languageString, translatedWord);
                    }
                    db.close();
                } catch (Resources.NotFoundException e) {
                    // Handle Not Found (404) exception
                } catch (RequestTooLargeException e) {
                    // Handle Request Too Large (413) exception
                } catch (ServiceResponseException e) {
                    System.out.println("Service returned status code "
                            + e.getStatusCode() + ": " + e.getMessage());
                }

            }

            return firstTranslation;
        }

        // get rid of the processing bar
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);

        }
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

    // to create ordered text view inside the adapter view
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        DatabaseConnection db=new DatabaseConnection(TranslateAll.this);
        languageString = languages.get(i);
        Cursor cursor = db.getSubscriptionAll();
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals(languageString))
                languagetype = cursor.getString(2);

        }
        Log.d("languagetype",languageString);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }
}
