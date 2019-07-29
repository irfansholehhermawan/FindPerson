package org.d3ifcool.alert;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anyOf;

/**
 * Created by Sholeh Hermawan on 12/1/2017.
 */

@RunWith(AndroidJUnit4.class)
public class AddDataProfile {

    @Rule
    public ActivityTestRule<ProfileActivity> mProfileDataTestRule =
            new ActivityTestRule<>(ProfileActivity.class);

    @Test
    public void click_AddDataProfile() {
        onView(withId(R.id.company_profil_editor)).perform(click());

        onView(withId(R.id.input_add_nama)).perform(replaceText("Sholeh"));
        onView(withId(R.id.input_add_tempat)).perform(replaceText("Klaten"));
        onView(withId(R.id.input_add_tgl)).perform(replaceText("24/04/1997"));
        onView(withId(R.id.input_add_nohp)).perform(replaceText("081578999579"));
    }

    public void testClickInsert() {
        onView(anyOf(withId(R.drawable.ic_check_24dp), withId(R.id.submit_profile))).perform(click());
    }

    public void checkData() {
        onView(withId(R.id.profile_nama)).check(matches(withText("Sholeh")));
        onView(withId(R.id.profile_ttl)).check(matches(withText("Klaten, 24/04/1997")));
        onView(withId(R.id.profile_phone_number)).check(matches(withText("081578999579")));
    }

}