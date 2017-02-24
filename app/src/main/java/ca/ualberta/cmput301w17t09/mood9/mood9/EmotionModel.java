package ca.ualberta.cmput301w17t09.mood9.mood9;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by dannick on 2/22/17.
 */

public class EmotionModel {
    protected static LoadingCache<String, Emotion> emotions;

    public static Emotion getEmotion(String id){
        Emotion emo = null;
        try {
            emo = emotions.get(id);
        } catch (ExecutionException e) {
            // TODO
        }
        return emo;
    }

    public static void initEmotions() {
        EmotionModel.emotions = CacheBuilder.newBuilder()
                .maximumSize(100) // maximum 100 records can be cached
                .build(new CacheLoader<String, Emotion>(){ // build the cacheloader

                    @Override
                    public Emotion load(String id) throws Exception {
                        //make the expensive call
                        return getFromXMLRessource(id);
                    }
                });
    }

    public static ConcurrentMap<String, Emotion> getEmotions(){
        return emotions.asMap();
    }

    public static Emotion getFromXMLRessource(String id){
        // TODO
        return new Emotion("", "", "", "", "");
    }

    public static void setOnXMLResource(Emotion emotion){
        //Sets an emotion
    }

    public static void initialLoad(){

    }
}
