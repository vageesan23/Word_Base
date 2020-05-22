package com.example.test.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.connections.DatabaseConnection;

public class Edit extends AppCompatActivity {
    DatabaseConnection db; //define the database inside this activity
    EditText name; // Text view to edit words

    //Implementing the activity using edit layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);name = findViewById(R.id.name);

        System.out.println(EditPhrase.selectedName);

        db = new DatabaseConnection(this);
        setTitle("EDIT");

        // get and edit the word using it id
        Cursor cs = db.getSpecifiedName(EditPhrase.editId);
        Log.d("id", String.valueOf(EditPhrase.editId));


        if (cs.moveToNext()) {
            name.setText(cs.getString(1));

        }

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

    public void change(View view) { // Asks for the confirmation to edit the words on database
        AlertDialog.Builder confirmation = new AlertDialog.Builder(this);
        confirmation.setTitle("Word Base");
        confirmation.setMessage("Are you sure want to edit this phrase?")
                .setCancelable(false)
                .setPositiveButton("Yes"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updateAll(EditPhrase.editId,name.getText().toString());
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                finish();
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
