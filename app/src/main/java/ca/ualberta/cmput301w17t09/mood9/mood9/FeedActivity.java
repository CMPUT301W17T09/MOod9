package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * The main feed activity controller for I/O control on anything to do with the feed views.
 * Maintains the mood list that is used to contain what will be displayed in the fields.
 * <p>
 * Originally created by :
 * Modified by cdkushni on 3/5/17 and 3/8/17 to implement MoodListAdapter and data bundle receipt
 * from addMood. Also made to inflate layout to a listview in the feed.
 * Modified by cdkushni on 3/10/17 to access the global application for global Models, changed
 * over to using resource files for emotions and social situations,
 * started updating MoodModel along with linkedList of moods
 * Modified by cdkushni on 3/18/17 to incorporate a expandable search action bar item to search
 * queries in elastic search
 * Modified by cdkushni on 3/20/17 to return search queries to main feed and load it into the
 * display adapter, also encapsulated default mood load for easy reloads
 * Also, kept using moodLinkedList so that we can use the linkedList as a displayer that can be
 * cleared when searching without affecting the moodModel which holds the default moods
 * Fixed some bugs with shared preferences that came up upon new accounts after a clear data
 * Disabled editing mood events while searching. Instead clicking on a mood will bring up a dialog
 * window with username, trigger and social description.
 * Modified by cdkushni on 3/20/17 to start a new function that will take input queries and convert
 * it to usable id queries
 * Modified by cdkushni on 3/25/17 to work with each individual feed (Universal, personal and
 * followed)
 * Modified by cdkushni on 3/30/17 to be able to search/sort/filter all feeds
 * </p>
 *
 * @author cdkushni
 * @version 1.6, 4/2/17
 * @see AddMoodActivity
 * @see MoodViewActivity
 * @see MoodListAdapter
 * @see LocationService
 * @see Mood9Application
 * @see UpdatedMoodModel
 * @since 1.0
 */
