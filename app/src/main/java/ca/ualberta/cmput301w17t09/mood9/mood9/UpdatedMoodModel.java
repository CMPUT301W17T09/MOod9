package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by ddao on 3/21/17.
 */

public class UpdatedMoodModel {

    private String FILENAME = "personal_moods.sav";
    private ArrayList<Mood> moodList;
    Context mContext;

    /***
     * Reference: http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
     * Checks if there's Internet connetivity
     * @return
     * Bool: True if there's connectivity, False otherwise
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public UpdatedMoodModel(Context context) {
        this.mContext = context;
        loadFromFile();
    }

    public void addMood(Mood mood) {
        moodList.add(mood);
        saveInFile();
        synchronize();
    }

    public void deleteMood(Mood mood) {
        moodList.remove(mood);
        saveInFile();
        synchronize();
    }

    public void updateMood(Mood mood) {
        for (Mood m : moodList) {
            if (m.getId().equals(mood.getId())) {
                moodList.remove(m);
                moodList.add(mood);
                break;
            }
        }
        saveInFile();
        synchronize();
    }

    private void synchronize() {
        if (isNetworkAvailable()) {
            ElasticSearchMOodController.AddMoodsTask addMoodsTask = new ElasticSearchMOodController.AddMoodsTask(moodList);
            addMoodsTask.execute();
        }
    }

    public ArrayList<Mood> getCurrentUserMoods() {
        return moodList;
    }

    public ArrayList<Mood> getUniversalUserMoods(ArrayList<String> user_ids) {
        ArrayList<Mood> moods = new ArrayList<Mood>();
        // Elastic Search Stuff, do later
        ElasticSearchMOodController.GetMoodsTask getMoodsTask = new ElasticSearchMOodController.GetMoodsTask(user_ids);
        getMoodsTask.execute();

        try {
            moods = getMoodsTask.get();
        } catch (Exception e) {
            Log.i("Error", "Can't get Universal Moods");
        }

        return moods;
    }

    private void loadFromFile() {

        try {
            JsonReader br = new JsonReader(new FileReader(FILENAME));

            Gson gson = new Gson();

            Type listType = new TypeToken<ArrayList<Mood>>() {}.getType();
            moodList = new ArrayList<Mood>();
            moodList = gson.fromJson(br, listType);

        }   catch (FileNotFoundException e) {
            moodList = new ArrayList<Mood>();
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void saveInFile() {
        try {
            // Delete everything on file
            new PrintWriter(FILENAME).close();

            // Write new array to file
            BufferedWriter buffWriter = new BufferedWriter(new FileWriter(FILENAME, true));

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Mood>>() {}.getType();
            String json = gson.toJson(moodList, type);


            buffWriter.append(json);

            buffWriter.newLine();
            buffWriter.flush();
            buffWriter.close();

        } catch (FileNotFoundException e) {
            // TODO: Handle the Exception properly later
            throw new RuntimeException();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
