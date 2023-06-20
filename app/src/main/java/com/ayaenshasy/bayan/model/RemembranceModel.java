package com.ayaenshasy.bayan.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class RemembranceModel implements Parcelable {
    private int id;
    private ArrayList<RemembranceDetailsModel> list;
    private String name;

    public RemembranceModel() {
    }

    public RemembranceModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    protected RemembranceModel(Parcel in) {
        name = in.readString();
        list = in.createTypedArrayList(RemembranceDetailsModel.CREATOR);
        id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(list);
        dest.writeInt(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RemembranceModel> CREATOR = new Creator<RemembranceModel>() {
        @Override
        public RemembranceModel createFromParcel(Parcel in) {
            return new RemembranceModel(in);
        }

        @Override
        public RemembranceModel[] newArray(int size) {
            return new RemembranceModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<RemembranceDetailsModel> getList() {
        return list;
    }

    public void setList(ArrayList<RemembranceDetailsModel> list) {
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


