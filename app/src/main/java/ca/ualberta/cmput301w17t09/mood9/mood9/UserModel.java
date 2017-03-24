package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.util.ArrayList;

/**
 * Created by dannick on 2/22/17.
 */
public class UserModel {

    /***
     * Checks if a username already existed, by querying ElasticSearch
     * @param username the username
     * @return null  if username already existed User object with the new username, as well as push the new User object to ElasticSearch
     */
    public static String getUser(String username){

        ElasticSearchMOodController.GetUsersTaskName getUsersTask = new ElasticSearchMOodController.GetUsersTaskName();
        getUsersTask.execute(username);

        ArrayList<User> users = new ArrayList<User>();

        try {
            users = getUsersTask.get();
        } catch (Exception e) {

        }

        // If user already exists, return null
        if (users.size() != 0)
            return null;
        else {
            User user = new User(username);
            ElasticSearchMOodController.AddUsersTask addUsersTask = new ElasticSearchMOodController.AddUsersTask();
            String id = null;
            try {
                id = addUsersTask.execute(user).get();
            } catch (Exception e){ return null;}

            return id;
        }
    }

    public  static User getUserProfile(String userId) {
        ElasticSearchMOodController.GetUsersTaskID getUsersTask = new ElasticSearchMOodController.GetUsersTaskID();
        getUsersTask.execute(userId);

        User user = new User("");

        try {
            user = getUsersTask.get();
        } catch (Exception e) {

        }

        return user;
    }

    public static ArrayList<String> getAllUsers() {
        ElasticSearchMOodController.GetUsersTaskName getUsersTaskName = new ElasticSearchMOodController.GetUsersTaskName();
        getUsersTaskName.execute("");

        ArrayList<User> users = new ArrayList<User>();
        ArrayList<String> user_names = new ArrayList<String>();

        try {
            users = getUsersTaskName.get();
        } catch (Exception e) {

        }

        for (User user : users)
            user_names.add(user.getName());

        return user_names;
    }

    public static String getUserID(String username) {
        ElasticSearchMOodController.GetUsersTaskName getUsersTaskName = new ElasticSearchMOodController.GetUsersTaskName();
        getUsersTaskName.execute(username);

        ArrayList<User> users = new ArrayList<User>();

        try {
            users = getUsersTaskName.get();
        } catch (Exception e) {

        }

        return users.get(0).getId();
    }
}
