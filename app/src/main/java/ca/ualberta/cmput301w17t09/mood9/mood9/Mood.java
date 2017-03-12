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

public class Mood implements Parcelable {
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


    protected Mood(Parcel in) {
        id = in.readString();
        latitude = in.readByte() == 0x00 ? null : in.readDouble();
        longitutde = in.readByte() == 0x00 ? null : in.readDouble();
        trigger = in.readString();
        emotionId = in.readString();
        socialSituationId = in.readString();
        imageTriggerId = in.readString();
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        user_id = in.readString();
        user = (User) in.readValue(User.class.getClassLoader());
        emotion = (Emotion) in.readValue(Emotion.class.getClassLoader());
        socialSituation = (SocialSituation) in.readValue(SocialSituation.class.getClassLoader());
        mApplication = (Mood9Application) in.readValue(Mood9Application.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        if (latitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(latitude);
        }
        if (longitutde == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(longitutde);
        }
        dest.writeString(trigger);
        dest.writeString(emotionId);
        dest.writeString(socialSituationId);
        dest.writeString(imageTriggerId);
        dest.writeLong(date != null ? date.getTime() : -1L);
        dest.writeString(user_id);
        dest.writeValue(user);
        dest.writeValue(emotion);
        dest.writeValue(socialSituation);
        dest.writeValue(mApplication);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Mood> CREATOR = new Parcelable.Creator<Mood>() {
        @Override
        public Mood createFromParcel(Parcel in) {
            return new Mood(in);
        }

        @Override
        public Mood[] newArray(int size) {
            return new Mood[size];
        }
    };
}