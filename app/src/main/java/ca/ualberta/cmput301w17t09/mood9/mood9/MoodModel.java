package ca.ualberta.cmput301w17t09.mood9.mood9;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

/**
 * Added array size getter by cdkushni on 3/10/2017
 * Redone by Rohit on 2017-03-08
 * Created by dannick on 2/22/17.
 */

/**
 * The MoodModel includes functionality to add, and delete moods and the ability to synchronize
 * with elastic search
 * @throws IOException
 */
public class MoodModel {
    private static ArrayList<Mood> moods = new ArrayList<Mood>();
    private EmotionModel emodel;
    private SocialSituationModel smodel;
    private FileInputStream moodsOnFile;
    File ADDEDNAME;
    File DELETEDNAME;
    ElasticSearchMOodController.GetMoodsTask getMoodsTask = new ElasticSearchMOodController.GetMoodsTask();
    ElasticSearchMOodController.AddMoodsTask addMoodsTask = new ElasticSearchMOodController.AddMoodsTask();
    ElasticSearchMOodController.UpdateMoodsTask UpdateMoodsTask = new ElasticSearchMOodController.UpdateMoodsTask();
    ElasticSearchMOodController.DeleteMoodTask deleteMoodTask = new ElasticSearchMOodController.DeleteMoodTask();


    /**
     * Updates the moods arrayList to reflect the moods on elasticsearch and on disk
     */
    public void setMoodsArray() {
        ArrayList<Mood> deleteMoods = readFromDeleted();
        ArrayList<Mood> fileMoods = readFromAdded(); //Last set of universal moods PLUS any offline moods
        ArrayList<Mood> finalarr = new ArrayList<Mood>();  //
        ArrayList<Mood> finalarr2 = new ArrayList<Mood>();
        finalarr.addAll(fileMoods); // final array contains all moods form elastic and add moods
        for (int i = 0; i < finalarr.size(); i++) {
            if (!finalarr2.contains(finalarr.get(i))) {
                finalarr2.add(finalarr.get(i));
            }
        } //finalarr2 has unique set of moods
        if(deleteMoods != null) {
            for(int i = 0; i < deleteMoods.size(); i++){
                for(int j = 0; j < finalarr2.size(); j++){
                    if (finalarr2.get(j).getOfflineid().equals(deleteMoods.get(i).getOfflineid())){
                            finalarr2.remove(finalarr.get(j));
                            break;
                        }
                    }
                }
            } //remove all moved pending for deletion from finalarr

        deletefromfile(); //no more deleted mooos
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

    public MoodModel(File f1, File f2){
        this.DELETEDNAME = f2;
        this.ADDEDNAME = f1;

    }
    /**
     *
     * @param userid User provides a user ID
     * @return an ArrayList of all moods of that user
     */
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
     * @return All moods of every person the user is following
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

    /**
     *
     * @return All files on disk and/or elasticsearch in an ArrayList format
     */
    public ArrayList<Mood> getUniversalMoods() {
        setMoodsArray();
        return moods;
    }

    /**
     * Returns an arraylist of all moods near a certain latitude and longitude
     * @param latitude
     * @param longitude
     * @return TO DO
     */
    public ArrayList<Mood> getMoodsNear(Double latitude, Double longitude) {
        // TODO
        return moods;
    }

    /**
     * When a user clicks on a mood and edits it, the changed information is passed to this function
     * and updated in place
     * @param mood
     */
    public void updateMood(Mood mood) {
        for (int i = 0; i < moods.size(); i++) {
            if (moods.get(i).getOfflineid().equals(mood.getOfflineid())) {
                moods.set(i, mood);
            }
        }
    }


    public int getMoodModelSize() {
        return moods.size();
    }

    /**
     * Adds the given mood to elastic search and to the addMood file in a gson format
     * @param mood
     * @throws IOException e
     */
    public void addMood(Mood mood) {
        ElasticSearchMOodController.AddMoodsTask addMoodsTask = new ElasticSearchMOodController.AddMoodsTask();
        addMoodsTask.execute(mood);
        // add to elastic search
        Gson gson = new Gson();
//      String mjs = gson.toJson(mood);
        ArrayList<Mood> mjs1 = new ArrayList<Mood>();
        mjs1 = readFromAdded();
        if (mjs1==null){
            mjs1 = new ArrayList<Mood>();
        }
        mjs1.add(mood);
        try {
            FileOutputStream fos = new FileOutputStream(ADDEDNAME);
            ByteArrayOutputStream baos =  new ByteArrayOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(baos);
            gson.toJson(mjs1, osw);
            String x = osw.toString();
        } catch (IOException e) {
            System.out.println("File not found2");
        }
        setMoodsArray();
    }

/**
 * Adds a mood to the deletedMoods file, and deletes the mood form addedmoods as well as
 * elasticsearch
 */
    public void deleteMood(Mood mood) {
        Gson gson = new Gson();
        ArrayList<Mood> mjs2 = new ArrayList<Mood>();
        mjs2 = readFromDeleted();
        if (mjs2==null){
            mjs2 = new ArrayList<Mood>();
        }
        mjs2.add(mood);
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(DELETEDNAME));
            gson.toJson(mjs2,br);
            br.flush();
        } catch (IOException e) {
            System.out.println("File not found3");
        }
        setMoodsArray();
    }

    /**
     * Returns all moods in the addedname file
     * @return an Arraylist of all moods from a JSON file
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
    /**
     * Returns all moods in the deletedname file
     * @return arraylist of moods from the deletedname file
     */
    private ArrayList<Mood> readFromDeleted() {
        ArrayList<Mood> deleted = new ArrayList<Mood>();
        try {
            JsonReader br = new JsonReader(new FileReader(DELETEDNAME));
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
     * Deletes all moods form the deleted mooods file
     */
    public void deletefromfile() {
        try {
            FileOutputStream writer = new FileOutputStream(DELETEDNAME);
            writer.write(("").getBytes());
            writer.close();
        } catch (IOException e) {
            System.out.println("file not found");
        }
    }

    /**
     * Clears all moods from the added file. Mainly used for testing
     */
    public void clearAdded(){
        try {
            FileOutputStream writer = new FileOutputStream(ADDEDNAME);
            writer.write(("").getBytes());
            writer.close();
        } catch (IOException e) {
            System.out.println("file not found");
        }
    }
    //http://stackoverflow.com/questions/24573598/write-arraylist-of-custom-objects-to-file

    /**
     * Saves the arrayList(moods) to addedname file
     * @return int if an error occurs
     */
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
