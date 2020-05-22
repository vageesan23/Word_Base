package com.example.test.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.connections.DatabaseConnection;

// This Activity for the user to add words and phrases
public class AddPhrase extends AppCompatActivity {
    DatabaseConnection db; //define the database inside this activity
    EditText name; // Text view to add words

    //Implementing the activity using addPhrases layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phrase);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("ADD PHRASE");
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

    public void register(View view) {

        name = findViewById(R.id.name);

        db = new DatabaseConnection(this);

        if (name.getText().toString().equals("")) { // checks and display if the text view submitted blanked
            name.setHintTextColor(getResources().getColor(R.color.colorWarning));
            toastMethod("Please fill the field");
        } else { // Asks for the confirmation to add the words to database
            AlertDialog.Builder confirmation = new AlertDialog.Builder(this);
            confirmation.setTitle("Word Base");
            confirmation.setMessage("Are you sure want to add this phrase?").setCancelable(false)
                    .setPositiveButton("Yes"
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Cursor res=db.checkPhrase(name.getText().toString());

                                    if (res.moveToNext()){ // Alert if the word is already exist in the database
                                        Toast.makeText(getApplicationContext(),"This word is Already Added to database",Toast.LENGTH_LONG).show();
                                    }else { // Checks and add the words to database
                                        db.addToDB(name.getText().toString());
                                        toastMethod("Successfully Added");
                                        startActivity(getIntent());
                                        overridePendingTransition(0, 0);
                                        name.setText("");
                                        finish();
                                    }
                                }
                            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = confirmation.create();
            alertDialog.show();
        }

    }

    // Toast method to alert user that the word is successfully added
    public void toastMethod(String toast) {
        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
    }
}