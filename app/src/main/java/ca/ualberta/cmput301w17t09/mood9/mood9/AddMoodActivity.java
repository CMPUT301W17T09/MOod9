package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class AddMoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood);

        Spinner emotionsSpinner = (Spinner) findViewById(R.id.emotions_spinner);
        Spinner socialSpinner = (Spinner) findViewById(R.id.social_spinner);
        EditText trigger = (EditText) findViewById(R.id.trigger_edittext);
        Button addLocation = (Button) findViewById(R.id.button);
        Button save = (Button) findViewById(R.id.button2);

        String[] emotions = {"Anger", "Confusion", "Happiness", "Sadness", "Shame", "Surprise"};
        int[] emoticons = {R.drawable.anger, R.drawable.confusion, R.drawable.happiness, R.drawable.sadness, R.drawable.shame, R.drawable.surpise};
        // map<Emotion> emotions = EmotionModel.getEmotions();
        String[] socials = {"TEMP", "DO NOT USE", "With my enemies", "All Alone"};

        EmotionsSpinnerAdapter emotionsSpinnerAdapter = new EmotionsSpinnerAdapter(getApplicationContext(), emoticons, emotions);
        emotionsSpinner.setAdapter(emotionsSpinnerAdapter);


        ArrayAdapter<String> socialSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, socials);
        socialSpinner.setAdapter(socialSpinnerAdapter);


        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Add location functionality to the button
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Need to actually save the information that is entered
                Intent feedIntent = new Intent();
                feedIntent.putExtra("Emoticons", "Angry!!!!!");
                setResult(0, feedIntent);
                finish();
            }
        });

    }

}
