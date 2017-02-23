package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dannick on 2/22/17.
 */

public class SocialSituationModel {
    protected static ConcurrentHashMap<String, SocialSituation> socialSituations;

    public static SocialSituation getSocialSituation(String id){
        return socialSituations.get(id);
    }

    public static void loadSocialSituations() {
        SocialSituationModel.socialSituations = new ConcurrentHashMap<String, SocialSituation>();
    }

    public static ConcurrentHashMap<String, SocialSituation> getSocialSituations(){
        return socialSituations;
    }
}
