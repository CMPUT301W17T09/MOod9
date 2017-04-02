package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
/**
 * Created by ddao on 3/21/17.
 */

public class UpdatedMoodModelTest {

    private ArrayList<Mood> testMoods;
    private File testFile = new File("test_moods.sav");
    private UpdatedMoodModel moodModel;
    
    private setUp() {
        testMoods = new ArrayList<Mood>();
        moodModel = new UpdatedMoodModel(this, testFile);
        
        Mood m1 = new Mood(12.22,13.22,"Trigger","1","Fun","22",new Date(),"1");
        Mood m2 = new Mood(12.22,13.22,"Trigger","2","Fun","22",new Date(),"2");
        Mood m3 = new Mood(12.22,13.22,"Trigger","3","asodjf","powooidi",new Date(),"3");
        
        testMoods.add(m1);
        testMoods.add(m2);
        testMoods.add(m3):
    }
    
    @Test
    public void testAdd(){
        setUp();
        
        for (Mood m : testMoods) 
            moodModel.addMood(m);

        assertTrue(moodModel.getCurrentUserMoods().size() == 3);
    }
    
    @Test
    public void testDelete() {
        moodModel.deleteMood(testMoods.get(2));
        assertTrue(moodModel.getCurrentUserMoods().size() == 2);
    }
    
    @Test
    public void testUpdate(){
        m1.setTrigger("This is awesome");
        moodModel.updateMood(m1);
        
        for (Mood m : moodModel.getCurrentUserMoods()) { 
            if (m.getOfflineid() == m1.getOfflineid()) {
                assertTrue(m1.getTrigger().equals(m.getTrigger()));
                break;
            }
        }
    }
    
    @Test
    public void testReset() {
        UpdatedMoodModel newModel = new UpdatedMoodModel();
        assertTrue(newModel.getCurrentUserMoods().size() == 2);
    }
}
