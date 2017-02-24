package ca.ualberta.cmput301w17t09.mood9.mood9;

import org.junit.Test;
import static org.junit.Assert.*;


import java.util.concurrent.ConcurrentMap;
/**
 * Created by Rohit on 2017-02-23.
 */

public class UserModelTest {

    //Also tests getUser since the code for that test would be redundant
    @Test
    public void testLoadUsers(){
        UserModel test = new UserModel();
        User user1 = new User("1","Test");
        User user2  = new User("2","Test1");
        test.setOnElasticSearch(user1);
        test.setOnElasticSearch(user2);
        test.loadUsers();
        assertEquals(test.getUser("1"),"Test");
        assertEquals(test.getUser("2"),"Test1");

    }

    //Tests both the uplink and downlink for our elasticsearch
    @Test
    public void testElasticSearch(){
        UserModel test = new UserModel();
        User user1 = new User("1","Test");
        test.setOnElasticSearch(user1);
        User testuser = test.getFromElasticSearch("1");
        assertEquals(user1,testuser);
    }

    @Test
    public void testGetUsers(){
        UserModel test = new UserModel();
        User user1 = new User("1","Test");
        User user2  = new User("2","Test1");
        test.setOnElasticSearch(user1);
        test.setOnElasticSearch(user2);
        test.loadUsers();
        ConcurrentMap<String, User> map1 = test.getUsers();
        assertEquals(map1.size(),2);
        assertEquals(map1.get("1"),"Test");
        assertEquals(map1.get("2"),"Test1");
    }

}
