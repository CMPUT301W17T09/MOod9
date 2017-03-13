package ca.ualberta.cmput301w17t09.mood9.mood9;

/**
 * Created by dannick on 2/22/17.
 */

public class UserModel {

    public static User getUser(String username){

        ElasticSearchMOodController.GetUsersTask getUsersTask = new ElasticSearchMOodController.GetUsersTask();
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

    public static void loadUsers() {}
}
