package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Switch;

public class CustomLocation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_location);

        NumberPicker lon = (NumberPicker) findViewById(R.id.longitudePicker);
        NumberPicker lat = (NumberPicker) findViewById(R.id.latitudePicker);
        Button doneButton = (Button) findViewById(R.id.doneButton);
        Switch lonSwitch = (Switch) findViewById(R.id.switch1);
        Switch latSwitch = (Switch) findViewById(R.id.switch2);

        lon.setMaxValue(180);
        lon.setMinValue(0);
        lat.setMaxValue(90);
        lat.setMinValue(0);


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent customIntent = new Intent();

                double sendLon =  lon.getValue();
                double sendLat = lat.getValue();

                if(lonSwitch.isChecked()){
                    sendLon *= -1;
                }
                if(latSwitch.isChecked()){
                   sendLat *= -1;
                }

                customIntent.putExtra("lon", sendLon);
                customIntent.putExtra("lat", sendLat);
                setResult(Activity.RESULT_OK, customIntent);
                finish();
            }
        });

    }
}
