package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;

/**
 * Deals with I/O and saving of new/edited moods
 * <p>
 * Originally created by Fady
 * Save returns data through intent extras implemented by cdkushni 3/5/17
 * Changed to implement AdapterView.OnItemSelectedListener by cdkushni on 3/8/17
 * Fixed to use mood model, grabbing emotion model and socialmodel data for spinners
 * and reloading other edit details from passed in mood by cdkushni on 3/10/17
 * Modified by cdkushni on 3/20/17, fixed some bugs with shared preferences that came
 * up whenever a new account was made and implemented a new version of the location
 * service, not tested yet.
 * Modified by cdkushni on 3/20/17 gps location grabbing is now working, network location
 * grabbing is still not working for whatever reason.
 * Encapsulated some parts from the on create function to make it a bit cleaner
 * Modified by cdkushni on 4/2/17
 * Fixed lack of OO practices and removed unnecessary setter variables and instead just set returnMood
 * when setting values. Also removed unnecessary global variables.
 * </p>
 *
 * @author fady
 * @author cdkushni
 * @version 1.7
 * @see FeedActivity
 * @see Mood9Application
 * @see LocationService
 * @since 1.0
 */
public class AddMoodActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Mood9Application mApplication;
    private static Mood returnMood;

    private ImageView cameraImage;

    private int oldMoodIndex = 0;

    private static final int REQUEST_PERMISSION_FINE = 0;
    private LocationService locationService;

    /**
     * OnCreate method, handles Initialization of activity, including:
     * deciding if we are editing an old mood or creating a new mood
     * Initializing click listeners and view objects
     * @param savedInstanceState : instance bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkLocationPermissions();

        mApplication = (Mood9Application)getApplicationContext();
        Intent thisIntent = getIntent();
        Bundle editCheckB = thisIntent.getExtras();
        int editCheck = editCheckB.getInt("editCheck", 0);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPreferences.getString("username", "test");
        String userId;
        try {
            userId = UserModel.getUserID(userName);
        } catch (Exception e) {
            userId = sharedPreferences.getString("userId", "idNotFoundDefaulter");
            if (mApplication.getMoodModel().getCurrentUserMoods().size() > 0) {
                userId = mApplication.getMoodModel().getCurrentUserMoods().get(0).getUser_id();
            }
        }

        /**
         * If this is an edit then get the index of the old mood and set to the edit layout
         * If this is a new mood then create a new mood and set to the add layout
         */
        if (editCheck == 1) {
            setContentView(R.layout.activity_edit_mood);
            oldMoodIndex = editCheckB.getInt("moodIndex");

        } else {
            setContentView(R.layout.activity_add_mood);
            returnMood = new Mood(53.5, 113.5, "",
                    "0", "0", "N/A", "2017-04-01T00:00:00", userId, null);
        }

        /**
         * Initialize all needed view objects
         */
        Button customLocation = (Button) findViewById(R.id.custom_location);
        cameraImage  = (ImageView) findViewById(R.id.cameraImage);
        ImageButton cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        Spinner emotionsSpinner = (Spinner) findViewById(R.id.emotions_spinner);
        Spinner socialSpinner = (Spinner) findViewById(R.id.social_spinner);
        final EditText trigger = (EditText) findViewById(R.id.trigger_edittext);
        Button addLocation = (Button) findViewById(R.id.button);
        Button save = (Button) findViewById(R.id.button2);
        Button calendar = (Button) findViewById(R.id.calendar);
        final TextView txtDate = (TextView) findViewById(R.id.curDate);

        /**
         * Initialize the fields with their values varied if they are an old mood vs a new mood
         */
        spinnerDateInit(editCheck, trigger, txtDate, emotionsSpinner, socialSpinner);

        /**
         * Sets getting a custom location listener
         */
        customLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent customLocationIntent = new Intent(AddMoodActivity.this
                        , CustomLocation.class);
                startActivityForResult(customLocationIntent, 1);
            }
        });

        /**
         * sets adding the current location listener
         */
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationService = new LocationService(AddMoodActivity.this);
                if (locationService.canGetLocation()) {
                    returnMood.setLongitude(locationService.getLongitude());
                    returnMood.setLatitude(locationService.getLatitude());
                }
            }
        });

        /**
         * sets opening the date picker listener
         */
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "DatePicker");
            }
        });

        /**
         * sets the delete button listener if we know this is an old mood
         */
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

        /**
         * sets the camera button listener
         */
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 0);
            }
        });

        /**
         * sets the save button listener
         * If the trigger entered is not the correct format, don't save and make a toast to prompt
         * if the trigger is valid then saves the trigger to the return mood and updates elasticsearch
         * and local with the new/edited mood
         */
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (triggerVerify(trigger.getText().toString()) == 1) {
                    if (editCheckB.getInt("editCheck", -1) == 1) {
                        try {
                            returnMood.setTrigger(trigger.getText().toString());
                            mApplication.getMoodLinkedList().set(oldMoodIndex, returnMood);
                            mApplication.getMoodModel().updateMood(returnMood);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            finish();
                        }
                    } else {
                        returnMood.setTrigger(trigger.getText().toString());
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

    /**
     * https://developer.android.com/guide/topics/ui/controls/pickers.html
     * Accessed 2017-03-30
     */
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
            AddMoodActivity thisActivity = (AddMoodActivity) getActivity();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String newDate = dateFormat.format(c.getTime());
            returnMood.setDate(newDate);
            thisActivity.setDateText(String.format("%1$tY-%1$tm-%1$td", c));
        }
    }

    public void setDateText(String text){
        TextView dateEditText = (TextView) findViewById(R.id.curDate);
        dateEditText.setText(text);
    }

    /**
     * Verifies trigger length when saving
     * @param triggerText : the current trigger text
     * @return boolean whether it is a valid trigger or not
     */
    private int triggerVerify(String triggerText) {
        if (triggerText.length() > 20) {
            return 0;
        }
        if (triggerText.split(" ").length > 3) {
            return 0;
        }
        return 1;
    }

    /**
     * Sets image when picture is taken
     * @param requestCode : request code request
     * @param resultCode : resulting code request
     * @param data : intent data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                cameraImage.setImageBitmap(imageBitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                Bitmap compressedImage = null;
                int maxImageSize = 65536;
                int bitmapByteCount = BitmapCompat.getAllocationByteCount(imageBitmap);

                if (bitmapByteCount > maxImageSize) {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    double compressionRatio = ((double) bitmapByteCount / (double) maxImageSize);
                    double compressionSize = sqrt(compressionRatio);
                    options.inSampleSize = (int) ceil(compressionSize);
                    compressedImage = BitmapFactory.decodeByteArray(byteArray, 0
                            , byteArray.length, options);
                }

                compressedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                //imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);
                returnMood.setImage(Base64.encodeToString(byteArray, Base64.DEFAULT));
            }
            if (requestCode == 1) {
                returnMood.setLongitude((double) data.getExtras().get("lon"));
                returnMood.setLatitude((double) data.getExtras().get("lat"));
            }
        }
    }

    //Performing action onItemSelected and onNothing selected

    /**
     * When a spinner item is selected, set the return mood parameter to it
     * @param arg0 : argument
     * @param arg1 : argument
     * @param position : position of selection
     * @param id : id
     */
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Spinner spinner = (Spinner) arg0;
        if (spinner.getId() == R.id.emotions_spinner) {
            returnMood.setEmotionId(String.valueOf(position));
        }
        else if (spinner.getId() == R.id.social_spinner) {
            returnMood.setSocialSituationId(String.valueOf(position));
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    /**
     * check location permission for location getting
     */
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

    /**
     * Initiates spinners with Emotion and SocialSituation model data
     * @param editCheck : edit check for spinnerInit data checking
     * @param trigger : trigger data for spinnerInit data init
     * @param txtDate : Date text for spinnerInit data init
     * @param emotionsSpinner : emotionsSpinner to be init
     * @param socialSpinner : socialSpinner to be init
     */
    private void spinnerDateInit(int editCheck, EditText trigger, TextView txtDate,
                                 Spinner emotionsSpinner, Spinner socialSpinner) {
        int[] emoticons = new int[mApplication.getEmotionModel().getEmotions().size()];
        String[] emotions = new String[mApplication.getEmotionModel().getEmotions().size()];
        for (Map.Entry<String, Emotion> entry : mApplication.getEmotionModel()
                .getEmotions().entrySet()) {
            String imgNameBuilder = entry.getValue().getImageName();
            emoticons[Integer.parseInt(entry.getKey())] = getResources()
                    .getIdentifier(imgNameBuilder.substring(0,
                            imgNameBuilder.lastIndexOf(".")), "drawable", getPackageName());
            emotions[Integer.parseInt(entry.getKey())] = entry.getValue().getName();
        }
        String[] socials = new String[mApplication.getSocialSituationModel().getSocialSituations()
                .entrySet().size()];

        for (Map.Entry<String, SocialSituation> entry : mApplication.getSocialSituationModel()
                .getSocialSituations().entrySet()) {
            socials[Integer.parseInt(entry.getKey())] = entry.getValue().getName();
        }
        spinnerInit(editCheck, trigger, txtDate, emotionsSpinner, socialSpinner
                , emoticons, emotions, socials);
    }

    /**
     * Initialization of all views with either default values or parameters if this is an old mood
     * @param editCheck : old mood check
     * @param trigger : trigger value
     * @param txtDate : Date Value
     * @param emotionsSpinner : Emotions spinner
     * @param socialSpinner : Social Situation spinner
     * @param emoticons : emoticons for init
     * @param emotions : emotions for init
     * @param socials : socials for init
     */
    private void spinnerInit(int editCheck, EditText trigger, TextView txtDate,
                             Spinner emotionsSpinner, Spinner socialSpinner,
                             int[] emoticons, String[] emotions, String[] socials) {
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

            txtDate.setText(returnMood.getDate().split("T")[0]);
        }
        else {
            emotionsSpinner.setAdapter(emotionsSpinnerAdapter);
            socialSpinner.setAdapter(socialSpinnerAdapter);
        }
        emotionsSpinner.setOnItemSelectedListener(this);
        socialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                returnMood.setSocialSituationId(String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                returnMood.setSocialSituationId("0");
            }
        });
    }

}

