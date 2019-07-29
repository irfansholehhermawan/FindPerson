package org.d3ifcool.alert;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Sholeh Hermawan on 11/30/2017.
 */

@RunWith(AndroidJUnit4.class)
public class FabAddScheduleTest {

    @Rule
    public ActivityTestRule<MainActivity> mScheduleFragmentTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickFab_AddSchedule(){
        onView(withId(R.id.add_schedule)).perform(click());

        onView(withText(R.string.title_add_schedule)).check(matches(isDisplayed()));
    }
}
