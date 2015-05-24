package com.comp4521.bookscan.MainLayout;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookscan.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hk.ust.comp4521.AddFriendProgressFragment;
import hk.ust.comp4521.Common;
import hk.ust.comp4521.RegistrationActivity;
import hk.ust.comp4521.instant_messaging.ChatFragment;
import hk.ust.comp4521.instant_messaging.gcm.GCMUtils;
import hk.ust.comp4521.instant_messaging.server_utils.NetworkReceiver;
import hk.ust.comp4521.instant_messaging.server_utils.PostRequest;
import hk.ust.comp4521.instant_messaging.server_utils.RequestTaskManager;
import hk.ust.comp4521.instant_messaging.server_utils.ServerUtils;
import hk.ust.comp4521.storage_handle.CustomAsyncQueryHandler;
import hk.ust.comp4521.storage_handle.DataProvider;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        BookGridFragment.OnFragmentInteractionListener, GCMUtils.OnHandleResultListener, ChatFragment.OnGetSelfUidListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private String targetFragment;

    private GCMUtils gcmUtils;
    private String selfUsername;
    private CustomAsyncQueryHandler customAsyncQueryHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkReceiver networkReceiver = NetworkReceiver.getInstance();
        // handle request
        networkReceiver.setNetworkTask(new NetworkReceiver.NetworkTask() {

            private RequestTaskManager requestTaskManager;

            // Runs before the constructor each time you instantiate an object
            {
                requestTaskManager = RequestTaskManager.getInstance();
            }

            @Override
            public boolean hasTask() {
                return requestTaskManager.hasPendingTask();
            }

            @Override
            public void executeTask() {
                requestTaskManager.resumeTaskFromPending();
            }

            @Override
            public void executeOfflineTask() {
            }

        });

        //customAsyncQueryHandler.startQuery(DataProvider.CONTENT_URI_MESSAGES, new String[]{DataProvider.MESSAGES_COL_MSG, DataProvider.MESSAGES_COL_FROM, DataProvider.MESSAGES_COL_TO, DataProvider.MESSAGES_COL_AT}, DataProvider.MESSAGES_COL_SENDED+"=?", new String[]{"0"}, null);


        selfUsername = getIntent().getStringExtra(RegistrationActivity.Intent_USERNAME);
        if(selfUsername == null){
            selfUsername = Common.getSelfUid();
            gcmUtils = new GCMUtils(this);
            gcmUtils.checkPlayServices();
            gcmUtils.register();
            customAsyncQueryHandler = new CustomAsyncQueryHandler(getApplicationContext());
            customAsyncQueryHandler.startQuery(DataProvider.CONTENT_URI_MESSAGES, new String[]{DataProvider.COL_ID, DataProvider.MESSAGES_COL_MSG, DataProvider.MESSAGES_COL_TO}, DataProvider.MESSAGES_COL_SENDED+"=?", new String[]{"0"}, null);
        }
        else
            Common.saveSelfUid(selfUsername);



        if( getIntent()!=null) {
            targetFragment = getIntent().getStringExtra("toFragment");
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (targetFragment != null && targetFragment.equals("My Library")) {
            onNavigationDrawerItemSelected(1);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Common.setIsInForeground(true);
    }
    @Override
    protected void onPause() {
        super.onPause();
        Common.setIsInForeground(false);
    }

    @Override
    public void onSuccess(final String regId) {
        new AsyncTask<Void,Void,Integer>(){

            private final int SUCCESS = 0;
            private final int UNKNOW_ERROR = 1;

            @Override
            protected Integer doInBackground(Void... params) {

                try {
                    Map<String,String[]> postParams = new HashMap<String,String[]>();
                    postParams.put("username",new String[]{selfUsername});
                    postParams.put("regId",new String[]{regId});
                    String response = new PostRequest(ServerUtils.SERVER_URL+"/updateRegistration.php",postParams).request();
                    JSONObject reader = new JSONObject(response);
                    if(reader.getBoolean("success"))
                        return SUCCESS;

                }
                catch (Exception e) {}
                return UNKNOW_ERROR;
            }

            @Override
            protected void onPostExecute(Integer result) {
                switch(result){
                    case SUCCESS:
                        gcmUtils.successfullyRegistered();
                        break;
                }
            }

        }.execute(new Void[]{});

    }

    @Override
    public void onError() {}

    @Override
    public String onGetSelfUid() {
        return selfUsername;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position){
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, BookGridFragment.newInstance(getString(R.string.title_section1)))
                        .commit();
                break;
//            case 1:
//                fragmentManager.beginTransaction()
//                        .replace(R.id.container, BookGridFragment.newInstance(getString(R.string.title_section2)))
//                        .commit();
//                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, BookGridFragment.newInstance(getString(R.string.title_section3)))
                        .commit();
                break;
            default:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();
                break;
        }


    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
//            case 2:
//                mTitle = getString(R.string.title_section2);
//                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
            case 3:
                mTitle = getString(R.string.title_section4);
                break;
            case 4:
                mTitle = getString(R.string.title_section5);
                break;

        }
    }

    /**
     * Change the menu bar title. Called by fragment
     * @param title to be change
     */
    public void onSectionAttached(String title) {
        mTitle = title;

        Log.i("MainActivity", title);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    //TODO change menu for different fragment.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));


        }
    }



}
