package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.util.Date;

/**
 * Created by dannick on 2/22/17.
 */

public class Mood {
    // The following fields are to be serialized
    private String id;
    private Double latitude;
    private Double longitutde;
    private String trigger;
    private String emotionId;
    private String socialSituationId;
    private String imageTriggerId;
    Date date;

    // The following fields should not be serialized
    private User user;
    private Emotion emotion;
    private SocialSituation socialSituation;

}
