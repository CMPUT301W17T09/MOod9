package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dannick on 2/22/17.
 */

public class UserModel {
    protected static HashMap<String, User> users;
    protected Date last_updated;

    public static User getUser(String id){
        return users.get(id);
    }

    public static void loadUsers() {
        UserModel.users = new HashMap<String, User>();
    }

    public static void loadUser(String id) {

    }

    public static HashMap<String, User> getUsers(){
        return users;
    }
}
