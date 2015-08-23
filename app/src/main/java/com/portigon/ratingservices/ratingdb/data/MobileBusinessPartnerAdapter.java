package com.portigon.ratingservices.ratingdb.data;

import android.app.Activity;
import android.content.Context;
import android.media.Rating;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

        final TextView nameTextView = (TextView) row.findViewById(R.id.rating_list_item_name);
        final ImageView ratingMethodIcon = (ImageView) row.findViewById(R.id.rating_list_item_icon);
        final TextView ratingTextView = (TextView) row.findViewById(R.id.rating_list_item_rating);

        if (currentRating.mRatingMethod.equals("cor")) {
            ratingMethodIcon.setImageResource(R.drawable.cor_icon);
        } else if (currentRating.mRatingMethod.equals("sov")) {
            ratingMethodIcon.setImageResource(R.drawable.sov_icon);
        }

        nameTextView.setText(currentRating.mShortName);
        switch (currentRating.mRatingClass) {
            case "a":
                ratingTextView.setBackgroundColor(mContext.getResources().getColor(R.color.rating_a));
                break;
            case "b":
                ratingTextView.setBackgroundColor(mContext.getResources().getColor(R.color.rating_b));
                break;
            case "c":
                ratingTextView.setBackgroundColor(mContext.getResources().getColor(R.color.rating_c));
                break;
            case "d":
                ratingTextView.setBackgroundColor(mContext.getResources().getColor(R.color.rating_d));
                break;
        }
        ratingTextView.setText(currentRating.mRatingClass.toUpperCase());

        return row;
    }
}
