package com.portigon.ratingservices.ratingdb;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.portigon.ratingservices.ratingdb.Utility.Utility;
import com.portigon.ratingservices.ratingdb.data.MobilePartialRating;
import com.portigon.ratingservices.ratingdb.data.MobileRatingSheet;
import com.portigon.ratingservices.ratingdb.data.MobileRatingSheetAdapter;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * A placeholder fragment containing a simple view.
 */
public class SingleRatingActivityFragment extends Fragment {

    public static final String RATING_ID = "ratingGUID";
    public static final String BUSINESS_PARTNER_NAME = "bp_name";



    public final String LOG_TAG = SingleRatingActivityFragment.class.getSimpleName();

    private MobileServiceClient mClient;

    private ArrayList<HashMap<String, String>> mGroupData;

    private MobileServiceTable<MobileRatingSheet> mRatingSheetTable;

    private ExpandableListView mExpandableListView;

    private MobileRatingSheetAdapter mExpViewAdapter;

    private String mRatingGuid;

    public SingleRatingActivityFragment() {
    }

    private class ExpandFilter implements ServiceFilter {


        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {


            try {
                if (request.getUrl().contains("/tables/MobileRatingSheet") && !request.getUrl().contains("$expand")) {
                    request.setUrl(request.getUrl() + ((!request.getUrl().contains("?")) ? "?" : "&"));
                    request.setUrl(request.getUrl() + "$expand=partialRatingsInSection");
                }
            } catch (URISyntaxException e) {
                Log.e(LOG_TAG, "Error " + e.getMessage());
            }
            return nextServiceFilterCallback.onNext(request);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_single_rating, container, false);

        try {
            mClient = new MobileServiceClient(getString(R.string.api_url), getString(R.string.api_key), getActivity())
                .withFilter(new ExpandFilter());

            mRatingSheetTable = mClient.getTable(MobileRatingSheet.class);

            initLocalStore().get();

            mRatingGuid = getActivity().getIntent().getStringExtra(RATING_ID);
            ActionBar actionBar = getActivity().getActionBar();
            if (actionBar != null) {
                actionBar.setTitle(getActivity().getIntent().getStringExtra(BUSINESS_PARTNER_NAME));
            }


            mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
            refreshItemsFromTable();

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "There was an error creating the Mobile Service. Verify the URL " + e.getMessage());
        }
        //Next three exceptions may stem from initLocalStore()
        catch (Exception e) {
            Log.e(LOG_TAG, "Error " + e.getMessage());
        }




        return rootView;
    }


    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    private List<MobileRatingSheet> refreshItemsFromMobileServiceTable() throws MobileServiceException, ExecutionException, InterruptedException {
        return mRatingSheetTable.where().field("ratingGuid").eq(mRatingGuid).execute().get();


    }

    /**
     * Initialize local storage
     * @return Void
     */
    private AsyncTask<Void, Void, Void> initLocalStore() {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> tableDefinition = new HashMap<>();
                    tableDefinition.put("id", ColumnDataType.String);
                    tableDefinition.put("ratingSectionId", ColumnDataType.Integer);
                    tableDefinition.put("ratingID", ColumnDataType.Integer);
                    tableDefinition.put("ratingGuid", ColumnDataType.String);
                    tableDefinition.put("riskGroup", ColumnDataType.String);
                    tableDefinition.put("name", ColumnDataType.String);

                    localStore.defineTable("RatingSheet", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }

                return null;
            }
        };

        return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Refresh the list with the items in the Table
     */
    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<MobileRatingSheet> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<ToDoItem> results = refreshItemsFromMobileServiceTableSyncTable();
                    mGroupData = new ArrayList<>();
                    List<List<Map<String, String>>> listOfChildGroups = new ArrayList<>();
                    for (MobileRatingSheet item : results) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put(MobileRatingSheetAdapter.GROUP_NAME, item.mName);
                        map.put(MobileRatingSheetAdapter.GROUP_RATING, item.mRiskGroup.toUpperCase());
                        mGroupData.add(map);
                        List<Map<String, String>> childGroupForGroupRow = new ArrayList<>();
                        for (MobilePartialRating partialRating : item.mPartialRatingsInSection) {
                            Map<String, String> childTextMap = new HashMap<>();
                            childTextMap.put(MobileRatingSheetAdapter.CHILD_NAME, partialRating.mName);
                            childTextMap.put(MobileRatingSheetAdapter.CHILD_RATING, partialRating.mRiskGroup.toUpperCase());
                            childGroupForGroupRow.add(childTextMap);
                        }
                        listOfChildGroups.add(childGroupForGroupRow);
                    }


                    mExpViewAdapter = new MobileRatingSheetAdapter(
                            getActivity(),
                            mGroupData,
                            R.layout.rating_sheet_group_view,
                            new String[]{MobileRatingSheetAdapter.GROUP_NAME, MobileRatingSheetAdapter.GROUP_RATING},
                            new int[]{R.id.rating_sheet_group_name, R.id.rating_sheet_group_item_rating},
                            listOfChildGroups,
                            R.layout.rating_sheet_child_view,
                            new String[]{MobileRatingSheetAdapter.CHILD_NAME, MobileRatingSheetAdapter.CHILD_RATING},
                            new int[]{R.id.rating_sheet_child_name, R.id.rating_sheet_child_item_rating});

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            View rootView = getView();
                            if (rootView != null) {

                                mExpandableListView.setAdapter(mExpViewAdapter);
                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                int width = displayMetrics.widthPixels;
                                mExpandableListView.setIndicatorBoundsRelative(
                                        width - Utility.GetPixelFromDips(getActivity(), 50),
                                        width - Utility.GetPixelFromDips(getActivity(),10)
                                );
                            }

                        }
                    });
                } catch (final Exception e){
                    Log.e(LOG_TAG, e.getMessage());
                }

                return null;
            }
        };

        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
