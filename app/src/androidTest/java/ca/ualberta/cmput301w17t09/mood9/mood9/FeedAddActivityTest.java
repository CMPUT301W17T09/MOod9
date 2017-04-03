package ca.ualberta.cmput301w17t09.mood9.mood9;

import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
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

    public void testStart() throws Exception {
        Activity activity = getActivity();
    }

    @Test
    public void testNavBar(){
        FeedActivity activityFeed = (FeedActivity) solo.getCurrentActivity();

        solo.assertCurrentActivity("Wrong Activity", FeedActivity.class);

        solo.clickOnImageButton(0);
        solo.clickOnMenuItem("Profile", true);
        solo.goBack();

        solo.clickOnImageButton(0);
        solo.clickOnMenuItem("About", true);
        solo.goBack();

        solo.clickOnImageButton(0);
        solo.clickOnMenuItem("Stats", true);
        solo.goBack();

        solo.clickOnImageButton(0); // open navbar menu
        solo.clickOnMenuItem("Followed");
        solo.clickOnImageButton(0); // open navbar menu

        solo.clickOnMenuItem("Near Me");
        solo.assertCurrentActivity("Wrong Activity", MapsActivity.class);
        solo.goBack();

        solo.clickOnImageButton(0);
        solo.clickOnMenuItem("Personal");
        solo.assertCurrentActivity("Wrong Activity", FeedActivity.class);
        testAdd();
    }

    @Override
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
    @Test
    public void testAdd(){
        solo.assertCurrentActivity("Wrong Activity", FeedActivity.class);
        View fab = getActivity().findViewById(R.id.fab);
        solo.clickOnView(fab);

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
        testEdit();
    }
    @Test
    public void testEdit(){
        solo.assertCurrentActivity("Wrong Activity", FeedActivity.class);

        solo.clickInList(0);
        solo.assertCurrentActivity("Wrong Activity", MoodViewActivity.class);

        solo.goBack();

        solo.assertCurrentActivity("Wrong Activity", FeedActivity.class);

        solo.clickOnImageButton(0);
        solo.clickOnMenuItem("Personal");
        solo.clickOnMenuItem("Personal");
        solo.assertCurrentActivity("Wrong Activity", FeedActivity.class);

        solo.clickInList(0);
        solo.assertCurrentActivity("Wrong Activity", MoodViewActivity.class);

        solo.clickOnButton(0);
        solo.assertCurrentActivity("Wrong Activity", AddMoodActivity.class);

        solo.enterText((EditText) solo.getView(R.id.trigger_edittext), "THIS IS SPARTA!");
        View view1 = solo.getView(Spinner.class, 0);
        View view2 = solo.getView(Spinner.class, 1);
        solo.clickOnView(view1);
        solo.scrollToTop();
        solo.clickOnView(solo.getView(TextView.class, 3));
        solo.clickOnView(view2);
        solo.scrollToTop();
        solo.clickOnView(solo.getView(TextView.class, 2));
        solo.clickOnView(solo.getView(R.id.button)); // set location
        solo.clickOnView(solo.getView(R.id.button2)); // save

        solo.assertCurrentActivity("Wrong Activity", FeedActivity.class);
        testFilterSearch();
    }

    @Test
    public void testFilterSearch() {
        solo.assertCurrentActivity("Wrong Activity", FeedActivity.class);
        solo.clickOnView(solo.getView(R.id.search));
        solo.sendKey(KeyEvent.KEYCODE_SEARCH);  //hit search on softkeyboard
    }
}
