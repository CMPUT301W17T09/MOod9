package ca.ualberta.cmput301w17t09.mood9.mood9;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by dannick on 2/22/17.
 */

public class MoodModel {
    protected static LoadingCache<String, Mood> moods;
    protected static LinkedList<MoodTask> tasks;

    private enum TaskType {
        PUT, DELETE
    }

    private class MoodTask{
        private Mood mood;
        private TaskType type;

        public MoodTask(Mood mood, TaskType type) {
            this.mood = mood;
            this.type = type;
        }

        public Mood getMood() {
            return mood;
        }

        public TaskType getType() {
            return type;
        }
    }

    public static Mood getMood(String id){
        Mood m = null;
        try {
            m = moods.get(id);
        } catch (ExecutionException e) {
            // TODO
        }
        return m;
    }

    public static void loadMood() {
        MoodModel.moods = CacheBuilder.newBuilder()
                .maximumSize(100) // maximum 100 records can be cached
                .expireAfterAccess(30, TimeUnit.MINUTES) // cache will expire after 30 minutes of access
                .build(new CacheLoader<String, Mood>(){ // build the cacheloader

                    @Override
                    public Mood load(String id) throws Exception {
                        //make the expensive call
                        return getFromElasticSearch(id);
                    }
                });
    }

    public static ConcurrentMap<String, Mood> getMoods(){
        return moods.asMap();
    }

    public static Mood getFromElasticSearch(String id){
        // TODO
        return new Mood();
    }

    public static void setOnElasticSearch(Mood mood){
        //Send mood to elastic search
    }

    public static ConcurrentMap<String, Mood> getMyMoods(){
        // TODO
        return moods.asMap();
    }

    public static ConcurrentMap<String, Mood> getFollowedMoods(User user){
        // TODO
        return moods.asMap();
    }

    public static ConcurrentMap<String, Mood> getUniversalMoods(){
        // TODO
        return moods.asMap();
    }

    public static ConcurrentMap<String, Mood> getMoodsNear(Double latitude, Double longitude){
        // TODO
        return moods.asMap();
    }

    public static void deleteMood(Mood mood){

    }

    public static void updateMood(Mood mood){

    }
}
