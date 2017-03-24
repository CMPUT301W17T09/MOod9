package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import io.searchbox.core.Get;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPreferences.getString("username", null);
        String user_id = sharedPreferences.getString("user_id", null);
        TextView profileName = (TextView) findViewById(R.id.profile_name);
        ListView followees = (ListView) findViewById(R.id.followees);
        ListView requests = (ListView) findViewById(R.id.requests);

        profileName.setText(name);

        User current_user = UserModel.getUserProfile(user_id);


        ArrayList<String> request_list = new ArrayList<String>();
        for (String id : current_user.getRequests()) {
            User requested = UserModel.getUserProfile(id);
            request_list.add(requested.getName());
        }

        ArrayAdapter<String> req_adapter = new ArrayAdapter<String>(this, R.layout.request_fragment, request_list);
        //Profile now displays list of users the user is following

        requests.setAdapter(req_adapter);


    }
}
