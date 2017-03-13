package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.searchbox.annotations.JestId;

/**
 * Created by dannick on 2/22/17.
 */

public class User {
    @JestId
    private String id;
    private String name;
    private ArrayList<String> followees;

    public User(String name) {
        this.name = name;
        this.followees = new ArrayList<String>();
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

    public void addFollowee(User followee) {
        this.followees.add(followee.getId());
    }

    public void removeFollowee(User followee) {
        this.followees.remove(followee.getId());
    }

    public ArrayList<String> getFollowees() {
        return this.followees;
    }

}
