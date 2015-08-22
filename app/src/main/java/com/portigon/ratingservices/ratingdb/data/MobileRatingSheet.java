package com.portigon.ratingservices.ratingdb.data;

import java.util.List;

/**
 * Created by Moritz on 14.08.2015.
 *
 * Class for rating information
 */

public class MobileRatingSheet {
//
//    public static Status {
//            final static InProgress,
//            Approved(2),
//            Withdrawn(3),
//        }

    @com.google.gson.annotations.SerializedName("id")
    public String mId;

    @com.google.gson.annotations.SerializedName("ratingSectionId")
    public String mRatingSectionId;

    @com.google.gson.annotations.SerializedName("ratingID")
    public Integer mRatingId;

    @com.google.gson.annotations.SerializedName("ratingGuid")
    public String mRatingGuid;

    @com.google.gson.annotations.SerializedName("riskGroup")
    public String mRiskGroup;

    @com.google.gson.annotations.SerializedName("name")
    public String mName;

    @com.google.gson.annotations.SerializedName("partialRatingsInSection")
    public List<MobilePartialRating> mPartialRatingsInSection;
}

