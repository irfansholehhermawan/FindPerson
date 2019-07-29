package org.d3ifcool.alert;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Sholeh Hermawan on 12/1/2017.
 */

@RunWith(AndroidJUnit4.class)
public class AddDataSchedule {

    @Rule
    public ActivityTestRule<MainActivity> mDataScheduleTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickFab_AddSchedule(){
        onView(withId(R.id.add_schedule)).perform(click());
    }

    public void checkTitle_data() {
        onView(withText(R.string.title_add_schedule)).check(matches(isDisplayed()));
    }

    public void add_data(){
        onView(withId(R.id.input_date_schedule)).perform(replaceText("30/11/2017"));
        onView(withId(R.id.input_time_schedule)).perform(replaceText("18:18"));
        onView(withId(R.id.input_name_schedule)).perform(replaceText("Test"));

        onView(withId(R.id.btn_save)).perform(click());
    }
}
