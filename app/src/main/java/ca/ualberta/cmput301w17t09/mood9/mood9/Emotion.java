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
    private String image_name;

    public Emotion(String id, String name, String color, String description, String image_name) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.description = description;
        this.image_name = image_name;
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
        return image_name;
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