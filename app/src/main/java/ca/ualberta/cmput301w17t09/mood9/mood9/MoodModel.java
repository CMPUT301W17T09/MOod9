package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Context;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ArrayListMultimap;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Redone by Rohit on 2017-03-08
 * Created by dannick on 2/22/17.
 */

/**
 * The MoodModel includes functionality to add, and delete moods and the ability to synchronize
 * with elastic search
 */
public class MoodModel {
    private ArrayList<Mood> moods = new ArrayList<Mood>();
    private EmotionModel emodel;
    private SocialSituationModel smodel;
    private String moodsOnFile;
    private String deletedMoods;
    /*String FILENAME = "hello_file";
String string = "hello world!";

FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
fos.write(string.getBytes());
fos.close();
*/

    public MoodModel(EmotionModel emodel,SocialSituationModel smodel, String filename1, String filename2){
        this.emodel = emodel;
        this.smodel = smodel;
        this.moodsOnFile = filename1;
        this.deletedMoods = filename2;
        try {
            FileInputStream fos = openFileInput(moodsOnFile);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
            //Code taken from http://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylistt Sept.22,2016
            Type listType = new TypeToken<ArrayList<NormalTweet>>(){}.getType();
            tweetList = gson.fromJson(in, listType);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            tweetList = new ArrayList<NormalTweet>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }        FileOutputStream fos2 = openFileOutput(filename2, Context.MODE_PRIVATE);


    }


    /**
     * Returns a mood given the id, returns null if the mood is not found
     * @param id
     * @return mood
     */
    public Mood getMood(String id){
        Mood m = null;
        for (int i = 0; i < moods.size(); i++){
            if(moods.get(i).getId().equals(id)){
                m = moods.get(i);
            }
        }
        return m;
    }

    public void setMoodsArray() {
        // First load all moods on elastic search
        ArrayList<Mood> elasticmoods = new ArrayList<Mood>();
        ArrayList<Mood> filemoods = new ArrayList<Mood>();;
        ArrayList<Mood> deleteMoods = new ArrayList<Mood>();
        filemoods.removeAll(deleteMoods);
        elasticmoods.removeAll(deleteMoods);
        ArrayList<Mood> finalarr =  new ArrayList<Mood>();  //
        finalarr.addAll(elasticmoods);
        finalarr.addAll(filemoods);
        //Subtract deletedMoods from elasticmoods, from filemoods
        //Load missing elastic onto file and vice versa
    }

    public ArrayList<Mood> getAllMoods(){
        return moods;
    }


    public ArrayList<Mood> getMoodByUser(String userid){
        ArrayList<Mood> returnarr = new ArrayList<Mood>();
        for (int i = 0; i < moods.size(); i++){
            if(moods.get(i).getUser_id().equals(userid)){
                returnarr.add(moods.get(i));
            }
        }
        return returnarr;

    }

    public ArrayList<Mood> getFollowedMoods(User user){
        // User class can have a list of followers, for each follower, get their moods from the moods
        //arraylist
        return moods;
    }

    public ArrayList<Mood> getUniversalMoods(){
        setMoodsArray();
        return moods;
    }

    public ArrayList<Mood> getMoodsNear(Double latitude, Double longitude){
        // TODO
        return moods;
    }

    public ArrayList<Mood> getMood

    /**
     * Call setMoodsArray() at the end
     * @param mood
     */
    public void deleteMood(Mood mood){

    }

    public void updateMood(Mood mood){

    }
    public void addMood(Mood mood){
        //push mood onto elasticsearch

        setMoodsArray();
    }
}
