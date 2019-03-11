package com.example.admin.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Complain implements Parcelable{

    private String id,uid,cid,pid;
    private String type;

    public Complain(String type) {
        this.type = type;
    }

    protected Complain(Parcel in) {
        id = in.readString();
        uid = in.readString();
        cid = in.readString();
        pid = in.readString();
        type = in.readString();
    }

    public static final Creator<Complain> CREATOR = new Creator<Complain>() {
        @Override
        public Complain createFromParcel(Parcel in) {
            return new Complain(in);
        }

        @Override
        public Complain[] newArray(int size) {
            return new Complain[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(uid);
        parcel.writeString(cid);
        parcel.writeString(pid);
        parcel.writeString(type);
    }
}
