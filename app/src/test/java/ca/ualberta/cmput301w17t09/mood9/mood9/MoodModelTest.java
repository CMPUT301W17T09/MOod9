package ca.ualberta.cmput301w17t09.mood9.mood9;


import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Rohit on 2017-02-22.
 */

public class MoodModelTest {

    @Test
    public void testLoadMood(){
    MoodModel test = new MoodModel();
        Mood m1 = new Mood("1",12.22,13.22,"Trigger","1","Alone","22",new Date(12-12-2016));
        Mood m2 = new Mood("2",12.22,13.22,"Trigger","2","With 2 others","14",new Date(12-11-2016));
        test.setOnElasticSearch(m1);
        test.setOnElasticSearch(m2);
        test.loadMood();
        assertEquals(test.getMood("1"),m1);
        assertEquals(test.getMood("2"),m2);

    }

    public void testElasticSearch(){

    }

    public void testDeleteMood(){

    }

    public void testUpdateMood(){

    }

    public void testGetMoodsNear(){

    }

    public void testMyMoods(){

    }

    public void testGetUniversalMoods(){

    }

    public void testGetFollowedMoods(){

    }


}
