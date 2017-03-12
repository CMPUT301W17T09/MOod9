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
import java.util.Map;

/**
 * Originally created by Fady
 * Save returns data through intent extras implemented by cdkushni 3/5/17
 * Changed to implement AdapterView.OnItemSelectedListener by cdkushni on 3/8/17
 * Fixed to use mood model, grabbing emotion model and socialmodel data for spinners and reloading other edit details from passed in mood by cdkushni on 3/10/17
 */
public class AddMoodActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Mood9Application mApplication;
    //String[] emotions = {"Anger", "Confusion", "Happiness", "Sadness", "Shame", "Surprise"};
    //int[] emoticons = {R.drawable.anger, R.drawable.confusion, R.drawable.happiness, R.drawable.sadness, R.drawable.shame, R.drawable.surpise};
    String[] emotions;
    int[] emoticons;
    int emotionId = 0;
    int socialId = 0;
    String selectedEmotion = "Anger";
    int selectedEmote = R.drawable.anger;
    Bundle editCheckB;
    // map<Emotion> emotions = EmotionModel.getEmotions();
    //String[] socials = {"TEMP", "DO NOT USE", "With my enemies", "All Alone"};
    String[] socials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (Mood9Application)getApplicationContext();
        Intent thisIntent = getIntent();
        editCheckB = thisIntent.getExtras();
        int editCheck = editCheckB.getInt("editCheck", 0);
        if (editCheck == 1) {
            setContentView(R.layout.activity_edit_mood);
        } else {
            setContentView(R.layout.activity_add_mood);
        }


        Spinner emotionsSpinner = (Spinner) findViewById(R.id.emotions_spinner);
        Spinner socialSpinner = (Spinner) findViewById(R.id.social_spinner);
        EditText trigger = (EditText) findViewById(R.id.trigger_edittext);
        Button addLocation = (Button) findViewById(R.id.button);
        Button save = (Button) findViewById(R.id.button2);

        emoticons = new int[mApplication.getEmotionModel().getEmotions().size()];
        emotions = new String[mApplication.getEmotionModel().getEmotions().size()];
        for (Map.Entry<String, Emotion> entry : mApplication.getEmotionModel().getEmotions().entrySet()) {
            // drawable name may require not having a end extension like .png
            String test = entry.getValue().getImageName();
            emoticons[Integer.parseInt(entry.getKey())] = getResources().getIdentifier(entry.getValue().getImageName().substring(0, entry.getValue().getImageName().lastIndexOf(".")), "drawable", getPackageName());
            emotions[Integer.parseInt(entry.getKey())] = entry.getValue().getName();
        }
        socials = new String[mApplication.getSocialSituationModel().getSocialSituations().entrySet().size()];

        for (Map.Entry<String, SocialSituation> entry : mApplication.getSocialSituationModel().getSocialSituations().entrySet()) {
            socials[Integer.parseInt(entry.getKey())] = entry.getValue().getName();
        }

        EmotionsSpinnerAdapter emotionsSpinnerAdapter = new EmotionsSpinnerAdapter(getApplicationContext(), emoticons, emotions);

        ArrayAdapter<String> socialSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, socials);
        if (editCheck == 1) {
            Mood oldMoodRestore = editCheckB.getParcelable("oldMood");
            int position = 0;
            for (Map.Entry<String, Emotion> entry : mApplication.getEmotionModel().getEmotions().entrySet()) {
                if (entry.getValue().getName() == oldMoodRestore.getEmotion().getName()) {
                    position = Integer.parseInt(entry.getKey());
                }
            }
            emotionsSpinner.setSelection(position);
            for (Map.Entry<String, SocialSituation> entry : mApplication.getSocialSituationModel().getSocialSituations().entrySet()) {
                if (entry.getValue().getName() == oldMoodRestore.getSocialSituation().getName()) {
                    position = Integer.parseInt(entry.getKey());
                }
            }
            socialSpinner.setSelection(position);
            trigger.setText(oldMoodRestore.getTrigger());
            //TODO: need to figure out how to reload saved map details
        }
        emotionsSpinner.setAdapter(emotionsSpinnerAdapter);
        emotionsSpinner.setOnItemSelectedListener(this);
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

                if (editCheckB.getInt("editCheck", -1) == 0) {
                    // added emoticon parameter to Mood class to store the r.drawable of the selected emotion
                    Mood returnMood = new Mood(100.0, 100.0, trigger.getText().toString(), String.valueOf(emotionId), String.valueOf(socialId), "N/A", new Date(), "myName");
                    returnMood.setmApplication(mApplication);
                    returnMood.setEmotionId(String.valueOf(emotionId));
                    returnMood.setSocialSituationId(String.valueOf(socialId));
                    int newMoodId = editCheckB.getInt("moodId", 0);
                    returnMood.setId(String.valueOf(newMoodId));
                    // Parcelable http://www.parcelabler.com/
                    Intent feedIntent = new Intent();
                    feedIntent.putExtra("mood", returnMood);
                    setResult(0, feedIntent);
                    finish();
                } else if (editCheckB.getInt("editCheck", -1) == 1) {
                    Mood returnMood = editCheckB.getParcelable("oldMood");

                    returnMood.setEmotionId(String.valueOf(emotionId));
                    returnMood.setDate(new Date());
                    returnMood.setmApplication(mApplication);
                    returnMood.setEmotionId(String.valueOf(emotionId));
                    returnMood.setSocialSituationId(String.valueOf(socialId));
                    returnMood.setTrigger(trigger.getText().toString());
                    //TODO: need to find a way to get latitude and longitude from the location setter
                    Intent feedIntent = new Intent();
                    feedIntent.putExtra("mood", returnMood);
                    setResult(1, feedIntent);
                    finish();
                }
                else
                {
                    // TODO: implement failed to find edit check data so needs to error on return type and just make a new mood.
                }
            }
        });

    }
    //TODO: need to set up socialsituation spinner click listener
    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(), emotions[position], Toast.LENGTH_SHORT).show();
        emotionId = position;
        selectedEmotion = emotions[position];
        selectedEmote = emoticons[position];
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
