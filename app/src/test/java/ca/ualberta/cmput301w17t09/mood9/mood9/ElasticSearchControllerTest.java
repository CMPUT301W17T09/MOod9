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

        Mood m1 = new Mood("0",12.22,13.22,"Trigger","1","Alone","22",new Date(12-12-2016),"1");
        /*
        ElasticSearchMOodController.AddMoodsTask addMoodsTask = new ElasticSearchMOodController.AddMoodsTask();
        addMoodsTask.execute(m1);
        */
        ElasticSearchMOodController.GetMoodsTask getMoodsTask = new ElasticSearchMOodController.GetMoodsTask();
        getMoodsTask.execute("");

        ArrayList<Mood> moods = new ArrayList<Mood>();

        try {
            moods = getMoodsTask.get();
        } catch (Exception e) {
            Log.i("Error", "Can't get moods from ElasticSearch");
        }


        ElasticSearchMOodController.DeleteMoodTask deleteMoodTask = new ElasticSearchMOodController.DeleteMoodTask();
        deleteMoodTask.execute(m1);

        ElasticSearchMOodController.GetMoodsTask getMoodsTask2 = new ElasticSearchMOodController.GetMoodsTask();
        getMoodsTask2.execute("");
        try {
            moods = getMoodsTask2.get();
        } catch (Exception e) {
            Log.i("Error", "Can't get moods from ElasticSearch");
        }
    }

    @Test
    public void testEdit() {

    }

    @Test
    public void testDelete() {

    }

}
