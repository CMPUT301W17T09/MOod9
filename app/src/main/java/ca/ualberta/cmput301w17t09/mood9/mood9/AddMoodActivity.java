package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.Date;

/**
 * Originally created by Fady
 * Save returns data through intent extras implemented by cdkushni 3/5/17
 * Changed to implement AdapterView.OnItemSelectedListener by cdkushni on 3/8/17
 */
public class AddMoodActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String[] emotions = {"Anger", "Confusion", "Happiness", "Sadness", "Shame", "Surprise"};
    int[] emoticons = {R.drawable.anger, R.drawable.confusion, R.drawable.happiness, R.drawable.sadness, R.drawable.shame, R.drawable.surpise};
    String selectedEmotion = "Anger";
    int selectedEmote = R.drawable.anger;
    // map<Emotion> emotions = EmotionModel.getEmotions();
    String[] socials = {"TEMP", "DO NOT USE", "With my enemies", "All Alone"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood);

        Spinner emotionsSpinner = (Spinner) findViewById(R.id.emotions_spinner);
        Spinner socialSpinner = (Spinner) findViewById(R.id.social_spinner);
        EditText trigger = (EditText) findViewById(R.id.trigger_edittext);
        Button addLocation = (Button) findViewById(R.id.button);
        Button save = (Button) findViewById(R.id.button2);

        EmotionsSpinnerAdapter emotionsSpinnerAdapter = new EmotionsSpinnerAdapter(getApplicationContext(), emoticons, emotions);
        emotionsSpinner.setAdapter(emotionsSpinnerAdapter);
        emotionsSpinner.setOnItemSelectedListener(this);


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

                // added emoticon parameter to Mood class to store the r.drawable of the selected emotion
                Mood returnMood = new Mood("0", 100.0, 100.0, "Triggered", selectedEmotion, selectedEmote, "With my enemies", "N/A", new Date(), "myName");
                // Parcelable http://www.parcelabler.com/
                Intent feedIntent = new Intent();
                feedIntent.putExtra("mood", returnMood);
                setResult(0, feedIntent);
                finish();
            }
        });

    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(), emotions[position], Toast.LENGTH_LONG).show();
        selectedEmotion = emotions[position];
        selectedEmote = emoticons[position];
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
