package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dannick on 2/22/17.
 * Converted to parcelable class by cdkushni on 3/8/17
 */

public class Emotion implements Parcelable {
    private String id;
    private String name;
    private String color;
    private String description;
    private String imageName;

    public Emotion(){

    }

    public Emotion(String id, String name, String color, String description, String imageName) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.description = description;
        this.imageName = imageName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public String getImageName() {
        return imageName;
    }

    protected Emotion(Parcel in) {
        id = in.readString();
        name = in.readString();
        color = in.readString();
        description = in.readString();
        image_name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(color);
        dest.writeString(description);
        dest.writeString(image_name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Emotion> CREATOR = new Parcelable.Creator<Emotion>() {
        @Override
        public Emotion createFromParcel(Parcel in) {
            return new Emotion(in);
        }

        @Override
        public Emotion[] newArray(int size) {
            return new Emotion[size];
        }
    };
}