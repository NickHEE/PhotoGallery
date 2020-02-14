package com.example.photoGallery;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.util.Log;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest {
    private static final String TEST_CAPTION = "Random Caption";
    private static final String FILTER_CAPTION = "I love Surrey";
    private static final String TEST_LONGITUDE = "-122.8832483";
    private static final String TEST_LATITUDE = "49.126136";
    private static final String TEST_DISTANCE = "1000";

    private Matcher<View> hasValueEqualTo(final String content) {

        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("Has EditText/TextView the value:  " + content);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextView) && !(view instanceof EditText)) {
                    return false;
                }
                if (view != null) {
                    String text;
                    if (view instanceof TextView) {
                        text = ((TextView) view).getText().toString();
                        Log.d("hasValueEqualTo", text);
                    } else {
                        text = ((EditText) view).getText().toString();
                        Log.d("hasValueEqualTo", text);
                    }

                    return (text.equalsIgnoreCase(content));
                }
                return false;
            }
        };
    }

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);
    @Test
    public void ensureTextChangesWork() {
        // Espresso cannot interact with external apps. Therefore, assume pictures already taken
        // change the current image's caption
        onView(withId(R.id.editText_caption)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.editText_caption)).perform(typeText(TEST_CAPTION), closeSoftKeyboard());

        // scroll right
        onView(withId(R.id.btn_r)).perform(click());

        // change the current image's caption
        onView(withId(R.id.editText_caption)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.editText_caption)).perform(typeText(FILTER_CAPTION), closeSoftKeyboard());

        // scroll left
        onView(withId(R.id.btn_l)).perform(click());

        // TEST: see if caption entered earlier was retained
        onView(withId(R.id.editText_caption)).check(matches(hasValueEqualTo(TEST_CAPTION)));


        // go into filter activity
        onView(withId(R.id.btn_filter)).perform(click());

        // choose the "from date"
        onView(withId(R.id.tvDate_from)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 1, 20));
        onView(withId(android.R.id.button1)).perform(click());

        // choose the "to date"
        onView(withId(R.id.tvDate_to)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 2, 13));
        onView(withId(android.R.id.button1)).perform(click());

        // choose a caption to filter with
        onView(withId(R.id.Comment_search)).perform(typeText(FILTER_CAPTION), closeSoftKeyboard());

        // choose location and distance
        onView(withId(R.id.distance_search)).perform(click());
        onView(withId(R.id.distance_search)).perform(typeText(TEST_DISTANCE), closeSoftKeyboard());
        onView(withId(R.id.longitude_search)).perform(click());
        onView(withId(R.id.longitude_search)).perform(typeText(TEST_LONGITUDE), closeSoftKeyboard());
        onView(withId(R.id.latitude_search)).perform(click());
        onView(withId(R.id.latitude_search)).perform(typeText(TEST_LATITUDE), closeSoftKeyboard());
        onView(withId(R.id.update_map)).perform(click());

        // apply the search filter (go back to main activity)
        onView(withId(R.id.search_search)).perform(click());

        // TEST: see if filtered image has correct caption
        onView(withId(R.id.editText_caption)).check(matches(hasValueEqualTo(FILTER_CAPTION)));
    }
}



