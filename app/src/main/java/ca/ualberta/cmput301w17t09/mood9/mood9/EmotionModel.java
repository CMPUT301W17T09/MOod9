package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.util.HashMap;

/**
 * Created by dannick on 2/22/17.
 */

public class EmotionModel {
    protected static HashMap<String, Emotion> emotions;

    public static Emotion getEmotion(String id){
        return emotions.get(id);
    }

    public static void loadEmotions() {
        EmotionModel.emotions = new HashMap<String, Emotion>();
    }

    public static HashMap<String, Emotion> getEmotions(){
        return emotions;
    }
}
