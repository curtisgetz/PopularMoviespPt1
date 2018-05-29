package com.curtisgetz.popularmoviesppt1.data;

public class MovieVideo {


    private String mID, mName, mLanguage, mVideoKey, mSourceSite, mType;

    public MovieVideo(String mID, String mName, String mLanguage, String mVideoKey
            , String mSourceSite, String mType) {
        this.mID = mID;
        this.mName = mName;
        this.mLanguage = mLanguage;
        this.mVideoKey = mVideoKey;
        this.mSourceSite = mSourceSite;
        this.mType = mType;
    }


    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmLanguage() {
        return mLanguage;
    }

    public void setmLanguage(String mLanguage) {
        this.mLanguage = mLanguage;
    }

    public String getmVideoKey() {
        return mVideoKey;
    }

    public void setmVideoKey(String mVideoKey) {
        this.mVideoKey = mVideoKey;
    }

    public String getmSourceSite() {
        return mSourceSite;
    }

    public void setmSourceSite(String mSourceSite) {
        this.mSourceSite = mSourceSite;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }





}