public class FeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MoodListAdapter moodListAdapter;
    private LinkedList<Mood> moodLinkedList;
    private Mood9Application mApplication;
    private LinkedList<Mood> preSortList;


    /**
     * The onCreate method, sets up the feed with default settings and initializes adapter
     * @param savedInstanceState : saved Instance State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMood();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //This changes the title of the toolbar to the default of universal
        setFeedName("universal");

        // set up list view adapter
        mApplication = (Mood9Application) getApplicationContext();
        moodLinkedList = mApplication.getMoodLinkedList();
        preSortList = new LinkedList<>();

        ListView moodListView = (ListView) findViewById(R.id.moodList);
        moodListAdapter = new MoodListAdapter(this, moodLinkedList, mApplication);
        moodListView.setAdapter(moodListAdapter);
        populateFromMoodLoad(mApplication.getMoodModel().getUniversalUserMoods(null));
        //sortDisplayByDate();

        /**
         * Click listener for checking if a mood in the list was clicked and if so then to
         * start the viewing activity for it.
         */
        moodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //for list item clicked
                Intent viewMoodIntent = new Intent(FeedActivity.this, MoodViewActivity.class);
                viewMoodIntent.putExtra("moodIndex", position);
                startActivityForResult(viewMoodIntent, 1);
            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Search action bar menu handler.
     * Handles submission of a new search query and closing the search
     * @param menu : the new menu to inflate
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(true);
        }
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                return true;
            }

            /**
             * Submits the text to the query converter function which will search
             * for and return the moods found.
             * It then populates the feed with the newly found moods while saving the default
             * moods of the current feed in the preSortList before updating the adapter.
             * @param query : The query to search for
             * @return boolean
             */
            public boolean onQueryTextSubmit(String query) {
                /*
                if (toolbar.getTitle().toString().compareTo("Universal Feed") == 0) {
                    reloadedMoods = queryConverter(query);
                } else { // Personal or Follower feed so all should be loaded
                    reloadedMoods = queryConverter(query);
                }*/
                ArrayList<Mood> reloadedMoods = queryConverter(query);
                populateFromMoodLoad(reloadedMoods);
                preSortList.clear();
                preSortList.addAll(moodLinkedList);
                moodListAdapter.notifyDataSetChanged();
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        /**
         * On close search listener. Closes the search and reloads the feed to it's default
         * Moods based on what feed it is currently on.
         */
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                moodLinkedList.clear();
                ArrayList<Mood> reloadedMoods;
                if (toolbar.getTitle().toString().compareTo("Personal Feed") == 0)
                    reloadedMoods = mApplication.getMoodModel().getCurrentUserMoods();
                else if (toolbar.getTitle().toString().compareTo("Followed Feed") == 0)
                    reloadedMoods = getFollowerMoods();
                else
                    reloadedMoods = mApplication.getMoodModel().getUniversalUserMoods(null);
                populateFromMoodLoad(reloadedMoods);
                preSortList.clear();
                moodListAdapter.notifyDataSetChanged();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handles I/O for either sorting or filtering the feed
     * @param item : The item selected in the filter/sort drop down list
     * @return result
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            //TODO Add functionality to the search button
            return true;
        } else if (id == R.id.sort_last_day) {
            setMoodsByDate("day");
        } else if (id == R.id.sort_last_week) {
            setMoodsByDate("week");
        } else if (id == R.id.sort_last_month) {
            setMoodsByDate("month");
        } else if (id == R.id.sort_last_year) {
            setMoodsByDate("year");
        } else if (id == R.id.sort_forever) {
            updateFromPreSortList();
            sortDisplayByDate();
        } else if (id == R.id.filter_emotions) {
            openFilterDialog("emotions");
        } else if (id == R.id.filter_socials) {
            openFilterDialog("socials");
        } else if (id == R.id.filter_clear) {
            preSortList.clear();
            updateFromPreSortList();
            moodListAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Searches the current feed's moods for the string query and returns the moods found
     * @param query : the query to search for
     * @return reloadedMoods : the moodlist appended to
     */
    private ArrayList<Mood> currentFeedQuery(String query) {
        ArrayList<Mood> reloadedMoods = new ArrayList<>();
        if (query.substring(0, query.indexOf(':')).compareTo("emotionId") == 0) {
            for (Mood mood : moodLinkedList) {
                if (mood.getEmotionId().compareTo(query.substring(query.indexOf(':')+1)) == 0) {
                    reloadedMoods.add(mood);
                }
            }
        } else if (query.substring(0, query.indexOf(':')).compareTo("socialSituationId") == 0) {
            for (Mood mood : moodLinkedList) {
                if (mood.getSocialSituationId().compareTo(query.substring(query.indexOf(':') + 1)) == 0) {
                    reloadedMoods.add(mood);
                }
            }
        } else if (query.substring(0, query.indexOf(':')).compareTo("user_id") == 0) {
            for (Mood mood : moodLinkedList) {
                if (mood.getUser_id().compareTo(query.substring(query.indexOf(':') + 1)) == 0) {
                    reloadedMoods.add(mood);
                }
            }
        } else if (query.substring(0, query.indexOf(':')).compareTo("trigger") == 0) {
            for (Mood mood : moodLinkedList) {
                if (mood.getTrigger().compareTo(query.substring(query.indexOf(':') + 1)) == 0) {
                    reloadedMoods.add(mood);
                }
            }
        }
        return reloadedMoods;
    }

    /**
     * Handles I/O and filtering of the feed depending on said I/O
     * Gets what items were clicked and then based upon the feed filters the current feed based
     * upon whatever the selection was.
     * @param filterType : the type of filter selected
     */
    private void openFilterDialog(String filterType) {
        final String[] items = checkBoxDateInit(filterType);
        final ArrayList<String> selectedItems = new ArrayList<>();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select Filter Parameters")
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selectedItems.add(items[which]);
                        } else if (selectedItems.contains(items[which])) {
                            selectedItems.remove(selectedItems.indexOf(items[which]));
                        }
                    }
                }).setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // SAVE SELECTED ITEMS AND QUERY HERE
                        ArrayList<Mood> reloadedMoods = new ArrayList<>();
                        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                        if (toolbar.getTitle().toString().compareTo("Universal Feed") == 0) {
                            if (preSortList.size() == 0) {
                                moodLinkedList.clear();
                                moodLinkedList.addAll(mApplication.getMoodModel().getUniversalUserMoods(null));
                            } else {
                                // we have done a search and should thus filter the searched results
                                moodLinkedList.clear();
                                moodLinkedList.addAll(preSortList);
                            }
                        } else if (toolbar.getTitle().toString().compareTo("Followed Feed") == 0) {
                            if (preSortList.size() == 0) {
                                moodLinkedList.clear();
                                moodLinkedList.addAll(getFollowerMoods());
                            } else {
                                // we have done a search and should thus filter the searched results
                                moodLinkedList.clear();
                                moodLinkedList.addAll(preSortList);
                            }
                        } else { // We are in the personal feed
                            if (preSortList.size() == 0) {
                                moodLinkedList.clear();
                                moodLinkedList.addAll(mApplication.getMoodModel().getCurrentUserMoods());
                            } else {
                                // we have done a search and should thus filter the searched results
                                moodLinkedList.clear();
                                moodLinkedList.addAll(preSortList);
                            }
                        }
                        for (int i = 0; i < selectedItems.size(); i++) {
                            reloadedMoods.addAll(queryConverter(selectedItems.get(i))); // check this over, may need to fix reloading of prefilter personal list
                        }
                        populateFromMoodLoad(reloadedMoods);
                        preSortList.clear();
                        preSortList.addAll(moodLinkedList);
                        moodListAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // CANCELED SO BACK OUT
                    }
                }).create();
        dialog.show();
    }

    /**
     * Initiates the filter checkBoxes based upon what the filter type selection was
     * @param dateType : the selection made for what to filter
     * @return checkBoxItems : the list of items to be used as check box items
     */
    private String[] checkBoxDateInit(String dateType) {
        String[] checkBoxItems = new String[0];
        if (dateType.compareTo("emotions") == 0) {
            checkBoxItems = new String[mApplication.getEmotionModel().getEmotions().size()];
            for (Map.Entry<String, Emotion> entry : mApplication.getEmotionModel()
                    .getEmotions().entrySet()) {
                checkBoxItems[Integer.parseInt(entry.getKey())] = entry.getValue().getName();
            }
        } else if (dateType.compareTo("socials") == 0) {
            checkBoxItems = new String[mApplication.getSocialSituationModel()
                    .getSocialSituations().entrySet().size()];
            for (Map.Entry<String, SocialSituation> entry : mApplication.getSocialSituationModel()
                    .getSocialSituations().entrySet()) {
                checkBoxItems[Integer.parseInt(entry.getKey())] = entry.getValue().getName();
            }
        }
        return checkBoxItems;
    }

    /**
     * I/O controller for switching between different views based around the main feed view
     * @param item : the item selected to switch to
     * @return true
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.personal) {
            // Handle the camera action
            setFeedName("personal");
            ArrayList<Mood> temp = mApplication.getMoodModel().getCurrentUserMoods();
            populateFromMoodLoad(temp);
            sortDisplayByDate();
        } else if (id == R.id.followed) {
            setFeedName("followed");
            populateFromMoodLoad(getFollowerMoods());
            sortDisplayByDate();
        } else if (id == R.id.universal) {
            setFeedName("universal");
            ArrayList<Mood> temp = mApplication.getMoodModel().getUniversalUserMoods(null);
            populateFromMoodLoad(temp);
            sortDisplayByDate();
        } else if (id == R.id.near_me) {
            Intent mapIntent = new Intent(this, MapsActivity.class);
            mapIntent.putExtra("moodList", moodLinkedList);
            startActivity(mapIntent);
        } else if (id == R.id.profile) {
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            startActivity(profileIntent);
        } else if (id == R.id.about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
        } else if (id == R.id.stats) {
            Intent statsActivity = new Intent(this, StatsActivity.class);
            startActivity(statsActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * sets the Toolbar's name to the new feed name
     * @param newName : the new name to set the feed to
     */
    private void setFeedName(String newName) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (newName.compareTo("universal") == 0)
            toolbar.setTitle(R.string.universal_feed);
        else if (newName.compareTo("personal") == 0)
            toolbar.setTitle(R.string.personal_feed);
        else if (newName.compareTo("followed") == 0)
            toolbar.setTitle(R.string.followed_feed);
    }

    /**
     * Updates the view from the presort list or a default feed list for a full reset
     * If the preSortList is not empty then we know that it has been previously filled with
     * items that were saved there before a search or a filter and we now want to reload it.
     */
    private void updateFromPreSortList() {
        moodLinkedList.clear();
        if (preSortList.size() > 0) { // this string of conditions sets it up so that the original list of moods is reloaded for resorting
            moodLinkedList.addAll(preSortList);
        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            String title = toolbar.getTitle().toString();
            if (title.compareTo("Universal Feed") == 0) {
                moodLinkedList.addAll(mApplication.getMoodModel().getUniversalUserMoods(null));
            } else if (title.compareTo("Personal Feed") == 0) {
                moodLinkedList.addAll(mApplication.getMoodModel().getCurrentUserMoods());
            } else if (title.compareTo("Followed Feed") == 0) {
                moodLinkedList.addAll(getFollowerMoods());
            }
        }
    }

    /**
     * Updates the moodlist to original list for a new sort
     * Sorts the list by chronological order
     * Then filters the display to only show moods based upon the date range inputted
     * @param range : the date range to show
     */
    private void setMoodsByDate(String range) {
        updateFromPreSortList();
        sortDisplayByDate();
        Date target = new Date();
        Calendar cal = Calendar.getInstance();
        if (range.compareTo("day") == 0) {
            cal.add(Calendar.DATE, -1);
            target = cal.getTime();
        } else if (range.compareTo("week") == 0) {
            cal.add(Calendar.DATE, -7);
            target = cal.getTime();
        } else if (range.compareTo("month") == 0) {
            cal.add(Calendar.MONTH, -1);
            target = cal.getTime();
        } else if (range.compareTo("year") == 0) {
            cal.add(Calendar.YEAR, -1);
            target = cal.getTime();
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        LinkedList<Mood> temp = new LinkedList<>();
        for (Mood entry : moodLinkedList) {
            try {
                if (dateFormat.parse(entry.getDate()).after(target)) {
                    temp.add(entry);
                }
            } catch (ParseException e) {
                // PARSE FAILED
            }
        }
        moodLinkedList.clear();
        moodLinkedList.addAll(temp);
        moodListAdapter.notifyDataSetChanged();
    }

    /**
     * Sorts the current Moods on display into ascending chronological order
     */
    private void sortDisplayByDate() {
        Collections.sort(moodLinkedList, new Comparator<Mood>(){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            @Override
            public int compare(Mood mood1, Mood mood2){
                try {
                    return dateFormat.parse(mood2.getDate()).compareTo(dateFormat.parse(mood1.getDate()));
                } catch (ParseException e) {
                    // PARSE FAILED
                }
                return 0;
            }
        });
        moodListAdapter.notifyDataSetChanged();
    }

    /**
     * Calls the addition of a new mood and switches activities
     */
    private void addMood() {
        Intent addMoodIntent = new Intent(this, AddMoodActivity.class);
        addMoodIntent.putExtra("editCheck", 0);
        startActivityForResult(addMoodIntent, 0);
    }

    /**
     * Populates the Feed with the passed in list of moods
     * @param newMoods : the moods to populate the feed with
     */
    private void populateFromMoodLoad(ArrayList<Mood> newMoods) {
        moodLinkedList.clear();
        //LOADING FROM ELASTIC SEARCH
        for (int i = 0; i < newMoods.size(); i++) {
            moodLinkedList.add(newMoods.get(i));
        }
        moodListAdapter.notifyDataSetChanged();
    }

    /**
     * Converts user queries into usable Id search queries then queries for them based upon
     * which feed is currently active and appends them to the list of moods found
     * @param query : the inputted query to convert and search for
     * @return reloadedMoods : the moods found by the query
     */
    private ArrayList<Mood> queryConverter(String query) {
        // Convert user queries into usable id search queries
        String queryConverted;
        ArrayList<Mood> reloadedMoods = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ArrayList<String> names = UserModel.getAllUsers();

        for (Map.Entry<String, SocialSituation> entry : mApplication.getSocialSituationModel()
                .getSocialSituations().entrySet()) {
            if (entry.getValue().getName().toLowerCase()
                    .contains(query.toLowerCase()) || entry.getValue().getDescription()
                    .toLowerCase().contains(query.toLowerCase())) {
                queryConverted = "socialSituationId:"+entry.getValue().getId();
                if (toolbar.getTitle().toString().compareTo("Universal Feed") == 0)
                    reloadedMoods.addAll(universalElasticQuery(queryConverted));
                else
                    reloadedMoods.addAll(currentFeedQuery(queryConverted));
            }
        }
        for (Map.Entry<String, Emotion> entry: mApplication.getEmotionModel()
                .getEmotions().entrySet()) {
            if (entry.getValue().getName().toLowerCase()
                    .contains(query.toLowerCase()) || entry.getValue().getDescription()
                    .toLowerCase().contains(query.toLowerCase())) {
                queryConverted = "emotionId:"+entry.getValue().getId();
                if (toolbar.getTitle().toString().compareTo("Universal Feed") == 0)
                    reloadedMoods.addAll(universalElasticQuery(queryConverted));
                else
                    reloadedMoods.addAll(currentFeedQuery(queryConverted));
            }
        }
        for (String name : names) {
            if (name.toLowerCase().contains(query.toLowerCase())) {
                queryConverted = "user_id:"+UserModel.getUserID(name);
                if (toolbar.getTitle().toString().compareTo("Universal Feed") == 0)
                    reloadedMoods.addAll(universalElasticQuery(queryConverted));
                else
                    reloadedMoods.addAll(currentFeedQuery(queryConverted));
            }
        }
        if (reloadedMoods.size() == 0) {
            queryConverted = "trigger:"+query;
            if (toolbar.getTitle().toString().compareTo("Universal Feed") == 0)
                reloadedMoods.addAll(universalElasticQuery(queryConverted));
            else
                reloadedMoods.addAll(currentFeedQuery(queryConverted));
        }

        // Remove duplicates
        reloadedMoods = removeDuplicateMoods(reloadedMoods);

        return reloadedMoods;
    }

    /**
     * Removes and duplicate moods found in a list of moods
     * @param moodList : the list of moods to check for duplicates
     * @return tempList : the list of moods with duplicates removed
     */
    private ArrayList<Mood> removeDuplicateMoods(ArrayList<Mood> moodList) {
        ArrayList<Mood> tempList = new ArrayList<>();
        for (Mood mood : moodList) {
            int FoundMood = 0;
            for (Mood mood2 : tempList) {
                try {
                    if (mood.getId().compareTo(mood2.getId()) == 0) {
                        FoundMood = 1;
                    }
                } catch (Exception e) {
                    if (mood == mood2) {
                        FoundMood = 1;
                    }
                }
            }
            if (FoundMood == 0) {
                tempList.add(mood);
            }
        }
        return tempList;
    }

    /**
     * Gets moods by searching elastic search for universal moods with a converted query
     * @param queryConverted : the converted query to search elastic search for
     * @return the found moods
     */
    private ArrayList<Mood> universalElasticQuery(String queryConverted) {
        if (preSortList.size() == 0) {
            HashMap<String, String> queryHash = new HashMap<>();
            queryHash.put(queryConverted.substring(0, queryConverted.indexOf(':')),
                    queryConverted.substring(queryConverted.indexOf(':') + 1));
            moodLinkedList.clear();
            return mApplication.getMoodModel().getUniversalUserMoods(queryHash);
        } else {
            return currentFeedQuery(queryConverted);
        }
    }

    /**
     * Gets all moods from the current user's followed list
     * @return FollowerMoods : all moods found
     */
    private ArrayList<Mood> getFollowerMoods() {
        ArrayList<Mood> FollowerMoods = new ArrayList<>();
        HashMap<String, String> queryHash = new HashMap<>();
        for (String Followee : getCurrentUser().getFollowees()) {
            queryHash.put("user_id", Followee);
            FollowerMoods.addAll(mApplication.getMoodModel().getUniversalUserMoods(queryHash));
        }
        return FollowerMoods;
    }

    /**
     * Uses shared preferences on the phone to get the current user profile
     * @return userProfile: the user profile to be returned
     */
    private User getCurrentUser() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPreferences.getString("username", "test");
        User userProfile = UserModel.getUserProfile(UserModel.getUserID(userName));
        return userProfile;
    }

    /**
     * Recieves return data after either having added a new mood or edited an old one
     * Uses the return data to update the feed with the new mood or changed mood.
     */
    // Code Documentation found here: https://developer.android.com/reference/android/app/Activity.html
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // requestcode 0 is from adding a new mood
        if (requestCode == 0) {

            LayoutInflater inflater = (LayoutInflater)this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_mood,null);

            TextView username = (TextView)view.findViewById(R.id.username);
            username.setTypeface(null, Typeface.BOLD);
            setFeedName("personal");
            ArrayList<Mood> temp = mApplication.getMoodModel().getCurrentUserMoods();
            populateFromMoodLoad(temp);
            sortDisplayByDate();
            NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
            navView.getMenu().getItem(0).setChecked(true);

        } else {
            if (requestCode == 1) {
                moodListAdapter.notifyDataSetChanged();
                sortDisplayByDate();
            }
        }
    }
}
