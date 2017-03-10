package ca.ualberta.cmput301w17t09.mood9.mood9;


import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Rohit on 2017-02-22.
 */

public class MoodModelTest {

    @Test
    public void test1(){
        MoodModel ins = new MoodModel();
        Mood m1 = new Mood(12.22,13.22,"Trigger","1","Fun","22",new Date(12-12-2016),"1");
        Mood m2 = new Mood(12.22,13.22,"Trigger","1","asodjf","powooidi",new Date(12-12-2016),"2");
        ins.addMood(m1);
        ins.addMood(m2);
        System.out.println(ins.getUniversalMoods());
    }

}
