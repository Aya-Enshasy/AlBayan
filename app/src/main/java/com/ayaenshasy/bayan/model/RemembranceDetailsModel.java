package com.ayaenshasy.bayan.model;

import android.os.Parcel;
import android.os.Parcelable;


public class RemembranceDetailsModel implements Parcelable {

    private int repeat;
    private String text;

    public RemembranceDetailsModel() {
    }



    protected RemembranceDetailsModel(Parcel in) {
         text = in.readString();
        repeat = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeInt(repeat);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RemembranceDetailsModel> CREATOR = new Creator<RemembranceDetailsModel>() {
        @Override
        public RemembranceDetailsModel createFromParcel(Parcel in) {
            return new RemembranceDetailsModel(in);
        }

        @Override
        public RemembranceDetailsModel[] newArray(int size) {
            return new RemembranceDetailsModel[size];
        }
    };


    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
