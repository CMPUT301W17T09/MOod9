package ca.ualberta.cmput301w17t09.mood9.mood9;

/**
 * Created by dannick on 2/22/17.
 */
public class UserModel {

    /***
     * Checks if a username already existed, by querying ElasticSearch
     * @param username the username
     * @return null  if username already existed User object with the new username, as well as push the new User object to ElasticSearch
     */
    public static User getUser(String username){

        ElasticSearchMOodController.GetUsersTaskName getUsersTask = new ElasticSearchMOodController.GetUsersTaskName();
        getUsersTask.execute(username);

        User user = new User("");

        try {
            user = getUsersTask.get();
        } catch (Exception e) {

        }

        if (user != null)
            return null;
        else {
            user = new User(username);

            ElasticSearchMOodController.AddUsersTask addUsersTask = new ElasticSearchMOodController.AddUsersTask();
            addUsersTask.execute(user);

            return user;
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

    public static User getUserID(String username) {
        ElasticSearchMOodController.GetUsersTaskName getUsersTaskName = new ElasticSearchMOodController.GetUsersTaskName();
        getUsersTaskName.execute(username);

        User user = new User("");

        try {
            user = getUsersTaskName.get();
        } catch (Exception e) {

        }

        return user;
    }

}
