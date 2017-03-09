package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;

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
                Intent feedIntent = new Intent(MainActivity.this, FeedActivity.class);
                startActivity(feedIntent);
                finish();
            }
        });

        Mood m1 = new Mood("0",12.22,13.22,"Trigger","1","Alone","22",new Date(12-12-2016),"1");
        /*
        ElasticSearchMOodController.AddMoodsTask addMoodsTask = new ElasticSearchMOodController.AddMoodsTask();
        addMoodsTask.execute(m1);
        */
        ElasticSearchMOodController.GetMoodsTask getMoodsTask = new ElasticSearchMOodController.GetMoodsTask();
        getMoodsTask.execute("");

        ArrayList<Mood> moods = new ArrayList<Mood>();

        try {
            moods = getMoodsTask.get();
        } catch (Exception e) {
            Log.i("Error", "Can't get moods from ElasticSearch");
        }


        ElasticSearchMOodController.DeleteMoodTask deleteMoodTask = new ElasticSearchMOodController.DeleteMoodTask();
        deleteMoodTask.execute(m1);

        ElasticSearchMOodController.GetMoodsTask getMoodsTask2 = new ElasticSearchMOodController.GetMoodsTask();
        getMoodsTask2.execute("");
        try {
            moods = getMoodsTask2.get();
        } catch (Exception e) {
            Log.i("Error", "Can't get moods from ElasticSearch");
        }

        int xx= 1;
    }
}

