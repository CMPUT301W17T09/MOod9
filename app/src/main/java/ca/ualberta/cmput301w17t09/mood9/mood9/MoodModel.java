package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
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
import com.google.gson.stream.JsonReader;

import static android.provider.Telephony.Mms.Part.FILENAME;

/**
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
    File ADDEDNAME = new File("addedMoods.sav");
    File DELETEDNAME = new File("deletedMoods.sav");
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
        getMoodsTask.execute(""); //gets all moods
        ArrayList<Mood> elasticmoods = new ArrayList<>(); //all moods on elasticsearch
        try {
            elasticmoods = getMoodsTask.get();
        } catch (Exception e) {
            Log.i("Error", "Can't get moods from ElasticSearch");
        }
        ArrayList<Mood> deleteMoods = readFromDeleted();
        ArrayList<Mood> fileMoods = readFromAdded(); //Last set of universal moods PLUS any offline moods
        ArrayList<Mood> finalarr = new ArrayList<Mood>();  //
        ArrayList<Mood> finalarr2 = new ArrayList<Mood>();
        finalarr.addAll(elasticmoods); //Add everythingon elastic search
        finalarr.addAll(fileMoods); // final array contains all moods form elastic and add moods
        for (int i = 0; i < finalarr.size(); i++) {
            if (!finalarr2.contains(finalarr.get(i))) {
                finalarr2.add(finalarr.get(i));
            }
        } //finalarr2 has unique set of moods
        finalarr2.removeAll(deleteMoods); //remove all moved pending for deletion from finalarr
        ElasticSearchMOodController.UpdateMoodsTask updateMoodsTask = new ElasticSearchMOodController.UpdateMoodsTask();
        ElasticSearchMOodController.DeleteMoodTask deleteMoodTask = new ElasticSearchMOodController.DeleteMoodTask();
        for (int i = 0; i < deleteMoods.size(); i++) {
            if (!elasticmoods.contains(deleteMoods.get(i))) {
                deleteMoodTask.execute(deleteMoods.get(i)); //mood deleted from elastic search
            }
        }

        deletefromfile(); //no more deleted modos
        try {
            FileOutputStream writer = new FileOutputStream(ADDEDNAME);
            writer.write(("").getBytes());
            writer.close();
        } catch (IOException e) {
            System.out.println("file not found1");
        }
        moods = finalarr2;
        saveListToFile();
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


    public void updateMood(Mood mood) {

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
        ArrayList<Mood> mjs1 = new ArrayList<Mood>();
        mjs1.add(mjs);
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(ADDEDNAME));
            System.out.println(mjs);
            gson.toJson(mjs1, br);
            //wrties gson rep of mood to file
            br.flush();
        } catch (IOException e) {
            System.out.println("File not found2");
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
            System.out.println("File not found3");
        }
        setMoodsArray();
    }

    /**
     *
     * @return arraylist of moods in the addedname file
     */
    private ArrayList<Mood> readFromAdded() {
        ArrayList<Mood> loaded = new ArrayList<Mood>();
        try {
            JsonReader br = new JsonReader(new FileReader(ADDEDNAME));
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


    /**
     *
     * @return arraylist of moods from the deletedname file
     */
    private ArrayList<Mood> readFromDeleted() {
        ArrayList<Mood> deleted = new ArrayList<Mood>();
        try {
            DELETEDNAME.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader(DELETEDNAME));
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Mood>>() {
            }.getType();
            deleted = gson.fromJson(br, listType);
        } catch (IOException e) {
            System.out.println("file not found3");
        }
        return deleted;
    }

    /**
     * Deletes all tweets form the deleted tweets file
     */
    private void deletefromfile() {
        try {
            FileOutputStream writer = new FileOutputStream(DELETEDNAME);
            writer.write(("").getBytes());
            writer.close();
        } catch (IOException e) {
            System.out.println("file not found");
        }
    }
    //http://stackoverflow.com/questions/24573598/write-arraylist-of-custom-objects-to-file
    public int saveListToFile() {
        try {
            BufferedWriter buffWriter = new BufferedWriter(new FileWriter(ADDEDNAME, true));
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Mood>>() {}.getType();
            String json = gson.toJson(moods, type);
            buffWriter.append(json);
            buffWriter.newLine();
            buffWriter.close();
        } catch (IOException e) {
            return -1;
        }
        return 0;
    }
}
