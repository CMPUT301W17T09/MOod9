package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;



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
        ArrayList<String> request_list = new ArrayList<String>();
        ListView requests = (ListView) findViewById(R.id.requests);
        profileName.setText(name);
        User current_user = UserModel.getUserProfile(user_id);
        for (String id : current_user.getRequests()) {
            User requested = UserModel.getUserProfile(id);
            request_list.add(requested.getName());
        }
        System.out.println(user_id);
        CustomRequestAdapter req_adapter = new CustomRequestAdapter(request_list,this,user_id);

        //Profile now displays list of users the user is following
        requests.setAdapter(req_adapter);


        requests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
