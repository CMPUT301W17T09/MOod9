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

    // Probably need a new index just for testing
    // DO NOT RUN THESE TESTS YET, need to make a new index for testing.
    
    private ArrayList<Mood> getMoods(String user_id) {
        ElasticSearchMOodController.GetMoodsTask getMoodsTask = new ElasticSearchMOodController.GetMoodsTask();
        getMoodsTask.execute(user_id);
        
        ArrayList<Mood> moods = new ArrayList<Mood>();

        try {
            moods = getMoodsTask.get();
        } catch (Exception e) {
            Log.i("Error", "Can't get moods from ElasticSearch");
        }
        
        return moods;
    }
    
    @Test
    public void testAdd() {
        Mood m1 = new Mood(12.22,13.22,"Trigger","1","Fun","22",new Date(12-12-2016),"1");
        Mood m2 = new Mood(12.22,13.22,"Trigger","2","Alone","10",new Date(10-01-2017),"1");

        ElasticSearchMOodController.AddMoodsTask addMoodsTask = new ElasticSearchMOodController.AddMoodsTask();
        addMoodsTask.execute(new ArrayList<Mood>() {m1, m2} );
        
        ArrayList<Mood> moods = getMoods("");
       
        assertTrue(moods.size() == 2);
    }

    @Test
    public void testUpdate() {
        ArrayList<Mood> moods = getMoods("2");
        Mood update_mood = moods.get(0);
        update_mood.setImageTriggerId("30");

        // UPDATE Procedure
        ElasticSearchMOodController.UpdateMoodsTask updateMoodsTask = new ElasticSearchMOodController.UpdateMoodsTask();
        updateMoodsTask.execute(update_mood);
        
        // GET and check
        moods = getMoods("2");
        update_mood = moods.get(0);
        assertTrue(update_mood.getTriggerId() == "30");
    }

    @Test
    public void testDelete() {
        ArrayList<Mood> moods = getMoods(""); 
        assertTrue(moods.size() == 2);
        
        ElasticSearchMOodController.DeleteMoodTask deleteMoodTask = new ElasticSearchMOodController.DeleteMoodTask();
        deleteMoodTask.execute(moods);
        
        moods = getMoods(""); 
        assertTrue(moods.size() == 0);
    }

}
