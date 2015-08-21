package com.portigon.ratingservices.ratingdb.data;

/**
 * Created by Moritz on 14.08.2015.
 *
 * Class for rating information
 */

public class RatingSheet {
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

    @com.google.gson.annotations.SerializedName("comment")
    public String mComment;

    @com.google.gson.annotations.SerializedName("weight")
    public Double mWeight;

    @com.google.gson.annotations.SerializedName("riskGroup")
    public String mRiskGroup;

    @com.google.gson.annotations.SerializedName("ratingSection")
    public String mRatingSection;

    @com.google.gson.annotations.SerializedName("ratio")
    public Double mRatio;

    @com.google.gson.annotations.SerializedName("name")
    public String mName;

    @com.google.gson.annotations.SerializedName("ratingGuid")
    public String mRatingGuid;

    @com.google.gson.annotations.SerializedName("partialRatingId")
    public String mPartialRatingId;

}

