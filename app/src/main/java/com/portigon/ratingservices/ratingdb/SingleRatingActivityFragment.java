package com.portigon.ratingservices.ratingdb;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class SingleRatingActivityFragment extends Fragment {

    public static final String RATING_ID = "ratingGUID";

    public static final String[] groupTitles = {"Group 1", "Group 2"};

    static private final String GROUP_NAME = "RATING_SHEET_PART";


    private ArrayList<HashMap<String, String>> mGroupData;

    public SingleRatingActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_single_rating, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.single_rating_text_view);

        textView.setText("ratingId: " + getActivity().getIntent().getStringExtra(RATING_ID));



        mGroupData = new ArrayList<>();
        for (String groupTitle: groupTitles) {
            HashMap<String, String> map = new HashMap<>();
            map.put(GROUP_NAME, groupTitle);
            mGroupData.add(map);
        }

        List<List<Map<String, String>>> listOfChildGroups = new ArrayList<>();

        List<Map<String, String>> childGroupForFirstGroupRow = new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>() {{
                put("CHILD_NAME", "child in group 1");
            }});
            add(new HashMap<String, String>() {{
                put("CHILD_NAME", "child in group 1");
            }});
        }};
        listOfChildGroups.add(childGroupForFirstGroupRow);

        List<Map<String, String>> childGroupForSecondGroupRow = new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>() {{
                put("CHILD_NAME", "child in group 2");
            }});
            add(new HashMap<String, String>() {{
                put("CHILD_NAME", "child in group 2");
            }});
        }};
        listOfChildGroups.add(childGroupForSecondGroupRow);

        SimpleExpandableListAdapter expViewAdapter = new SimpleExpandableListAdapter(
                getActivity(),
                mGroupData,
                R.layout.rating_sheet_group_view,
                new String[] {GROUP_NAME},
                new int[] {R.id.rating_sheet_group_name},
                listOfChildGroups,
                R.layout.rating_sheet_child_view,
                new String[] {"CHILD_NAME"},
                new int[] {R.id.rating_sheet_child_name});

        ExpandableListView expandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
        expandableListView.setAdapter(expViewAdapter);
        return rootView;
    }
}
