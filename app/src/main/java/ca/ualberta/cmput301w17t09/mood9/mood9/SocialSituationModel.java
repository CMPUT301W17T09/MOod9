package ca.ualberta.cmput301w17t09.mood9.mood9;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by dannick on 2/22/17.
 */

public class SocialSituationModel {
    protected static LoadingCache<String, SocialSituation> socialSituations;

    public static SocialSituation getSocialSituation(String id){
        SocialSituation ss = null;
        try {
            ss = socialSituations.get(id);
        } catch (ExecutionException e) {
            // TODO
        }
        return ss;
    }

    public static void loadSocialSituations() {
        SocialSituationModel.socialSituations = CacheBuilder.newBuilder()
                .maximumSize(100) // maximum 100 records can be cached
                .build(new CacheLoader<String, SocialSituation>(){ // build the cacheloader

                    @Override
                    public SocialSituation load(String id) throws Exception {
                        //make the expensive call
                        return getFromXMLRessource(id);
                    }
                });
    }

    public static ConcurrentMap<String, SocialSituation> getSocialSituations(){
        return socialSituations.asMap();
    }

    public static SocialSituation getFromXMLRessource(String id){
        // TODO
        return new SocialSituation("", "", "");
    }
}
