package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import io.searchbox.core.Get;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.stored_name), MODE_PRIVATE);
        String name = sharedPreferences.getString("username", null);
        TextView profileName = (TextView) findViewById(R.id.profile_name);
        profileName.setText(name);

        ElasticSearchMOodController.GetUsersTask getUsersTask = new ElasticSearchMOodController.GetUsersTask();
        getUsersTask.execute(name);

        User user = new User("");
        try {
            user = getUsersTask.get();

        } catch (Exception e) {}

        user.getFollowees();

    }
}
