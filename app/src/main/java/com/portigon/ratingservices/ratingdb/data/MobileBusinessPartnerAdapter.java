package com.portigon.ratingservices.ratingdb.data;

import android.app.Activity;
import android.content.Context;
import android.media.Rating;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.portigon.ratingservices.ratingdb.R;

/**
 * Created by Moritz on 14.08.2015.
 *
 * Adapter for ListView with ratings
 *
 * @see Rating
 * @see com.portigon.ratingservices.ratingdb.MainActivityFragment
 */
public class MobileBusinessPartnerAdapter extends ArrayAdapter<MobileBusinessPartner>{

    private final Context mContext;

    private final int mLayoutResId;

    public MobileBusinessPartnerAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
        mContext = context;
        mLayoutResId = layoutResId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final MobileBusinessPartner currentRating = getItem(position);

        if (row==null) {
            //Do not attach to root
            row = ((Activity) mContext).getLayoutInflater().inflate(mLayoutResId, parent, false);
        }

        row.setTag(currentRating);

        final TextView textView = (TextView) row.findViewById(R.id.list_item_bp_rating);
        textView.setText("BP: " + currentRating.mShortName +
                ", Method: " + currentRating.mRatingMethod.toUpperCase() +
                ", Rating: " + currentRating.mRatingClass.toUpperCase());

        return textView;
    }
}
