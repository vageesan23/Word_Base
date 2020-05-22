package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.test.activity.AddPhrase;
import com.example.test.activity.DisplayPhrase;
import com.example.test.activity.EditPhrase;
import com.example.test.activity.LanguageSubscription;
import com.example.test.activity.Translate;
import com.example.test.activity.TranslateAll;

public class MainActivity extends AppCompatActivity {

    // defining the onclick actions for this activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // initializing the AddPhrase activity inside main activity
    public void addPhrase(View view) {
        Intent intent = new Intent(getApplicationContext(), AddPhrase.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // initializing the DisplayPhrase activity inside main activity
    public void displayPhrase(View view) {
        Intent intent = new Intent(getApplicationContext(), DisplayPhrase.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // initializing the LanguageSubscription activity inside main activity
    public void languageSubscription(View view) {
        Intent intent = new Intent(getApplicationContext(), LanguageSubscription.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // initializing the EditPhrase activity inside main activity
    public void edit(View view) {
        Intent intent = new Intent(getApplicationContext(), EditPhrase.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // initializing the Translate activity inside main activity
    public void search(View view) {
        Intent intent = new Intent(getApplicationContext(), Translate.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    // initializing the TranslateAll activity inside main activity
    public void translateAll(View view) {
        Intent intent = new Intent(getApplicationContext(), TranslateAll.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


}
