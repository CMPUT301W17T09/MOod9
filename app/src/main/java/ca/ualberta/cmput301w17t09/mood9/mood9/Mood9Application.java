package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * Created by dannick on 3/5/17.
 */

public class Mood9Application extends Application {
    private EmotionModel emotionModel;
    private SocialSituationModel socialSituationModel;
    private MoodModel moodModel;
    private LinkedList<Mood> moodLinkedList;

    public EmotionModel getEmotionModel() {
        return emotionModel;
    }

    public SocialSituationModel getSocialSituationModel() {
        return socialSituationModel;
    }

    public MoodModel getMoodModel() {
        return moodModel;
    }

    public LinkedList<Mood> getMoodLinkedList() {
        return moodLinkedList;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        // this method fires once as well as constructor
        // but also application has context here
        File MoodDir = getDir("moods",0);
        MoodDir.mkdir();
        File addedmoods = new File(MoodDir,"addedmoods.sav");
        File deletedmoods = new File(MoodDir,"deletedmoods.sav");

        try{
            addedmoods.createNewFile();
            deletedmoods.createNewFile();
        }
        catch(IOException e){
            System.out.print("file not created");
            throw new RuntimeException();
        }


        InputStream emotionsStream = this.getResources().openRawResource(R.raw.emotions);
        InputStream socialSituationsStream = this.getResources().openRawResource(R.raw.social_situations);
        this.emotionModel = new EmotionModel(emotionsStream);
        this.socialSituationModel = new SocialSituationModel(socialSituationsStream);
        this.moodModel = new MoodModel(addedmoods,deletedmoods);
        this.moodLinkedList = new LinkedList<Mood>();
    }
}
