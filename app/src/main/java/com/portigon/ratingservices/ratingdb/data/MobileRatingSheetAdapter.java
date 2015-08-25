package com.portigon.ratingservices.ratingdb.data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.portigon.ratingservices.ratingdb.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Moritz on 25.08.2015.
 *
 * ExpandableListAdapter implementing the change of colors for different ratings
 */
public class MobileRatingSheetAdapter extends SimpleExpandableListAdapter {
    static public final String GROUP_NAME = "GROUP_NAME";
    static public final String GROUP_RATING = "GROUP_RATING";
    static public final String CHILD_NAME = "CHILD_NAME";
    static public final String CHILD_RATING = "CHILD_RATING";

    private Context mContext;

    private int mChildLayout;

    public MobileRatingSheetAdapter(Context context, List<? extends Map<String, ?>> groupData,
                                    int groupLayout,
                                    String[] groupFrom, int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData, int childLayout, String[] childFrom, int[] childTo) {
        super(context, groupData, groupLayout, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
        mContext = context;
        mChildLayout = childLayout;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);

        TextView ratingTextView = (TextView) view.findViewById(R.id.rating_sheet_child_item_rating);
        @SuppressWarnings("unchecked")
        String rating = ((Map<String, String>) getChild(groupPosition, childPosition)).get(CHILD_RATING);

        switch (rating) {
            case "A":
                ratingTextView.setBackgroundColor(mContext.getResources().getColor(R.color.rating_a));
                break;
            case "B":
                ratingTextView.setBackgroundColor(mContext.getResources().getColor(R.color.rating_b));
                break;
            case "C":
                ratingTextView.setBackgroundColor(mContext.getResources().getColor(R.color.rating_c));
                break;
            case "D":
                ratingTextView.setBackgroundColor(mContext.getResources().getColor(R.color.rating_d));
                break;

        }

        return view;
    }
}
