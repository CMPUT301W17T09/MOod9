package ca.ualberta.cmput301w17t09.mood9.mood9;


import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;

/**
 * Created by Rohit on 2017-02-22.
 */

public class MoodModelTest {

    @Test
    public void testAddAndDeleteOffline(){
        MoodModel ins = new MoodModel();
        ins.deletefromfile();
        ins.clearAdded();
        Mood m1 = new Mood(12.22,13.22,"Trigger","1","Fun","22",new Date(),"1");
        Mood m2 = new Mood(12.22,13.22,"Trigger","1","Fun","22",new Date(),"1");
        Mood m3 = new Mood(12.22,13.22,"Trigger","1","asodjf","powooidi",new Date(),"2");
        ins.addMood(m3);
        ins.addMood(m1);
        ins.addMood(m2);
        ins.deleteMood(m1);
        assertEquals(ins.getUniversalMoods().size(),2);
        assertEquals(ins.getUniversalMoods().get(0).getDate().compareTo(m1.getDate()),-1);
        assertEquals(ins.getUniversalMoods().get(0).getUser_id(),"2");
    }

    @Test
    public void testUpdate(){
        MoodModel ins = new MoodModel();
        ins.deletefromfile();
        ins.clearAdded();
        Mood m1 = new Mood(12.22,13.22,"Trigger","1","Fun","22",new Date(),"1");
        Mood m2 = new Mood(12.22,13.22,"Trigger","1","Fun","22",new Date(),"1");
        Mood m3 = new Mood(12.22,13.22,"Trigger","1","asodjf","powooidi",new Date(),"2");
        ins.addMood(m1);
        ins.addMood(m2);
        ins.addMood(m3);
        m1.setLatitude(88.0);
        m1.setLongitude(90.0);
        ins.updateMood(m1);
        double x = m1.getLatitude();
        assertTrue(m1.getLatitude()==88.0);
        assertTrue(m1.getLongitude()==90.0);


    }

}
