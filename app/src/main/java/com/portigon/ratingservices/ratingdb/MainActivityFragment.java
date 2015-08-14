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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of fragment listing BusinessPartners with Ratings
 *
 * @author Moritz
 * @version 2015.0814
 * @since 1.0
 */
public class MainActivityFragment extends Fragment {

    ArrayAdapter<String> mRatingsAdapter;

    public MainActivityFragment() {
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
                FetchBpRatingsTask fetchBpRatingsTask = new FetchBpRatingsTask();
                fetchBpRatingsTask.execute();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Dummy data
        String[] data = {
                "Corporate X - Rated B",
                "Project F - Rated D",
                "Corporate D - Rated C",
                "Project J - Rated B",
                "Sovereign A - Rated A",
                "Sovereign C - Rated B",
                "Corporate Fu - Rated C",
                "Corporate E - Rated A",
                "Project Ni - Rated B",
                "Corporate Ds - Rated B",
                "Corporate R - Rated C"
        };

        List<String> BpRatings = new ArrayList<>(Arrays.asList(data));
        mRatingsAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_bp_rating,
                R.id.list_item_bp_rating,
                BpRatings
        );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listView_bp_ratings);
        listView.setAdapter(mRatingsAdapter);
        return rootView;
    }


    /**
     * Created by Moritz on 14.08.2015.
     */
    public class FetchBpRatingsTask extends AsyncTask<Void, Void, String[]> {
        final String LOG_TAG = FetchBpRatingsTask.class.getSimpleName();

        protected String[] doInBackground(Void... params) {
            // Will be closes in finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // JSON response string from rating db web API.
            String bpRatingsJsonString = null;

            try {
                //Current URL for testing:
                URL url = new URL("http://ratingtool.azure-mobile.net/tables/BusinessPartner?$expand=currentRating");

                // Open connection and send GET request.
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Add newline for debugging purposes
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // No data -> no further action.
                    return null;
                }
                bpRatingsJsonString = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                //Catch http connection exceptions
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getBpRatingsFromJsonString(bpRatingsJsonString);
            } catch (JSONException e) {
                // Catch error in JSON parsing
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return null;
        }

        private String[] getBpRatingsFromJsonString(String jsonString) throws JSONException {

            final String RDBAPI_NAME = "shortName";
            final String RDBAPI_RATING = "currentRating";
            //Returned JSON looks like:
            //[{"id":"1","shortName":"Company A","currentRating":null},{"id":"2","shortName":"Sovereign B","currentRating":null}]

            JSONArray bpRatingsJsonArray = new JSONArray(jsonString);
            int nRatings = bpRatingsJsonArray.length();
            String[] resultsString = new String[nRatings];
            for (int i = 0; i < nRatings; ++i) {
                JSONObject bpRatingJsonObj = bpRatingsJsonArray.getJSONObject(i);
                resultsString[i] = bpRatingJsonObj.getString(RDBAPI_NAME)
                        + bpRatingJsonObj.getString(RDBAPI_RATING);
            }
            return resultsString;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                mRatingsAdapter.clear();
                mRatingsAdapter.addAll(Arrays.asList(strings));
            }
            super.onPostExecute(strings);
        }
    }
}