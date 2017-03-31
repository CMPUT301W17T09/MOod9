package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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

import java.text.DateFormat;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static android.R.attr.bitmap;
import static android.R.attr.datePickerDialogTheme;
import static android.R.attr.targetActivity;
import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;

/**
 * Originally created by Fady
 * Save returns data through intent extras implemented by cdkushni 3/5/17
 * Changed to implement AdapterView.OnItemSelectedListener by cdkushni on 3/8/17
 * Fixed to use mood model, grabbing emotion model and socialmodel data for spinners and reloading other edit details from passed in mood by cdkushni on 3/10/17
 * Modified by cdkushni on 3/20/17, fixed some bugs with shared preferences that came up whenever a new account was made and implemented a new version of the location
 * service, not tested yet.
 * Modified by cdkushni on 3/20/17 gps location grabbing is now working, network location grabbing is still not working for whatever reason.
 * Encapsulated some parts from the on create function to make it a bit cleaner
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
    static Date curDate = new Date();
    String imageTriggerId = "N/A";
    String selectedEmotion = "Anger";
    String userId = "newUser";

    private int mYear, mMonth, mDay;


    ImageView cameraImage;
    Bitmap imageBitmap = null;
    String imageString;

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

        checkLocationPermissions();

        mApplication = (Mood9Application)getApplicationContext();
        Intent thisIntent = getIntent();
        editCheckB = thisIntent.getExtras();
        int editCheck = editCheckB.getInt("editCheck", 0);
        if (editCheck == 1) {
            setContentView(R.layout.activity_edit_mood);
            oldMoodIndex = editCheckB.getInt("moodIndex");
        }
        else {
            setContentView(R.layout.activity_add_mood);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPreferences.getString("username", "test");
        userId = UserModel.getUserID(userName);

        cameraImage  = (ImageView) findViewById(R.id.cameraImage);
        ImageButton cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        Spinner emotionsSpinner = (Spinner) findViewById(R.id.emotions_spinner);
        Spinner socialSpinner = (Spinner) findViewById(R.id.social_spinner);
        final EditText trigger = (EditText) findViewById(R.id.trigger_edittext);
        Button addLocation = (Button) findViewById(R.id.button);
        Button save = (Button) findViewById(R.id.button2);
        Button calendar = (Button) findViewById(R.id.calendar);
        final TextView txtDate = (TextView) findViewById(R.id.curDate);

        spinnerDateInit();
        spinnerInit(editCheck, trigger, txtDate, emotionsSpinner, socialSpinner);

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationService = new LocationService(AddMoodActivity.this);
                if (locationService.canGetLocation()) {
                    longitude = locationService.getLongitude();
                    latitude = locationService.getLatitude();
                }
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() { // TODO: replace this datepicker code with more concise code like dannicks
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "DatePicker");
            }
        });

        if (editCheck == 1) {
            Button delete = (Button) findViewById(R.id.delete_button);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String newDate = dateFormat.format(curDate);
                if (triggerVerify(trigger.getText().toString()) == 1) {
                    if (editCheckB.getInt("editCheck", -1) == 1) {
                        try {
                            returnMood.setDate(newDate);
                            returnMood.setEmotionId(String.valueOf(emotionId));
                            returnMood.setSocialSituationId(String.valueOf(socialId));
                            returnMood.setTrigger(trigger.getText().toString());
                            returnMood.setLatitude(latitude);
                            returnMood.setLongitude(longitude);
                            if (imageString != null) {
                                returnMood.setImage(imageString);
                            }
                            mApplication.getMoodLinkedList().set(oldMoodIndex, returnMood);

                            mApplication.getMoodModel().updateMood(returnMood);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            finish();
                        }
                    } else {
                        // added emoticon parameter to Mood class to store the r.drawable of the selected emotion
                        if (imageString == null) {
                            returnMood = new Mood(latitude, longitude, trigger.getText().toString(),
                                    String.valueOf(emotionId), String.valueOf(socialId),
                                    imageTriggerId, newDate, userId, null);
                        } else {
                            returnMood = new Mood(latitude, longitude, trigger.getText().toString(),
                                    String.valueOf(emotionId), String.valueOf(socialId),
                                    imageTriggerId, newDate, userId, imageString);
                        }
                        returnMood.setEmotionId(String.valueOf(emotionId));
                        returnMood.setSocialSituationId(String.valueOf(socialId));

                        mApplication.getMoodLinkedList().add(returnMood);
                        mApplication.getMoodModel().addMood(returnMood);
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Trigger must be of maximum 20 characters and no more than 3 words",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // https://developer.android.com/guide/topics/ui/controls/pickers.html
    // Accessed 2017-01-29
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            //return new DatePickerDialog(getActivity(), this, year, month, day);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    android.R.style.Theme_Holo_Panel, this, year, month, day);
            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            curDate = c.getTime();
            AddMoodActivity thisActivity = (AddMoodActivity) getActivity();
            thisActivity.setDateText(String.format("%1$tY-%1$tm-%1$td", c));
        }
    }

    public void setDateText(String text){
        TextView dateEditText = (TextView) findViewById(R.id.curDate);
        dateEditText.setText(text);
    }

    private int triggerVerify(String triggerText) {
        if (triggerText.length() > 20) {
            return 0;
        }
        if (triggerText.split(" ").length > 3) {
            return 0;
        }
        return 1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageBitmap = (Bitmap)data.getExtras().get("data");
        cameraImage.setImageBitmap(imageBitmap);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        Bitmap compressedImage = null;
        int maxImageSize = 65536;
        int bitmapByteCount = BitmapCompat.getAllocationByteCount(imageBitmap);

        if(bitmapByteCount > maxImageSize){
            final BitmapFactory.Options options = new BitmapFactory.Options();
            double compressionRatio = ((double)bitmapByteCount / (double)maxImageSize);
            double compressionSize = sqrt(compressionRatio);
            options.inSampleSize = (int) ceil(compressionSize);
            compressedImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        }

        compressedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        System.out.println(compressedImage.getAllocationByteCount());
        imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Spinner spinner = (Spinner) arg0;
        if (spinner.getId() == R.id.emotions_spinner) {
            //Toast.makeText(getApplicationContext(), emotions[position], Toast.LENGTH_SHORT).show();
            emotionId = position;
            selectedEmotion = emotions[position];
            selectedEmote = emoticons[position];
        }
        else if (spinner.getId() == R.id.social_spinner) {
            //Toast.makeText(getApplicationContext(), socials[position], Toast.LENGTH_SHORT).show();
            socialId = position;
            selectedSocial = socials[position];
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(AddMoodActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_FINE);
            } else {
                //Request the location permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_FINE);
            }
        }
    }

    private void spinnerDateInit() {
        emoticons = new int[mApplication.getEmotionModel().getEmotions().size()];
        emotions = new String[mApplication.getEmotionModel().getEmotions().size()];
        for (Map.Entry<String, Emotion> entry : mApplication.getEmotionModel()
                .getEmotions().entrySet()) {
            //String imgNameBuilder = entry.getValue().getName().toLowerCase() + ".png";
            String imgNameBuilder = entry.getValue().getImageName();
            emoticons[Integer.parseInt(entry.getKey())] = getResources()
                    .getIdentifier(imgNameBuilder.substring(0,
                            imgNameBuilder.lastIndexOf(".")), "drawable", getPackageName());
            emotions[Integer.parseInt(entry.getKey())] = entry.getValue().getName();
        }
        socials = new String[mApplication.getSocialSituationModel().getSocialSituations()
                .entrySet().size()];

        for (Map.Entry<String, SocialSituation> entry : mApplication.getSocialSituationModel()
                .getSocialSituations().entrySet()) {
            socials[Integer.parseInt(entry.getKey())] = entry.getValue().getName();
        }
    }

    private void spinnerInit(int editCheck, EditText trigger, TextView txtDate,
                             Spinner emotionsSpinner, Spinner socialSpinner) {
        EmotionsSpinnerAdapter emotionsSpinnerAdapter = new EmotionsSpinnerAdapter
                (getApplicationContext(), emoticons, emotions);

        ArrayAdapter<String> socialSpinnerAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_dropdown_item, socials);
        if (editCheck == 1) {
            returnMood = mApplication.getMoodLinkedList().get(oldMoodIndex);
            int position = 0;
            for (Map.Entry<String, Emotion> entry : mApplication.getEmotionModel()
                    .getEmotions().entrySet()) {
                if (entry.getValue().getName().equals(mApplication.getEmotionModel()
                        .getEmotion(returnMood.getEmotionId()).getName())) {
                    position = Integer.parseInt(entry.getKey());
                }
            }
            emotionsSpinner.setAdapter(emotionsSpinnerAdapter);
            emotionsSpinner.setSelection(position);
            for (Map.Entry<String, SocialSituation> entry : mApplication
                    .getSocialSituationModel().getSocialSituations().entrySet()) {
                if (entry.getValue().getName().equals(mApplication
                        .getSocialSituationModel()
                        .getSocialSituation(returnMood.getSocialSituationId()).getName())) {
                    position = Integer.parseInt(entry.getKey());
                }
            }
            socialSpinner.setAdapter(socialSpinnerAdapter);
            socialSpinner.setSelection(position);
            trigger.setText(returnMood.getTrigger());

            String myFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            txtDate.setText(sdf.format(curDate));

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
                //Toast.makeText(getApplicationContext(), socials[position], Toast.LENGTH_SHORT).show();
                socialId = position;
                selectedSocial = socials[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                socialId = 0;
                selectedSocial = socials[0];
            }
        });
    }

}

