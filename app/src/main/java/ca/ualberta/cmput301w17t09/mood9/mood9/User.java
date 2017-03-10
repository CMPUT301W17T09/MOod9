package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.util.ArrayList;

/**
 * Created by dannick on 2/22/17.
 */

public class User {
    private String id;
    private String name;
// arraylist of all users
    private ArrayList<User> following = new ArrayList<>();

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return ArrayList of people the user is following
     */
    public ArrayList<User> getFollowing() {
        return following;
    }

    public void addToFollowing(User user){
        following.add(user);
    }
    public void removeFromFollowing(User user){
        following.remove(user);
    }

}
