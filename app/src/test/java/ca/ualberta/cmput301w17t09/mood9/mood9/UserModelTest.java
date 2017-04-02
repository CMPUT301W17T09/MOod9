package ca.ualberta.cmput301w17t09.mood9.mood9;

import org.junit.Test;
import static org.junit.Assert.*;


import java.util.concurrent.ConcurrentMap;
/**
 * Created by Rohit on 2017-02-23.
 */

public class UserModelTest {
    
    private userID;
    String newUserName = "test_user";
    
    @Test
    public testAddUser() {     
        String newUserID = UserModel.getUser(newUserName);
        assertTrue(newUserID != null);
        userID = newUserID;
        
        String existedUserID = UserModel.getUser(newUserName);
        assertTrue(existeduserID == null);
    }
    
    @Test
    public testGetUser() {
        User existedUser = UserModel.getUserProfile(userID);
        assertTrue(existedUser.getName().equals(newUserName));
        
        existedUser = UserModel.getUserProfile("This is non-existing");
        assertTrue(existedUser == null);
    }
    
    @Test
    public testUpdateUser() {
        User existedUser = UserModel.getUserProfile(userID);
        existedUser.addToRequests("This is Request 1");
        
        UserModel.updateUser(existedUser);
        // WAIT For a bit
        Thread.sleep(1000);
        
        existedUser = UserModel.getUserProfile(userID);
        assertTrue(existedUser.getRequests().size() == 1);
    }

    @Test testGetUserID() {
        String ID = UserModel.getUserID(newUserName);
        assertTrue(ID.equals(userID));
    }
    

}
