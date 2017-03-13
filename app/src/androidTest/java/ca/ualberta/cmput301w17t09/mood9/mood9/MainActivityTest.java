package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.robotium.solo.Solo;
import junit.framework.TestCase;

/**
 * Created by fmachaal on 3/12/17.
 */

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    public MainActivityTest() {
        super(MainActivity.class);
    }
    public void setUp() throws Exception{
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testStart() throws Exception {
        Activity activity = getActivity();
    }


    public void testMain(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clearEditText((EditText) solo.getView(R.id.username_field));
        solo.enterText((EditText) solo.getView(R.id.username_field), "Test Username!");
        solo.clickOnButton("Go");
    }

    public void testClickTweetList(){
        MainActivity activity = (MainActivity) solo.getCurrentActivity();

//        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
//        solo.clickOnButton("Clear");

//        String testMessage = "Test Tweet!";
//        solo.enterText((EditText) solo.getView(R.id.body), testMessage);
//        solo.clickOnButton("Save");
//        solo.waitForText("Test Tweet");

//        final ListView oldTweetsList = activity.getOldTweetsList();
//        Tweet tweet = (Tweet) oldTweetsList.getItemAtPosition(0);
//        assertEquals("Test Tweet!", tweet.getMessage());

//        solo.clickInList(0);
//        solo.assertCurrentActivity("Wrong Activity", EditTweetActivity.class);
//        assertTrue(solo.waitForText(testMessage));

//        solo.goBack();
//        solo.assertCurrentActivity("Wrong Activity", LonelyTwitterActivity.class);
    }



    @Override
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }


}
