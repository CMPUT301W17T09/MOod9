package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by dannick on 2/22/17.
 */

public class EmotionModel {
    protected static ConcurrentHashMap<String, Emotion> emotions;

    public static Emotion getEmotion(String id){
        return emotions.get(id);
    }

    public static void loadEmotions() {
        EmotionModel.emotions = new ConcurrentHashMap<String, Emotion>();
    }

    public static ConcurrentMap<String, Emotion> getEmotions(){
        return emotions;
    }
}
