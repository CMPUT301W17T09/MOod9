package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.app.Application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * Mood9 Application Context
 * Contains all our models.
 * @author CMPUT301W17T09
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

    /**
     * Initializes all our models on application creation.
     */
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
            Boolean created = addedmoods.createNewFile();
            Boolean created2 = deletedmoods.createNewFile();
            Boolean myBool = created & created2;
            BufferedReader br = new BufferedReader(new FileReader(addedmoods));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }

        catch(IOException e){
            System.out.print("file not created");
            throw new RuntimeException();
        }
        if (addedmoods.exists()){
            System.out.println("exists 200");
        }

        InputStream emotionsStream = this.getResources().openRawResource(R.raw.emotions);
        InputStream socialSituationsStream = this.getResources().openRawResource(R.raw.social_situations);
        this.emotionModel = new EmotionModel(emotionsStream);
        this.socialSituationModel = new SocialSituationModel(socialSituationsStream);
        this.moodModel = new MoodModel(addedmoods,deletedmoods);
        this.moodLinkedList = new LinkedList<Mood>();
    }
}
