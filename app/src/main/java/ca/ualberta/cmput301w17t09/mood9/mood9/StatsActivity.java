package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        getSupportActionBar().setTitle("My Stats");
    }
}