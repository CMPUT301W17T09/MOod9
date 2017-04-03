package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.util.ArrayList;

/**
 * Created by dannick on 2/22/17.
 */
public class UserModel {

    /***
     * Checks if a username already existed, by querying ElasticSearch
     * @param username the username
     * @return null if username already existed User object with the new username, as well as push the new User object to ElasticSearch
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
        if (users == null) {
            User user = new User(username);
            ElasticSearchMOodController.AddUsersTask addUsersTask = new ElasticSearchMOodController.AddUsersTask();
            String id = null;
            try {
                id = addUsersTask.execute(user).get();
            } catch (Exception e){
                String err = e.getMessage();
                return null;
            }

            return id;
        } else
            return null;
    }

    /**
     * Get User object, given user id
     *
     * @param userId the user id
     * @return User Object, NUll if not exist
     */
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

    /***
     * Update the user, upload to ElasticSearch
     * @param user the user
     */
    public static void updateUser(User user) {
        ElasticSearchMOodController.AddUsersTask addUsersTask = new ElasticSearchMOodController.AddUsersTask();
        addUsersTask.execute(user);
    }

    /***
     * Get all Users of the app
     * @return  : ArrayList<String> of all Usernames
     */
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

    /***
     * Get userID give username
     * @param username the username
     * @return String userID
     */
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
