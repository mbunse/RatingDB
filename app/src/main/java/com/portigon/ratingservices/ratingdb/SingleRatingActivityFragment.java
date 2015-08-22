package com.portigon.ratingservices.ratingdb;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

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
import com.portigon.ratingservices.ratingdb.data.MobilePartialRating;
import com.portigon.ratingservices.ratingdb.data.MobileRatingSheet;

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

    static private final String GROUP_NAME = "RATING_SHEET_PART";

    public final String LOG_TAG = SingleRatingActivityFragment.class.getSimpleName();

    private MobileServiceClient mClient;

    private ArrayList<HashMap<String, String>> mGroupData;

    private MobileServiceTable<MobileRatingSheet> mRatingSheetTable;

    private SimpleExpandableListAdapter mExpViewAdapter;

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
            TextView textView = (TextView) rootView.findViewById(R.id.single_rating_text_view);
            textView.setText("ratingId: " + mRatingGuid);

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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mGroupData = new ArrayList<>();
                            List<List<Map<String, String>>> listOfChildGroups = new ArrayList<>();
                            for (MobileRatingSheet item : results) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put(GROUP_NAME, item.mName);
                                mGroupData.add(map);
                                List<Map<String, String>> childGroupForGroupRow = new ArrayList<>();
                                for (MobilePartialRating partialRating : item.mPartialRatingsInSection) {
                                    Map<String, String> childTextMap = new HashMap<>();
                                    childTextMap.put("CHILD_NAME", partialRating.mName);
                                    childGroupForGroupRow.add(childTextMap);
                                }
                                listOfChildGroups.add(childGroupForGroupRow);
                            }


                            mExpViewAdapter = new SimpleExpandableListAdapter(
                                    getActivity(),
                                    mGroupData,
                                    R.layout.rating_sheet_group_view,
                                    new String[]{GROUP_NAME},
                                    new int[]{R.id.rating_sheet_group_name},
                                    listOfChildGroups,
                                    R.layout.rating_sheet_child_view,
                                    new String[]{"CHILD_NAME"},
                                    new int[]{R.id.rating_sheet_child_name});

                            View rootView = getView();
                            if (rootView != null) {
                                ExpandableListView expandableListView = (ExpandableListView) getView().findViewById(R.id.expandableListView);
                                expandableListView.setAdapter(mExpViewAdapter);
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
