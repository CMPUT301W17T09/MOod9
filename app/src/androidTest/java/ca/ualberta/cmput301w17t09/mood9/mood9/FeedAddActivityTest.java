package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.junit.Test;

/**
 * Created by cdkushni on 3/13/17.
 */

public class FeedAddActivityTest extends ActivityInstrumentationTestCase2<FeedActivity> {
    private Solo solo;

    public FeedAddActivityTest() {
        super(FeedActivity.class);
    }
    public void setUp() throws Exception{
        solo = new Solo(getInstrumentation(), getActivity());
    }


    @Test
    public void addTest(){
        solo.assertCurrentActivity("Wrong Activity", FeedActivity.class);
        solo.clickOnView(solo.getView(R.id.fab));

        solo.assertCurrentActivity("Wrong Activity", AddMoodActivity.class);
        solo.enterText((EditText) solo.getView(R.id.trigger_edittext), "Test Trigger!");
        View view1 = solo.getView(Spinner.class, 0);
        View view2 = solo.getView(Spinner.class, 1);
        solo.clickOnView(view1);
        solo.scrollToTop();
        solo.clickOnView(solo.getView(TextView.class, 2));
        solo.clickOnView(view2);
        solo.scrollToTop();
        solo.clickOnView(solo.getView(TextView.class, 3));
        solo.clickOnView(solo.getView(R.id.button)); // set location
        solo.clickOnView(solo.getView(R.id.button2)); // save

        solo.assertCurrentActivity("Wrong Activity", FeedActivity.class);
    }
}
