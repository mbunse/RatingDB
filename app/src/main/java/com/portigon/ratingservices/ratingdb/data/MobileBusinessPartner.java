package com.portigon.ratingservices.ratingdb.data;

/**
 * Created by Moritz on 14.08.2015.
 *
 * Class for rating information
 */
@SuppressWarnings("unused")
public class MobileBusinessPartner {
//
//    public static Status {
//            final static InProgress,
//            Approved(2),
//            Withdrawn(3),
//        }

    @com.google.gson.annotations.SerializedName("id")
    public String mId;

    @com.google.gson.annotations.SerializedName("ratingId")
    public Integer mRatingId;

    @com.google.gson.annotations.SerializedName("ratingBpId")
    public String mRatingBpId;

    @com.google.gson.annotations.SerializedName("businessPartnerId")
    public String mBusinessPartnerId;

    @com.google.gson.annotations.SerializedName("shortName")
    public String mShortName;

    @com.google.gson.annotations.SerializedName("ratingClass")
    public String mRatingClass;

    @com.google.gson.annotations.SerializedName("ratingMethod")
    public String mRatingMethod;

}

