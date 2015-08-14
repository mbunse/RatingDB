package com.portigon.ratingservices.ratingdb.data;

import java.util.Date;

/**
 * Created by Moritz on 14.08.2015.
 *
 * Class for rating information
 */
@SuppressWarnings("unused")
public class Rating {
//
//    public static Status {
//            final static InProgress,
//            Approved(2),
//            Withdrawn(3),
//        }

    @com.google.gson.annotations.SerializedName("id")
    public String mId;

    @com.google.gson.annotations.SerializedName("ratingStatus")
    public String mRatingStatus;

    //DateTime in C#
    @com.google.gson.annotations.SerializedName("validUntil")
    public Date ValidUntil;

//        public enum InternalRatingClass
//        {
//            A0(0),
//            A1(1),
//            A2(2),
//            A3( 3),
//            A4( 4),
//            A5( 5),
//            B1( 6),
//            B2( 7),
//            B3( 8),
//            B4( 9),
//            B5( 10),
//            E (11)
//        }

    @com.google.gson.annotations.SerializedName("ratingClass")
    public String mRatingClass;

//        public enum RatingMethodType
//        {
//            COR(1),
//            SOV(2)
//        }

    @com.google.gson.annotations.SerializedName("ratingMethod")
    public String mRatingMethod;

    @com.google.gson.annotations.SerializedName("businessPartnerID")
    public String mBusinessPartnerID;
}

