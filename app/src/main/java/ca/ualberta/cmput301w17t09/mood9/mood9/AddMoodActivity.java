package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Originally created by Fady
 * Save returns data through intent extras implemented by cdkushni 3/5/17
 * Changed to implement AdapterView.OnItemSelectedListener by cdkushni on 3/8/17
 * Fixed to use mood model, grabbing emotion model and socialmodel data for spinners and reloading other edit details from passed in mood by cdkushni on 3/10/17
 * Modified by cdkushni on 3/20/17, fixed some bugs with shared preferences that came up whenever a new account was made and implemented a new version of the location
 * service, not tested yet.
 */
public class AddMoodActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Mood9Application mApplication;
    Context addMContext = this;
    String[] emotions;
    int[] emoticons;
    int emotionId = 0;
    int socialId = 0;
    double latitude = 0.0;
    double longitude = 0.0;
    Date curDate = new Date();
    String imageTriggerId = "N/A";
    String selectedEmotion = "Anger";
    String userId = "newUser";

    private int mYear, mMonth, mDay;

    ImageView cameraImage;
    Bitmap imageBitmap = null;

    int oldMoodIndex = 0;
    Mood returnMood;
    int selectedEmote = R.drawable.anger;
    String selectedSocial = "N/A";
    Bundle editCheckB;
    String[] socials;
    private static final int REQUEST_PERMISSION_FINE = 0;
    private LocationService locationService;

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPreferences.getString("username", "test");
        userId = UserModel.getUserID(userName).getId();

        cameraImage  = (ImageView) findViewById(R.id.cameraImage);
        ImageButton cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        Spinner emotionsSpinner = (Spinner) findViewById(R.id.emotions_spinner);
        Spinner socialSpinner = (Spinner) findViewById(R.id.social_spinner);
        EditText trigger = (EditText) findViewById(R.id.trigger_edittext);
        Button addLocation = (Button) findViewById(R.id.button);
        Button save = (Button) findViewById(R.id.button2);
        Button calendar = (Button) findViewById(R.id.calendar);
        TextView addedLocation = (TextView) findViewById(R.id.textView5);
        TextView txtDate = (TextView) findViewById(R.id.curDate);

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
                if (entry.getValue().getName().equals(mApplication.getEmotionModel().getEmotion(returnMood.getEmotionId()).getName())) {
                    position = Integer.parseInt(entry.getKey());
                }
            }
            emotionsSpinner.setAdapter(emotionsSpinnerAdapter);
            emotionsSpinner.setSelection(position);
            for (Map.Entry<String, SocialSituation> entry : mApplication.getSocialSituationModel().getSocialSituations().entrySet()) {
                if (entry.getValue().getName().equals(mApplication.getSocialSituationModel().getSocialSituation(returnMood.getSocialSituationId()).getName())) {
                    position = Integer.parseInt(entry.getKey());
                }
            }
            socialSpinner.setAdapter(socialSpinnerAdapter);
            socialSpinner.setSelection(position);
            trigger.setText(returnMood.getTrigger());
            txtDate.setText(returnMood.getDate().toString());

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
                locationService = new LocationService(AddMoodActivity.this);
                if (locationService.canGetLocation()) {
                    longitude = locationService.getLongitude();
                    latitude = locationService.getLatitude();
                    addedLocation.setText("Added!");
                }
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alert = new AlertDialog.Builder(AddMoodActivity.this).create();
                // Date Field code taken from http://stackoverflow.com/questions/39051210/how-to-give-input-date-field-for-registration-form-in-android
                final Calendar myCalendar = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, day);
                        String myFormat = "yyyy-MM-dd";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        //String value1 = sdf.format(myCalendar.getTime());
                    }
                };
                final Calendar c = Calendar.getInstance();

                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(AddMoodActivity.this, android.R.style.Theme_Holo_Panel,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox


                                if (year < mYear)
                                    view.updateDate(mYear,mMonth,mDay);

                                if (monthOfYear < mMonth && year == mYear)
                                    view.updateDate(mYear,mMonth,mDay);

                                if (dayOfMonth < mDay && year == mYear && monthOfYear == mMonth)
                                    view.updateDate(mYear,mMonth,mDay);
                                String strMonth = String.format("%02d", monthOfYear+1);
                                String strDay = String.format("%02d", dayOfMonth);
                                String value = (year + "-" + strMonth + "-" + strDay);

                                txtDate.setText(value);
                                String myFormat = "yyyy-MM-dd";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                try {
                                    curDate = sdf.parse(value);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }



                            }
                        }, mYear, mMonth, mDay);

                dpd.getDatePicker().setMinDate(1973);
                dpd.show();
            }
        });

        if (editCheck == 1) {
            Button delete = (Button) findViewById(R.id.delete_button);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: delete mood
                    mApplication.getMoodLinkedList().remove(oldMoodIndex);
                    mApplication.getMoodModel().deleteMood(returnMood);
                    finish();
                }
            });
        }

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 0);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Need to actually save the information that is entered

                if (editCheckB.getInt("editCheck", -1) == 1) {
                    try {
                        returnMood.setEmotionId(String.valueOf(emotionId));
                        returnMood.setDate(curDate);
                        returnMood.setEmotionId(String.valueOf(emotionId));
                        returnMood.setSocialSituationId(String.valueOf(socialId));
                        returnMood.setTrigger(trigger.getText().toString());
                        //TODO: need to find a way to get latitude and longitude from the location setter
                        mApplication.getMoodLinkedList().set(oldMoodIndex, returnMood);

                        mApplication.getMoodModel().updateMood(returnMood);
                        mApplication.getMoodModel().getCachedMoods().set(oldMoodIndex, returnMood);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                }
                else {
                    // added emoticon parameter to Mood class to store the r.drawable of the selected emotion
                    returnMood = new Mood(latitude, longitude, trigger.getText().toString(), String.valueOf(emotionId), String.valueOf(socialId), imageTriggerId, new Date(), userId);
                    //returnMood.setDate(curDate);
                    returnMood.setEmotionId(String.valueOf(emotionId));
                    returnMood.setSocialSituationId(String.valueOf(socialId));

                    mApplication.getMoodLinkedList().add(returnMood);

                    //Mood mood = new Mood(12.22, 13.22, "Trigger", "1", "77", "22", new Date(12 - 12 - 2016), userId);
                    mApplication.getMoodModel().addMood(returnMood);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageBitmap = (Bitmap)data.getExtras().get("data");
        cameraImage.setImageBitmap(imageBitmap);
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        locationService.stopUsingGPS();
    }
}

