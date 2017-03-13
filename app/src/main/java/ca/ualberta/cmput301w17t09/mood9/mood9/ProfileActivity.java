package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.SharedPreferences;
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

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.stored_name), MODE_PRIVATE);
        String name = sharedPreferences.getString("username", null);
        String user_id = sharedPreferences.getString("user_id", null);
        TextView profileName = (TextView) findViewById(R.id.profile_name);
        ListView followees = (ListView) findViewById(R.id.followees);
        profileName.setText(name);

        User current_user = UserModel.getUserProfile(user_id);

        ArrayList<String> followee_names = new ArrayList<String>();
        for (String id : current_user.getFollowees()) {
            User followee = UserModel.getUserProfile(id);
            followee_names.add(followee.getName());
        }

        ArrayAdapter<String> followees_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, followee_names);

        followees.setAdapter(followees_adapter);



    }
}
