package com.portigon.ratingservices.ratingdb;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.portigon.ratingservices.ratingdb.data.BpCurrentRating;
import com.portigon.ratingservices.ratingdb.data.BpCurrentRatingAdapter;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of fragment listing BusinessPartners with Ratings
 *
 * @author Moritz
 * @version 2015.0814
 * @since 1.0
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;
    private MobileServiceTable<BpCurrentRating> mRatingTable;

    private BpCurrentRatingAdapter mRatingsAdapter;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        try {
            mClient = new MobileServiceClient(getString(R.string.api_url), getString(R.string.api_key), getActivity());

            mRatingTable = mClient.getTable(BpCurrentRating.class);

            initLocalStore().get();

            // Create an adapter to bind the items with the view
            mRatingsAdapter = new BpCurrentRatingAdapter(getActivity(), R.layout.list_item_bp_rating);
            ListView listView = (ListView) rootView.findViewById(R.id.listView);
            listView.setAdapter(mRatingsAdapter);

            // Load the items from the Mobile Service
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bp_ratings_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                refreshItemsFromTable();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
                    tableDefinition.put("ratingId", ColumnDataType.Integer);
                    tableDefinition.put("businessPartnerId", ColumnDataType.Integer);
                    tableDefinition.put("ratingBpId", ColumnDataType.String);
                    tableDefinition.put("shortName", ColumnDataType.String);
                    tableDefinition.put("ratingClass", ColumnDataType.String);
                    tableDefinition.put("ratingMethod", ColumnDataType.String);

                    localStore.defineTable("Rating", tableDefinition);

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
     * Refresh the list with the items in the Mobile Service Table
     */

    private List<BpCurrentRating> refreshItemsFromMobileServiceTable() throws MobileServiceException, ExecutionException, InterruptedException {
        return mRatingTable.execute().get();


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
                    final List<BpCurrentRating> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<ToDoItem> results = refreshItemsFromMobileServiceTableSyncTable();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRatingsAdapter.clear();

                            for (BpCurrentRating item : results) {
                                mRatingsAdapter.add(item);
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