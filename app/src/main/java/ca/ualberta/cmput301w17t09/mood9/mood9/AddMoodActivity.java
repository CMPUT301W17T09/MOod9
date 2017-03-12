package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import android.location.LocationListener;

import java.util.Date;
import java.util.Map;
import java.util.Random;

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
    double latitude = 100;
    double longitude = 100;
    String imageTriggerId = "N/A";
    String selectedEmotion = "Anger";
    String userId = "newUser";

    int oldMoodIndex = 0;
    Mood returnMood;
    int selectedEmote = R.drawable.anger;
    String selectedSocial = "N/A";
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
            oldMoodIndex = editCheckB.getInt("moodIndex");
        } else {
            setContentView(R.layout.activity_add_mood);
        }


        Spinner emotionsSpinner = (Spinner) findViewById(R.id.emotions_spinner);
        Spinner socialSpinner = (Spinner) findViewById(R.id.social_spinner);
        EditText trigger = (EditText) findViewById(R.id.trigger_edittext);
        Button addLocation = (Button) findViewById(R.id.button);
        Button save = (Button) findViewById(R.id.button2);
        Button delete = (Button) findViewById(R.id.delete_button);
        TextView addedLocation = (TextView) findViewById(R.id.textView5);

        emoticons = new int[mApplication.getEmotionModel().getEmotions().size()];
        emotions = new String[mApplication.getEmotionModel().getEmotions().size()];
        for (Map.Entry<String, Emotion> entry : mApplication.getEmotionModel().getEmotions().entrySet()) {
            // drawable name may require not having a end extension like .png
            String imgNameBuilder = entry.getValue().getName().toLowerCase() + ".png";
            emoticons[Integer.parseInt(entry.getKey())] = getResources().getIdentifier(imgNameBuilder.substring(0, imgNameBuilder.lastIndexOf(".")), "drawable", getPackageName());
            emotions[Integer.parseInt(entry.getKey())] = entry.getValue().getName();
        }
        socials = new String[mApplication.getSocialSituationModel().getSocialSituations().entrySet().size()];

        for (Map.Entry<String, SocialSituation> entry : mApplication.getSocialSituationModel().getSocialSituations().entrySet()) {
            socials[Integer.parseInt(entry.getKey())] = entry.getValue().getName();
        }

        EmotionsSpinnerAdapter emotionsSpinnerAdapter = new EmotionsSpinnerAdapter(getApplicationContext(), emoticons, emotions);

        ArrayAdapter<String> socialSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, socials);
        if (editCheck == 1) {
            returnMood = mApplication.getMoodLinkedList().get(oldMoodIndex);
            int position = 0;
            for (Map.Entry<String, Emotion> entry : mApplication.getEmotionModel().getEmotions().entrySet()) {
                if (entry.getValue().getName() == returnMood.getEmotion().getName()) {
                    position = Integer.parseInt(entry.getKey());
                }
            }
            emotionsSpinner.setAdapter(emotionsSpinnerAdapter);
            emotionsSpinner.setSelection(position);
            for (Map.Entry<String, SocialSituation> entry : mApplication.getSocialSituationModel().getSocialSituations().entrySet()) {
                if (entry.getValue().getName() == returnMood.getSocialSituation().getName()) {
                    position = Integer.parseInt(entry.getKey());
                }
            }
            socialSpinner.setAdapter(socialSpinnerAdapter);
            socialSpinner.setSelection(position);
            trigger.setText(returnMood.getTrigger());
            //TODO: need to figure out how to reload saved map details
        }
        else {
            emotionsSpinner.setAdapter(emotionsSpinnerAdapter);
            socialSpinner.setAdapter(socialSpinnerAdapter);
        }
        emotionsSpinner.setOnItemSelectedListener(this);
        socialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), socials[position], Toast.LENGTH_SHORT).show();
                socialId = position;
                selectedSocial = socials[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                socialId = 0;
                selectedSocial = socials[0];
            }
        });

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addedLocation.setText("Added!");
            }
        });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Need to actually save the information that is entered

                if (editCheckB.getInt("editCheck", -1) == 1) {
                    try {
                        returnMood.setEmotionId(String.valueOf(emotionId));
                        returnMood.setDate(new Date());
                        returnMood.setmApplication(mApplication);
                        returnMood.setEmotionId(String.valueOf(emotionId));
                        returnMood.setSocialSituationId(String.valueOf(socialId));
                        returnMood.setTrigger(trigger.getText().toString());
                        //TODO: need to find a way to get latitude and longitude from the location setter
                        mApplication.getMoodLinkedList().set(oldMoodIndex, returnMood);
                        //mApplication.getMoodModel().updateMood(returnMood.getId(), returnMood);
                        Intent feedIntent = new Intent();
                        feedIntent.putExtra("moodIndex", oldMoodIndex);
                        setResult(1, feedIntent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                }
                else {
                    // added emoticon parameter to Mood class to store the r.drawable of the selected emotion
                    returnMood = new Mood(latitude, longitude, trigger.getText().toString(), String.valueOf(emotionId), String.valueOf(socialId), imageTriggerId, new Date(), userId);
                    returnMood.setmApplication(mApplication);
                    returnMood.setEmotionId(String.valueOf(emotionId));
                    returnMood.setSocialSituationId(String.valueOf(socialId));
                    Random rand = new Random();
                    userId = String.valueOf(rand.nextInt(1000000));
                    returnMood.setId(String.valueOf(userId));

                    mApplication.getMoodLinkedList().add(returnMood);
                    //mApplication.getMoodModel().addMood(returnMood);
                    finish();
                }
            }
        });
    }
    //TODO: need to set up socialsituation spinner click listener
    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Spinner spinner = (Spinner) arg0;
        if (spinner.getId() == R.id.emotions_spinner) {
            Toast.makeText(getApplicationContext(), emotions[position], Toast.LENGTH_SHORT).show();
            emotionId = position;
            selectedEmotion = emotions[position];
            selectedEmote = emoticons[position];
        }
        else if (spinner.getId() == R.id.social_spinner) {
            Toast.makeText(getApplicationContext(), socials[position], Toast.LENGTH_SHORT).show();
            socialId = position;
            selectedSocial = socials[position];
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}

