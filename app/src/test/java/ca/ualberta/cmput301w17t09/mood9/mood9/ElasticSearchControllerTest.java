package ca.ualberta.cmput301w17t09.mood9.mood9;
import android.util.Log;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.*;

/**
 * Created by ddao on 3/5/17.
 */

public class ElasticSearchControllerTest {

    @Test
    public void testAdd() {

        Mood m1 = new Mood(12.22,13.22,"Trigger","1","77","22",new Date(12-12-2016),"1");

        ElasticSearchMOodController.AddMoodsTask addMoodsTask = new ElasticSearchMOodController.AddMoodsTask();
        addMoodsTask.execute(m1);

        ElasticSearchMOodController.GetMoodsTask getMoodsTask = new ElasticSearchMOodController.GetMoodsTask();
        getMoodsTask.execute("");
        try {
             System.out.println(getMoodsTask.get());
        } catch (Exception e) {
            Log.i("Error", "Can't get moods from ElasticSearch");
        }

        /*
        ArrayList<Mood> moods = new ArrayList<Mood>();

        try {
            moods = getMoodsTask.get();
        } catch (Exception e) {
            Log.i("Error", "Can't get moods from ElasticSearch");
        }

        Mood update_mood = moods.get(2);
        update_mood.setImageTriggerId("10");

        // UPDATE Procedure
        ElasticSearchMOodController.UpdateMoodsTask updateMoodsTask = new ElasticSearchMOodController.UpdateMoodsTask();
        updateMoodsTask.execute(update_mood);

        // ElasticSearchMOodController.DeleteMoodTask deleteMoodTask = new ElasticSearchMOodController.DeleteMoodTask();
        // deleteMoodTask.execute(m1);

        ElasticSearchMOodController.GetMoodsTask getMoodsTask2 = new ElasticSearchMOodController.GetMoodsTask();
        getMoodsTask2.execute("");
        try {
            moods = getMoodsTask2.get();
        } catch (Exception e) {
            Log.i("Error", "Can't get moods from ElasticSearch");
        }
        */
    }

    @Test
    public void testEdit() {

    }

    @Test
    public void testDelete() {

    }

}
