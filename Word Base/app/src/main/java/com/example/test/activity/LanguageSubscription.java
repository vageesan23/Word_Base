package com.example.test.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.connections.DatabaseConnection;
import com.example.test.connections.ServiceClass;
import com.ibm.cloud.sdk.core.service.exception.RequestTooLargeException;
import com.ibm.cloud.sdk.core.service.exception.ServiceResponseException;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguages;

import java.util.ArrayList;

// This Activity for the user to display available languages
public class LanguageSubscription extends AppCompatActivity {
    DatabaseConnection db; //define the database inside this activity
    Button save; // define button to save option
    ProgressDialog pDialog; // define processing bar

    ArrayList<String> languageList = new ArrayList(); // getting all the available languages inside array list
    ListView lvCheckBox; // checkbox with list view

    ArrayList<String> selectedItems = new ArrayList<>();
    ArrayList<String> languageTypeList = new ArrayList<>();
    ArrayList<String> languageType = new ArrayList<>();

    //Implementing the activity using language subscription layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_subscription);
        db = new DatabaseConnection(this);

        save = findViewById(R.id.save);

        // initialize the checkboxes to the layout id
        setTitle("LANGUAGE OFFER");
        new Languages().execute();
        lvCheckBox = findViewById(R.id.listview);
        lvCheckBox.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvCheckBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = ((TextView) view).getText().toString();

                // To check and uncheck the languages
                if (selectedItems.contains(selectedItem)) {
                    selectedItems.remove(selectedItem);
                } else
                    selectedItems.add(selectedItem);


                if (languageType.contains(languageTypeList.get(position))) {
                    languageType.remove(languageTypeList.get(position));
                } else
                    languageType.add(languageTypeList.get(position));


            }
        });

        // show up the messages in show dialog method
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowDialog1();
            }
        });
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Alert the user for confirmation
    public void ShowDialog1() {

        final androidx.appcompat.app.AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        popDialog.setTitle("Are you sure want to subscribe the languages? ");

        popDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        // alert if any of the languages were chekced
                        if (selectedItems.size() < 1) {
                            Toast.makeText(getApplicationContext(), "You didn't select any language", Toast.LENGTH_LONG).show();
                        } else { // delete th languages from the database
                            Cursor cursor = db.getSubscriptionAll();
                            while (cursor.moveToNext()) {
                                db.deleteSubscription(cursor.getInt(0));
                            }

                            // if the languages added to database show the toast message
                            for (int j = 0; j < selectedItems.size(); j++) {
                                db.addSubscription(selectedItems.get(j), languageType.get(j));
                            }

                            Toast.makeText(getApplicationContext(), "Successfully updated", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        // to cancel the selected choice
        popDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }

                });


        popDialog.create();
        popDialog.show();

    }

    // exit from the activity and returns to the home
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();

        }
        return super.onOptionsItemSelected(item);
    }


    IdentifiableLanguages languages;

    private class Languages extends AsyncTask<String, Void, String> { //Async task to show up the processing bar

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(LanguageSubscription.this);
            pDialog.setMessage("Loading.....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();


        }

        // TO DO the word translation
        @Override
        protected String doInBackground(String... params) {
            String firstTranslation = "";

            try {
                languages = ServiceClass.initLanguageTranslatorService().listIdentifiableLanguages()
                        .execute().getResult();
                System.out.println(languages);
                for (int i = 0; i < languages.getLanguages().size(); i++) {
                    Log.d("Language", languages.getLanguages().get(i).getLanguage());
                    Log.d("Names", languages.getLanguages().get(i).getName());
                    languageList.add(languages.getLanguages().get(i).getName());
                    languageTypeList.add(languages.getLanguages().get(i).getLanguage());
                }

            } catch (Resources.NotFoundException e) {
                // Handle Not Found (404) exception
            } catch (RequestTooLargeException e) {
                // Handle Request Too Large (413) exception
            } catch (ServiceResponseException e) {
                // Base class for all exceptions caused by error responses from the service
                System.out.println("Service returned status code "
                        + e.getStatusCode() + ": " + e.getMessage());
            }

            return firstTranslation;
        }

        //Adding the languages to database
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            lvCheckBox.setAdapter(new ArrayAdapter<String>(LanguageSubscription.this,
                    android.R.layout.simple_list_item_multiple_choice, languageList));
            for ( int i=0; i< languageList.size(); i++ ) {
                Cursor cursor = db.getSubscriptionAll();
                while (cursor.moveToNext()) {
                    if (languageList.get(i).equals(cursor.getString(1))){
                        lvCheckBox.setItemChecked(i, true);
                        selectedItems.add(languageList.get(i));
                        languageType.add(languageTypeList.get(i));
                    }
                }


            }
            pDialog.cancel();

            Log.d("s", s);
            Cursor cursor = db.getSubscriptionAll();

        }
    }
}