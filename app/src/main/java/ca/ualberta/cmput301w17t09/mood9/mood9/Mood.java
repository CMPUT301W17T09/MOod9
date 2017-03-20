package ca.ualberta.cmput301w17t09.mood9.mood9;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import io.searchbox.annotations.JestId;

/**
 * Created by dannick on 2/22/17.
 * Converted to parcelable class by cdkushni on 3/8/17
 * Disabled this.emotion and this.socialSituation setting due to unusability at the moment by cdkushni on 3/10/17
 */

public class Mood implements Serializable {
    // The following fields are to be serialized
    @JestId
    private String id;
    private String offlineid;
    private Double latitude;
    private Double longitude;
    private String trigger;
    //private int emoticon;
    private String emotionId;
    private String socialSituationId;
    private String imageTriggerId;
    private Date date;
    private String user_id;

    public Mood(Double latitude, Double longitude,
                String trigger, String emotionId, String socialSituationId,
                String imageTriggerId, Date date,String user_id) {
        //http://stackoverflow.com/questions/1389736/how-do-i-create-a-unique-id-in-java
        this.offlineid = UUID.randomUUID().toString();
        this.latitude = latitude;
        this.longitude = longitude;
        this.trigger = trigger;
        this.emotionId = emotionId;
        this.socialSituationId = socialSituationId;
        this.imageTriggerId = imageTriggerId;
        this.date = date;
        this.user_id = user_id;
    }


    public Mood(){

    }
    public String getOfflineid() {
        return offlineid;
    }

    public void setOfflineid(String offlineid) {
        this.offlineid = offlineid;
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

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
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

    }

    public String getSocialSituationId() {
        return socialSituationId;
    }

    public void setSocialSituationId(String socialSituationId) {
        this.socialSituationId = socialSituationId;
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
}
