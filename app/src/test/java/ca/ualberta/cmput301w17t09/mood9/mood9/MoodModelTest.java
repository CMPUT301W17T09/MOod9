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
    public void testLoadMood(){
    MoodModel test = new MoodModel();
        Mood m1 = new Mood("1",12.22,13.22,"Trigger","1","Alone","22",new Date(12-12-2016),"1");
        Mood m2 = new Mood("2",12.22,13.22,"Trigger","2","With 2 others","14",new Date(12-11-2016),"2");
        test.setOnElasticSearch(m1);
        test.setOnElasticSearch(m2);
        test.loadMood();
        assertEquals(test.getMood("1"),m1);
        assertEquals(test.getMood("2"),m2);

    }

    @Test
    public void testElasticSearch(){
    MoodModel test = new MoodModel();
    Mood m1 = new Mood("1",12.22,13.22,"Trigger","1","Alone","22",new Date(12-12-2016),"2");
    test.setOnElasticSearch(m1);
    Mood testmood = test.getFromElasticSearch("1");
    assertEquals(m1,testmood);
    }

    @Test
    public void testDeleteMood(){
        MoodModel test = new MoodModel();
        Mood m1 = new Mood("1",12.22,13.22,"Trigger","1","Alone","22",new Date(12-12-2016),"1");
        Mood m2 = new Mood("2",12.22,13.22,"Trigger","2","With 2 others","14",new Date(12-11-2016),"2");
        test.setOnElasticSearch(m1);
        test.setOnElasticSearch(m2);
        test.loadMood();
        test.deleteMood(m1);
        assertEquals(test.getFromElasticSearch("1"),null);
        test.loadMood();
        assertEquals(test.getMood("1"),null);
    }

    @Test
    public void testUpdateMood(){
        MoodModel test = new MoodModel();
        Mood m1 = new Mood("1",12.22,13.22,"Trigger","1","Alone","22",new Date(12-12-2016),"1");
        test.setOnElasticSearch(m1);
        test.loadMood();
        assertEquals(test.getMood("1"),m1);
        m1.setId("2");
        test.updateMood(m1); //Assuming updateMood automatically modifies elasticsearch
        assertEquals(test.getFromElasticSearch("1"),null);
        assertEquals(test.getFromElasticSearch("2"),m1);
        test.loadMood();
        assertEquals(test.getMood("1"),null);
        assertEquals(test.getMood("2"),m1);
    }

    @Test
    public void testGetMoodsNear(){
        MoodModel test = new MoodModel();
        Mood m1 = new Mood("1",12.22,13.22,"Trigger","1","Alone","22",new Date(12-12-2016),"1");
        Mood m2 = new Mood("2",12.22,13.22,"Trigger","2","With 2 others","14",new Date(12-11-2016),"2");
        Mood m3 = new Mood("3",9999.0,9999.0,"Trigger","2","With 2 others","15",new Date(12-11-2016),"3");
        //http://tutorials.jenkov.com/java-util-concurrent/concurrentmap.html
        ConcurrentMap<String, Mood> testing = new ConcurrentHashMap();
        testing.put("1",m1);
        testing.put("2",m2);
        ConcurrentMap<String, Mood> test1 = test.getMoodsNear(12.22,13.22);
        assertEquals(test1,testing);
    }

    @Test
    public void testMyMoods(){
        //Not sure how
    }

    @Test
    public void testGetUniversalMoods(){
        MoodModel test = new MoodModel();
        Mood m1 = new Mood("1",12.22,13.22,"Trigger","1","Alone","22",new Date(12-12-2016),"1");
        Mood m2 = new Mood("2",12.22,13.22,"Trigger","2","With 2 others","14",new Date(12-11-2016),"1");
        Mood m3 = new Mood("3",9999.0,9999.0,"Trigger","2","With 2 others","15",new Date(12-11-2016),"2");
        ConcurrentMap<String, Mood> testing = new ConcurrentHashMap();
        testing.put("1",m1);
        testing.put("2",m2);
        testing.put("3",m3);
        test.setOnElasticSearch(m1);
        test.setOnElasticSearch(m2);
        test.setOnElasticSearch(m3);
        test.loadMood();
        ConcurrentMap<String,Mood> final1 = test.getUniversalMoods();
        assertEquals(testing,final1);
    }

    @Test
    public void testGetFollowedMoods(){
        MoodModel test = new MoodModel();
        UserModel sample = new UserModel();
        Mood m1 = new Mood("1",12.22,13.22,"Trigger","1","Alone","22",new Date(12-12-2016),"1");
        Mood m2 = new Mood("2",12.22,13.22,"Trigger","2","With 2 others","14",new Date(12-11-2016),"1");
        Mood m3 = new Mood("3",9999.0,9999.0,"Trigger","2","With 2 others","15",new Date(12-11-2016),"2");
        User u1 = new User("1","Test");
        sample.setOnElasticSearch(u1);
        sample.loadUsers();
        User user1 = sample.getUser("1");
        ConcurrentMap<String, Mood> testing = new ConcurrentHashMap();
        testing.put("1",m1);
        testing.put("2",m2);
        test.setOnElasticSearch(m1);
        test.setOnElasticSearch(m2);
        test.setOnElasticSearch(m3);
        test.loadMood();
        ConcurrentMap<String,Mood> final1 = test.getFollowedMoods(user1);
        assertEquals(testing,final1);
    }


}
