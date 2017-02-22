package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dannick on 2/22/17.
 */

public class UserModel {
    protected static HashMap<String, UserRecord> users;
    protected Date last_updated;

    public static User getUser(String id){
        return users.get(id).getUser();
    }

    public static void loadUsers() {
        UserModel.users = new HashMap<String, UserRecord>();
    }

    public static void loadUser(String id) {

    }

    public static HashMap<String, User> getUsers(){
        HashMap<String, User> users_map = new HashMap<String, User>();
        Iterator<Map.Entry<String, UserRecord>> it = users.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            UserRecord ur = (UserRecord)pair.getValue();
            users_map.put((String) pair.getKey(), ur.getUser());
        }
        return users_map;
    }
}
