package ca.ualberta.cmput301w17t09.mood9.mood9;


import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Rohit on 2017-02-22.
 */

public class MoodModelTest {

    @Test
    public void testAddAndDeleteOffline(){
        File addedmoods = new File("addedmoods.sav");
        File deletedmoods = new File("deletedmoods.sav");
        MoodModel ins = new MoodModel(addedmoods,deletedmoods);
        ins.deletefromfile();
        ins.clearAdded();
        Mood m1 = new Mood(12.22,13.22,"Trigger","1","Fun","22",new SimpleDateFormat(),"1");
        Mood m2 = new Mood(12.22,13.22,"Trigger","1","Fun","22",new SimpleDateFormat(),"1");
        Mood m3 = new Mood(12.22,13.22,"Trigger","1","asodjf","powooidi",new SimpleDateFormat(),"2");
        ins.addMood(m3);
        ins.addMood(m1);
        ins.addMood(m2);
        ins.deleteMood(m1);
        assertEquals(ins.getUniversalMoods().size(),2);
        assertEquals(ins.getUniversalMoods().get(0).getUser_id(),"2");
    }

}
