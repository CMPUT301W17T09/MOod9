package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import static android.provider.Telephony.Mms.Part.FILENAME;

/**
 * Added array size getter by cdkushni on 3/10/2017
 * Redone by Rohit on 2017-03-08
 * Created by dannick on 2/22/17.
 */

/**
 * The MoodModel includes functionality to add, and delete moods and the ability to synchronize
 * with elastic search
 */
public class MoodModel {
    private static ArrayList<Mood> moods = new ArrayList<Mood>();
    private EmotionModel emodel;
    private SocialSituationModel smodel;
    private FileInputStream moodsOnFile;
    private static final String ADDEDNAME = "addedMoods.sav";
    private static final String DELETEDNAME = "deletedMoods.sav";
    ElasticSearchMOodController.GetMoodsTask getMoodsTask = new ElasticSearchMOodController.GetMoodsTask();
    ElasticSearchMOodController.AddMoodsTask addMoodsTask = new ElasticSearchMOodController.AddMoodsTask();
    ElasticSearchMOodController.UpdateMoodsTask UpdateMoodsTask = new ElasticSearchMOodController.UpdateMoodsTask();
    ElasticSearchMOodController.DeleteMoodTask deleteMoodTask = new ElasticSearchMOodController.DeleteMoodTask();
    /*String FILENAME = "hello_file";
String string = "hello world!";

FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
fos.write(string.getBytes());
fos.close();
*/

    /**
     * public MoodModel(EmotionModel emodel,SocialSituationModel smodel){
     * this.emodel = emodel;
     * this.smodel = smodel;
     * }
     */

    public void setMoodsArray() {
        // First load all moods on elastic search
        ElasticSearchMOodController.GetMoodsTask getMoodsTask = new ElasticSearchMOodController.GetMoodsTask();
        getMoodsTask.execute("");
        ArrayList<Mood> elasticmoods = new ArrayList<>(); //all moods on elasticsearch
        try {
            elasticmoods = getMoodsTask.get();
        } catch (Exception e) {
            Log.i("Error", "Can't get moods from ElasticSearch");
        }
        ArrayList<Mood> deleteMoods = readFromDeleted();
        ArrayList<Mood> fileMoods = readFromAdded();
        ArrayList<Mood> finalarr = new ArrayList<Mood>();  //
        ArrayList<Mood> finalarr2 = new ArrayList<Mood>();
        finalarr.addAll(elasticmoods);
        finalarr.addAll(fileMoods);
        for (int i = 0; i < finalarr.size(); i++) {
            if (!finalarr2.contains(finalarr.get(i))) {
                finalarr2.add(finalarr.get(i));
            }
        }
        finalarr2.removeAll(deleteMoods);
        deletefromfile();
        moods = finalarr2;
        //Subtract deletedMoods from elasticmoods, from filemoods
        //Load missing elastic onto file and vice versa
    }

    public ArrayList<Mood> getMoodByUser(String userid) {
        ArrayList<Mood> returnarr = new ArrayList<Mood>();
        for (int i = 0; i < moods.size(); i++) {
            if (moods.get(i).getUser_id().equals(userid)) {
                returnarr.add(moods.get(i));
            }
        }
        return returnarr;

    }

    /**
     * @param user
     * @return latest mood of every person the user is following
     */
    public ArrayList<Mood> getFollowedMoods(User user) {
        ArrayList<Mood> returnarr = new ArrayList<Mood>();
        for (int i = 0; i < moods.size(); i++) {
            if (user.getFollowing().contains(moods.get(i).getUser())) {
                returnarr.add(moods.get(i));
            }
        }
        return returnarr;
    }

    public ArrayList<Mood> getUniversalMoods() {
        setMoodsArray();
        return moods;
    }

    public ArrayList<Mood> getMoodsNear(Double latitude, Double longitude) {
        // TODO
        return moods;
    }


    public void updateMood(String oldMoodId, Mood updatedMood) {
        // TODO
    }

    public int getMoodModelSize() {
        return moods.size();
    }

    /**
     * Adds the given mood to elastic search and to the addMood file in a gson format
     *
     * @param mood
     */
    public void addMood(Mood mood) {
        ElasticSearchMOodController.AddMoodsTask addMoodsTask = new ElasticSearchMOodController.AddMoodsTask();
        addMoodsTask.execute(mood); // add to elastic search
        Gson gson = new Gson();
        String mjs = gson.toJson(mood);
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(ADDEDNAME));
            br.write(mjs);
            br.flush();
        } catch (IOException e) {
            System.out.println("File not found");
        }
        setMoodsArray();
    }

    public void deleteMood(Mood mood) {
        Gson gson = new Gson();
        String mjs = gson.toJson(mood);
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(DELETEDNAME));
            br.write(mjs);
            br.flush();
        } catch (IOException e) {
            System.out.println("File not found");
        }
        setMoodsArray();
    }


    private ArrayList<Mood> readFromAdded() {
        ArrayList<Mood> loaded = new ArrayList<Mood>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(ADDEDNAME));
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Mood>>() {
            }.getType();
            loaded = gson.fromJson(br, listType);
        } catch (IOException e) {
            System.out.println("file not found");
        }
        return loaded;
    }
    //Code taken from http://stackoverflow.com/questions/12384064/gson-convert-from-json-to-a-typed-arraylistt Sept.22,2016

    private ArrayList<Mood> readFromDeleted() {
        ArrayList<Mood> deleted = new ArrayList<Mood>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(DELETEDNAME));
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Mood>>() {
            }.getType();
            deleted = gson.fromJson(br, listType);
        } catch (IOException e) {
            System.out.println("file not found");
        }
        return deleted;
    }

    private void deletefromfile() {
        try {
            FileOutputStream writer = new FileOutputStream(DELETEDNAME);
            writer.write(("").getBytes());
            writer.close();
        } catch (IOException e) {
            System.out.println("file not found");
        }
    }
}
