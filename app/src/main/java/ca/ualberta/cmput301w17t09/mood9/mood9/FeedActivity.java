package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView moodListView;
    private ArrayList<String> moodHeaders;
    //private HashMap<String, List<String>> moodHash;
    private ArrayAdapter<String> listAdapter;
    static final int REQUEST_CODE = 0;

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

        moodHeaders = new ArrayList<String>();
        //moodHeaders.add("Test Mood!");

        listAdapter = new ArrayAdapter<String>(FeedActivity.this, android.R.layout.simple_list_item_1, moodHeaders);

        moodListView = (ListView) findViewById(R.id.moodList);
        moodListView.setAdapter(listAdapter);
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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addMood() {
        Intent addMoodIntent = new Intent(this, AddMoodActivity.class);
        startActivityForResult(addMoodIntent, 0);
    }

    // Code Documentation found here: https://developer.android.com/reference/android/app/Activity.html
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            String receiptData = data.getStringExtra("Emoticons");
            LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_mood,null);

            TextView username = (TextView)view.findViewById(R.id.username);
            username.setTypeface(null, Typeface.BOLD);

            username.setText(receiptData);

            moodHeaders.add(receiptData);
            listAdapter.notifyDataSetChanged();
        }
    }
}
