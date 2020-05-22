package com.example.test.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.test.R;
import com.example.test.connections.DatabaseConnection;
import com.example.test.connections.ServiceClass;
import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.service.exception.RequestTooLargeException;
import com.ibm.cloud.sdk.core.service.exception.ServiceResponseException;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;

import java.util.ArrayList;

// This Activity for the user to translate and pronounce words
public class Translate extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    Spinner search; // dropdown to get subscribed languages
    ListView list; // show the words in list view
    ArrayList<String> names; // array list to get the words
    ArrayAdapter<String> adapter; //adapter view for list
    DatabaseConnection db; //define the database inside this activity
    ArrayList languages = new ArrayList<String>(); // to add the subscribed languages inside the arrayList
    String languageString = ""; // get the language string
    String phrase = ""; // get the phrase
    Button translate, pronounce; //define translate and pronounce buttons
    String languagetype; //define the language type
    String translatedWord = ""; // to get the translated word
    TextView textView3; // to show the translated word
    ProgressDialog pDialog;
    private StreamPlayer player = new StreamPlayer();
    ProgressBar progressBar; //display the processing bar

    //Implementing the activity using translate layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        search = findViewById(R.id.search);
        pronounce = findViewById(R.id.pronounce);
        list = findViewById(R.id.list);
        textView3 = findViewById(R.id.textView3);
        progressBar=findViewById(R.id.progressBar1);

        db = new DatabaseConnection(this);

        names = new ArrayList<>();

        db = new DatabaseConnection(this);
        translate = findViewById(R.id.translate);
        setTitle("TRANSLATE");
        Cursor cs = db.getAll();
        while (cs.moveToNext()) {
            names.add(cs.getString(1));
        }

        Cursor cursor = db.getSubscriptionAll();
        while (cursor.moveToNext()) {
            Log.d("items", cursor.getString(1));
            languages.add(cursor.getString(1));
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (translatedWord.equals("")) { //if none of the words were selected
                    Toast.makeText(getApplicationContext(), "Not valid word to pronounce  ", Toast.LENGTH_SHORT).show();
                } else { // pronounce the translated word
                    new SpeechTask().execute(translatedWord);
                }
            }
        });

        // set all the subscribed languages inside the dropdown
        Spinner spin = (Spinner) findViewById(R.id.search);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, languages);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);

        // show the words in adapter view
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                phrase = names.get(position);

            }
        });

        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TranslateAsync().execute();

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

    // to create ordered text view inside the adapter view if translated all words
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        languageString = languages.get(i).toString();
        Cursor cursor = db.getSubscriptionAll();
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals(languageString))
                languagetype = cursor.getString(2);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private class TranslateAsync extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() { // show the processing bar
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        //TO DO the word translation
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            String firstTranslation = "";

            try {

                TranslateOptions translateOptions = new TranslateOptions.Builder()
                        .addText(phrase)
                        .modelId("en-" + languagetype)
                        .build();


                TranslationResult result = ServiceClass.initLanguageTranslatorService().translate(translateOptions)
                        .execute().getResult();
                Log.d("result", result.getWordCount().toString());
                translatedWord = result.getTranslations().get(0).getTranslation();
                Log.d("translatedWord", translatedWord);
            } catch (Resources.NotFoundException e) {
                // Handle Not Found (404) exception
            } catch (RequestTooLargeException e) {
                // Handle Request Too Large (413) exception
            } catch (ServiceResponseException e) {
                translatedWord = "";
                System.out.println("Service returned status code "
                        + e.getStatusCode() + ": " + e.getMessage());
            }

            return firstTranslation;
        }

        // get rid of the processing bar
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            textView3.setText(translatedWord);
            progressBar.setVisibility(View.GONE);

        }
    }

    private class SpeechTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() { // show the processing bar
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        //TO DO the word pronounce
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                    .text(params[0])
                    .voice(SynthesizeOptions.Voice.EN_US_LISAVOICE)
                    .accept(HttpMediaType.AUDIO_WAV)
                    .build();
            StreamPlayer player = new StreamPlayer();
            progressBar.setVisibility(View.GONE);
            player.playStream(ServiceClass.initTextToSpeechService().synthesize(synthesizeOptions).execute().getResult());
            return "Did synthesize";
        }
    }
}