package com.comp4521.bookscan.MainLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.comp4521.bookscan.BookInfoToServer;
import com.comp4521.bookscan.BookScanActivity;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.bookscan.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// import android.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookGridFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookGridFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // logcat label
    private static final String LABEL = "BookGridFragment";
    public final static String TAG = "BookGridFragment";


    /**
     * title of the fragment
     */
    private String mtitle;

    private Date lastRefreshTime;

    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeLayout;
    private SimpleAdapter gridListAdapter;
    private GridView gridView;

    private String[] imgText;
    private String[] imgAuthor;
    private String[] imgCover;
    private String[] imgbookIDs;

    Parcelable state;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param bookType title from nav draw, indicate type of book (borrow or donated)
     * @return A new instance of fragment BookGridFragment.
     */
    public static BookGridFragment newInstance(String bookType) {
        BookGridFragment fragment = new BookGridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, bookType);
        fragment.setArguments(args);
        return fragment;
    }

    public BookGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            mtitle = getArguments().getString(ARG_PARAM1);
            Log.i(LABEL, mtitle);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_book_grid_list, container, false);


        new RefreshMyLibraryTask().execute();


        gridView = (GridView)rootView.findViewById(R.id.main_page_gridview);
        gridView.setOnItemClickListener(this);


        // handle swipe layout
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(onSwipeToRefresh);
        swipeLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);


        // handle the action button
        ActionButton actionButton = (ActionButton) rootView.findViewById(R.id.action_button);

        if(mtitle != getString(R.string.title_section3)){
            actionButton.dismiss();
        } else {
            actionButton.setOnClickListener(this);
        }


        if(state != null) {
            Log.d(TAG, "trying to restore listview state..");
            gridView.onRestoreInstanceState(state);
        }

        return rootView;
    }

    private SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeLayout.setRefreshing(true);

            new RefreshMyLibraryTask().execute();
        }
    };
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            ((MainActivity) activity).onSectionAttached(getArguments().getString(ARG_PARAM1));
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        // Save ListView state @ onPause
        Log.d(TAG, "saving listview state @ onPause");
        state = gridView.onSaveInstanceState();
        super.onPause();
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, BookDetailFragment.newInstance(imgCover[position],
                imgText[position], imgAuthor[position], imgbookIDs[position], mtitle));
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_button:
                Intent intent = new Intent(getActivity(), BookScanActivity.class);
                intent.putExtra("type", "lend"); // TODO can be "donate"
                startActivity(intent);
                break;
        }

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    private class RefreshMyLibraryTask extends AsyncTask<Void, Void, Void> {

        private boolean connectSuccess;

        @Override
        protected Void doInBackground(Void... params) {

            String time = new Date().toString();
            ReadFromServer server = new ReadFromServer();
            // TODO change userID after register complete
            JSONObject receivedJson;
            if(mtitle == getString(R.string.title_section3)) {
                receivedJson = server.getBookListOfOneUser("useriduselessfornow");
            } else if(mtitle == getString(R.string.title_section2)) {
                receivedJson = server.getBookListAll("useriduselessfornow");
            } else {
                receivedJson = server.getBookListAll("useriduselessfornow");
            }

            if(receivedJson != null) {
                try {
                    imgAuthor = receviedJsonTolocalArray(receivedJson,"author");
                    imgCover = receviedJsonTolocalArray(receivedJson,"cover");
                    imgbookIDs = receviedJsonTolocalArray(receivedJson,"server_book_id");
                    imgText = receviedJsonTolocalArray(receivedJson,"name");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                connectSuccess = true;

            } else {
                connectSuccess = false;
            }
            return null;
        }

        protected void onPostExecute(Void params){
            // clear loading ball and gave user feedback

            swipeLayout.setRefreshing(false);

            if(connectSuccess) {

                displayDataOnGrid();
                if(isAdded()) {
                    Toast.makeText(getActivity(), "Refresh completed!", Toast.LENGTH_SHORT).show();
                }
                gridListAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "Connection failed! Please Try again.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void displayDataOnGrid() {
        List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < imgText.length; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("image", imgCover[i]);
            item.put("text", imgText[i]);
            items.add(item);
        }

        if(isAdded()) {
            gridListAdapter = new SimpleAdapter(getActivity(),
                    items, R.layout.grid_item, new String[]{"image", "text"},
                    new int[]{R.id.image, R.id.text});

            gridListAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if (view.getId() == R.id.image) {
                        Picasso.with(view.getContext()).load(textRepresentation).error(R.drawable.no_cover).into((ImageView) view);
                        // Todo change to link array's value, nolink value handling
                        return true;
                    }
                    return false;
                }
            });


            gridView.setNumColumns(3);
            gridView.setAdapter(gridListAdapter);
        }
    }

    private String[] receviedJsonTolocalArray(JSONObject receivedJson, String field) throws JSONException {
        JSONArray jsonArray = receivedJson.getJSONArray(field);
        String [] outputArray = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            outputArray[i] = jsonArray.getString(i);
        }
        return outputArray;
    }
}
