package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;

/**
 * Created by ddao on 3/21/17.
 */

public class UpdatedMoodModelTest {

    // Put this in tomorrow Monday:
    // http://stackoverflow.com/questions/28960898/getting-context-in-androidtestcase-or-instrumentationtestcase-in-android-studio
    private Context context = new MockContext();
    private File testFile = new File("test_moods.sav");
    private UpdatedMoodModel moodModel = new UpdatedMoodModel(context, testFile);

    Mood m1 = new Mood(12.22,13.22,"Trigger","1","Fun","22", "2015-02-01 00:00:00","1", "asf");
    Mood m2 = new Mood(12.22,13.22,"Trigger","2","Fun","22", "2015-02-01 00:00:00","2", "sfas");
    Mood m3 = new Mood(12.22,13.22,"Trigger","3","asodjf","powooidi", "2015-02-01 00:00:00","3", "asdds");
    
    @Test
    public void testAdd(){

        moodModel.addMood(m1);

        assertTrue(moodModel.getCurrentUserMoods().size() == 3);
    }
    
    @Test
    public void testDelete() {
        moodModel.deleteMood(m2);
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
        UpdatedMoodModel newModel = new UpdatedMoodModel(context, testFile);
        assertTrue(newModel.getCurrentUserMoods().size() == 2);
    }
}
