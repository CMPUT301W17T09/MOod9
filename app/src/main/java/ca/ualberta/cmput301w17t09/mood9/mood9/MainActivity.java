package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button goButton = (Button) findViewById(R.id.go);
        EditText usernameField = (EditText) findViewById(R.id.username_field);


        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedIntent = new Intent(MainActivity.this, FeedActivitytemp.class);
                startActivity(feedIntent);
                finish();
            }
        });

    }




}

