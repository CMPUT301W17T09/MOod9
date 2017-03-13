package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Originally created by :
 * Modified by cdkushni on 3/5/17 and 3/8/17 to implement MoodListAdapter and data bundle receipt from addMood. Also made to inflate layout to a listview in the feed.
 * Modified by cdkushni on 3/10/17 to access the global application for global Models, changed over to using resource files for emotions and social situations,
 * started updating MoodModel along with linkedList of moods
 */
public class FeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Mood9Application mApplication;
    private ListView moodListView;
    private MoodListAdapter moodListAdapter;
    private LinkedList<Mood> moodLinkedList;
    //private ArrayList<Integer> emoteImages; // {R.drawable.anger, R.drawable.confusion, R.drawable.happiness, R.drawable.sadness, R.drawable.shame, R.drawable.surpise}
    //private ArrayList<String> userNameList; //{"Anger","Confusion","Happiness","Sadness","Shame","Surprise"}
    //private ArrayList<String> dateList;
    Context context;


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
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //This changes the title of the toolbar - need to change according to feed shown
        toolbar.setTitle(R.string.universal_feed);

        // set up list view adapter
        context = this;
        mApplication = (Mood9Application)getApplicationContext();
        //emoteImages = new ArrayList<Integer>();
        //userNameList = new ArrayList<String>();
        //dateList = new ArrayList<String>();
        moodLinkedList = mApplication.getMoodLinkedList();

        moodListView = (ListView) findViewById(R.id.moodList);
        moodListAdapter = new MoodListAdapter(this, moodLinkedList);
        moodListView.setAdapter(moodListAdapter);

        moodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //for list item clicked
                Intent editMoodIntent = new Intent(FeedActivity.this, AddMoodActivity.class);
                editMoodIntent.putExtra("editCheck", 1);
                editMoodIntent.putExtra("moodIndex", position);
                startActivityForResult(editMoodIntent, 1);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

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
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.personal) {
            // Handle the camera action
        } else if (id == R.id.followed) {

        } else if (id == R.id.universal) {

        } else if (id == R.id.near_me) {
            Intent mapIntent = new Intent(this, MapsActivity.class);
            startActivity(mapIntent);
        } else if (id == R.id.profile) {

        } else if (id == R.id.about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addMood() {
        Intent addMoodIntent = new Intent(this, AddMoodActivity.class);
        addMoodIntent.putExtra("editCheck", 0);
        startActivityForResult(addMoodIntent, 0);
    }

    private int getIndexOfMoodID(String moodId) {
        int targetId = Integer.parseInt(moodId);
        for (int i = 0; i < moodLinkedList.size(); i++) {
            int currentId = Integer.parseInt(moodLinkedList.get(i).getId());
            if (currentId == targetId) {
                return i;
            }
        }
        return -1;
    }

    // Code Documentation found here: https://developer.android.com/reference/android/app/Activity.html
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // requestcode 0 is from adding a new mood
        if (requestCode == 0) {

            LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_mood,null);

            TextView username = (TextView)view.findViewById(R.id.username);
            username.setTypeface(null, Typeface.BOLD);

            //TODO: Get rid of text view and move all display functionality over to linearlayout
            moodListAdapter.notifyDataSetChanged();
        } else {
            if (requestCode == 1) {
                moodListAdapter.notifyDataSetChanged();
            }
        }
    }
}
