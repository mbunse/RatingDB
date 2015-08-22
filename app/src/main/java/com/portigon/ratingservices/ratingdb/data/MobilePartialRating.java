package com.portigon.ratingservices.ratingdb.data;

/**
 * Created by Moritz on 22.08.2015.
 *
 * DTO for partial ratings of rating sheet within rating sections
 */
public class MobilePartialRating {

    @com.google.gson.annotations.SerializedName("id")
    public String mId;

    @com.google.gson.annotations.SerializedName("comment")
    public String mComment;

    @com.google.gson.annotations.SerializedName("weight")
    public Double mWeight;

    @com.google.gson.annotations.SerializedName("riskGroup")
    public String mRiskGroup;

    @com.google.gson.annotations.SerializedName("ratio")
    public Double mRatio;

    @com.google.gson.annotations.SerializedName("name")
    public String mName;

}
