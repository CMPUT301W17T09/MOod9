package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * View showing you the details of a mood
 * @author CMPUT301W17T09
 */
public class MoodViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_view);

        int position = getIntent().getExtras().getInt("moodIndex");
        Mood9Application mApplication = (Mood9Application) getApplication();
        Mood mood = mApplication.getMoodLinkedList().get(position);

        SocialSituationModel socialSituationModel = mApplication.getSocialSituationModel();
        EmotionModel emotionModel = mApplication.getEmotionModel();
        SocialSituation ss = socialSituationModel.getSocialSituation(mood.getSocialSituationId());
        Emotion e = emotionModel.getEmotion(mood.getEmotionId());

        ImageView emoticon = (ImageView) findViewById(R.id.emotion_image);
        TextView emotion = (TextView) findViewById(R.id.emotion_name);
        TextView date = (TextView) findViewById(R.id.date);
        TextView username = (TextView) findViewById(R.id.username);
        TextView trigger = (TextView) findViewById(R.id.trigger);
        TextView social_situation = (TextView) findViewById(R.id.social_situation);
        ImageView viewMoodImage = (ImageView) findViewById(R.id.viewMoodImage);

        int resID = getResources().getIdentifier(e.getName().toLowerCase().trim() , "drawable", getPackageName());
        viewMoodImage.setImageBitmap(StringToBitMap(mood.getImage()));
        emoticon.setImageResource(resID);
        emotion.setText(e.getName());
        social_situation.setText(ss.getName());
        date.setText(mood.getDate().toString());
        trigger.setText(mood.getTrigger());
        username.setText(UserModel.getUserProfile(mood.getUser_id()).getName());

        Button edit = (Button) findViewById(R.id.edit);
        ImageButton follow = (ImageButton) findViewById(R.id.follow);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String user_id = sharedPreferences.getString("user_id", "TESTER IN MAIN");
        User current = UserModel.getUserProfile(mood.getUser_id());
        User k_user = UserModel.getUserProfile(user_id);

        if (mood.getUser_id().equals(user_id)) {
            // If the mood is a mood of the users show the edit button
            follow.setVisibility(View.GONE);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //for list item clicked
                    Intent editMoodIntent = new Intent(MoodViewActivity.this, AddMoodActivity.class);
                    editMoodIntent.putExtra("editCheck", 1);
                    editMoodIntent.putExtra("moodIndex", position);
                    startActivityForResult(editMoodIntent, 1);
                }
            });
        } else {
            // If the mood is not of the users then show the follow button
            edit.setVisibility(View.INVISIBLE);
            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    int duration = Toast.LENGTH_SHORT;
                    Context context = getApplicationContext();
                    //If you are already following the user
                    if(k_user.getFollowees().contains(UserModel.getUserProfile(user_id))){
                        Toast toast2 = Toast.makeText(context, "You are already following this user!", duration);
                        toast2.show();
                    }
                    else {
                        if(current.getRequests().contains(user_id)){
                            Toast toast3 = Toast.makeText(context, "Request already sent!", duration);
                            toast3.show();
                        }
                        else {
                            current.addToRequests(user_id);
                            CharSequence text = "Follow request sent!";
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            UserModel.updateUser(current);
                        }
                    }
                }
            });

        }
    }


    //http://androidtrainningcenter.blogspot.in/2012/03/how-to-convert-string-to-bitmap-and.html
    //Found on March 23, 2017
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    // Code Documentation found here: https://developer.android.com/reference/android/app/Activity.html
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
    }
}
