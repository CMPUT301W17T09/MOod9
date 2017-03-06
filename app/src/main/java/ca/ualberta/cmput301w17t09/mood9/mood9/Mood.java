package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.util.Date;

/**
 * Created by dannick on 2/22/17.
 */

public class Mood {
    // The following fields are to be serialized
    @JestId
    private String id;
    private Double latitude;
    private Double longitutde;
    private String trigger;
    private String emotionId;
    private String socialSituationId;
    private String imageTriggerId;
    private Date date;
    private String user_id;

    // The following fields should not be serialized
    private User user;
    private Emotion emotion;
    private SocialSituation socialSituation;


    public Mood(String id, Double latitude, Double longitutde,
                String trigger, String emotionId, String socialSituationId,
                String imageTriggerId, Date date,String user_id) {
        this.id = id;
        this.latitude = latitude;
        this.longitutde = longitutde;
        this.trigger = trigger;
        this.emotionId = emotionId;
        this.socialSituationId = socialSituationId;
        this.imageTriggerId = imageTriggerId;
        this.date = date;
        this.user_id = user_id;
    }


    public Mood(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitutde() {
        return longitutde;
    }

    public void setLongitutde(Double longitutde) {
        this.longitutde = longitutde;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getEmotionId() {
        return emotionId;
    }

    public void setEmotionId(String emotionId) {
        this.emotionId = emotionId;
        this.emotion = EmotionModel.getEmotion(emotionId);
    }

    public String getSocialSituationId() {
        return socialSituationId;
    }

    public void setSocialSituationId(String socialSituationId) {
        this.socialSituationId = socialSituationId;
        this.socialSituation = SocialSituationModel.getSocialSituation(socialSituationId);
    }

    public String getImageTriggerId() {
        return imageTriggerId;
    }

    public void setImageTriggerId(String imageTriggerId) {
        this.imageTriggerId = imageTriggerId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public User getUser() {
        return user;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public SocialSituation getSocialSituation() {
        return socialSituation;
    }
}
