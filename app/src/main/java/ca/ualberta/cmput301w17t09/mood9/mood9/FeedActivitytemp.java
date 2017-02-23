package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

public class FeedActivitytemp extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_temp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMood();
            }
        });

        //Set the title of the toolbar - adjust it to each feed
        toolbar.setTitle(R.string.universal_feed);


    }

    private void addMood() {
        Intent addMoodIntent = new Intent(this, AddMoodActivity.class);
        startActivity(addMoodIntent);
    }


}
