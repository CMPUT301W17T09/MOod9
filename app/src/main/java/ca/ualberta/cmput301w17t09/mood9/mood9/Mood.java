package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import io.searchbox.annotations.JestId;

/**
 * Created by dannick on 2/22/17.
 * Converted to parcelable class by cdkushni on 3/8/17
 * Disabled this.emotion and this.socialSituation setting due to unusability at the moment by cdkushni on 3/10/17
 */

public class Mood {
    // The following fields are to be serialized
    @JestId
    private String id;
    private Double latitude;
    private Double longitutde;
    private String trigger;
    //private int emoticon;
    private String emotionId;
    private String socialSituationId;
    private String imageTriggerId;
    private Date date;
    private String user_id;

    // The following fields should not be serialized
    private User user;
    private Emotion emotion;
    private SocialSituation socialSituation;


    private Mood9Application mApplication;


    public Mood(Double latitude, Double longitutde,
                String trigger, String emotionId, String socialSituationId,
                String imageTriggerId, Date date,String user_id) {
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

    public void setmApplication(Mood9Application mApplication) {
        this.mApplication = mApplication;
    }

    public String getId() {
        return this.id;
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
        this.emotion = mApplication.getEmotionModel().getEmotion(emotionId);
    }/*
    public int getEmoticon() {
        return emoticon;
    }

    public void setEmoticon(int emoticon) {
        this.emoticon = emoticon;
    }*/

    public String getSocialSituationId() {
        return socialSituationId;
    }

    public void setSocialSituationId(String socialSituationId) {
        this.socialSituationId = socialSituationId;
        this.socialSituation = mApplication.getSocialSituationModel().getSocialSituation(socialSituationId);
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