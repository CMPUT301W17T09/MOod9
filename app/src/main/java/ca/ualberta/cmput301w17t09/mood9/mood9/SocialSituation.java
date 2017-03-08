package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dannick on 2/22/17.
 * Converted to parcelable class by cdkushni on 3/8/17
 */

public class SocialSituation implements Parcelable {
    private String id;
    private String name;
    private String description;

    public SocialSituation(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    protected SocialSituation(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SocialSituation> CREATOR = new Parcelable.Creator<SocialSituation>() {
        @Override
        public SocialSituation createFromParcel(Parcel in) {
            return new SocialSituation(in);
        }

        @Override
        public SocialSituation[] newArray(int size) {
            return new SocialSituation[size];
        }
    };
}