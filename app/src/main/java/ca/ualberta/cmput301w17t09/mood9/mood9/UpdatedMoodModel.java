package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import java.util.HashMap;

/**
 * Created by ddao on 3/21/17.
 */
public class UpdatedMoodModel extends InstrumentationTestCase {

    /**
     * The constant FILENAME.
     */
    public static String FILENAME = "personal_moods.sav";
    private ArrayList<Mood> moodList;
    /**
     * The Currentmoods.
     */
    File CURRENTMOODS = new File("personal_moods.sav");
    /**
     * The M context.
     */
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

    /***
     * Initialization for MoodModel
     * @param context : current activity context
     * @param file : Filename to save offline
     */
    public UpdatedMoodModel(Context context, File file) {
        this.mContext = context;
        this.CURRENTMOODS = file;
        this.moodList = new ArrayList<Mood>();
        loadFromFile();
    }

    /***
     * Add a mood to offline storage, then synchronize to Elastic Search
     * if possible
     * @param mood the mood
     */
    public void addMood(Mood mood) {
        moodList.add(mood);
        saveInFile();
        synchronize();
    }

    /***
     * Delete a mood from offline storage,
     * then synchronize to Elastic Search
     * @param mood the mood
     */
    public void deleteMood(Mood mood) {
        moodList.remove(mood);
        saveInFile();
        synchronize();
    }

    /***
     * Update a mood in offline storage,
     * then synchronize to Elastic Search
     * @param mood the mood
     */
    public void updateMood(Mood mood) {
        for (Mood m : moodList) {
            if (m.getId() == null) {
                if (m == mood) {
                    moodList.remove(m);
                    moodList.add(mood);
                    break;
                }
            } else {
                if (m.getId().equals(mood.getId())) {
                    moodList.remove(m);
                    moodList.add(mood);
                    break;
                }
            }
        }
        saveInFile();
        synchronize();
    }

    /***
     * Check if Internet connection is available,
     * then synchronize to Elastic Search
     */
    private void synchronize() {
        if (isNetworkAvailable()) {
            ElasticSearchMOodController.AddMoodsTask addMoodsTask = new ElasticSearchMOodController.AddMoodsTask(moodList);
            addMoodsTask.execute();
        }
    }

    /***
     * Get all current User moods
     * @return current user moods
     */
    public ArrayList<Mood> getCurrentUserMoods() {
        return moodList;
    }

    /**
     * Gets universal user moods.
     *
     * @param search_param the search param
     * @return the universal user moods
     */
    public ArrayList<Mood> getUniversalUserMoods(HashMap<String, String> search_param) {
        ArrayList<Mood> moods = new ArrayList<Mood>();
        // Elastic Search Stuff, do later
        if (search_param == null) {
            search_param = new HashMap<String, String>();
            search_param.put("", "");
        }
        ElasticSearchMOodController.GetMoodsTask getMoodsTask = new ElasticSearchMOodController.GetMoodsTask(search_param);
        Log.d("ESTEST", "getUniversalUserMoods");
        getMoodsTask.execute();

        try {
            moods = getMoodsTask.get();
        } catch (Exception e) {
            Log.i("Error", "Can't get Universal Moods");
        }

        return moods;
    }

    /**
     * Load current user moods from offline storage
     */
    private void loadFromFile() {

        try {
            JsonReader br = new JsonReader(new FileReader(CURRENTMOODS));

            Gson gson = new Gson();

            Type listType = new TypeToken<ArrayList<Mood>>() {}.getType();
            moodList = new ArrayList<Mood>();
            moodList = gson.fromJson(br, listType);
            if (moodList == null) moodList = new ArrayList<Mood>();

        }   catch (FileNotFoundException e) {
            moodList = new ArrayList<Mood>();
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Save all current user moods in offline storage
     */
    private void saveInFile() {
        try {
            new PrintWriter(CURRENTMOODS).close();

            // Write new array to file
            BufferedWriter buffWriter = new BufferedWriter(new FileWriter(CURRENTMOODS, true));

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
