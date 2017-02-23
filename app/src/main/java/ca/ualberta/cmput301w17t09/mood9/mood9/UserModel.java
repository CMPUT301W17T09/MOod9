package ca.ualberta.cmput301w17t09.mood9.mood9;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.commons.lang3.concurrent.ConcurrentException;

import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by dannick on 2/22/17.
 */

public class UserModel {
    protected static LoadingCache<String, User> users;

    public static User getUser(String id){
        User u = null;
        try {
            u = users.get(id);
        } catch (ExecutionException e) {
            // TODO
        }
        return u;
    }

    public static void loadUsers() {
        UserModel.users = CacheBuilder.newBuilder()
                .maximumSize(100) // maximum 100 records can be cached
                .expireAfterAccess(30, TimeUnit.MINUTES) // cache will expire after 30 minutes of access
                .build(new CacheLoader<String, User>(){ // build the cacheloader

                    @Override
                    public User load(String id) throws Exception {
                        //make the expensive call
                        return getFromElasticSearch(id);
                    }
                });
    }

    public static void loadUser(String id) {

    }

    public static ConcurrentMap<String, User> getUsers(){
        return users.asMap();
    }

    public static User getFromElasticSearch(String id){
        // TODO
        return new User("", "");
    }
}
