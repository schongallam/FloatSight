/*
 *
 *     FloatSight
 *     Copyright 2018 Thomas Hirsch
 *     https://github.com/84n4n4/FloatSight
 *
 *     This file is part of FloatSight.
 *     FloatSight is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FloatSight is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with FloatSight.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.watchthybridle.floatsight.mpandroidchart.customcharts;

import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentTransaction;
import com.github.mikephil.charting.highlight.Highlight;
import com.watchthybridle.floatsight.Actions;
import com.watchthybridle.floatsight.MainActivity;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.fragment.plot.PlotFragment;
import com.watchthybridle.floatsight.csvparser.FlySightCsvParser;
import com.watchthybridle.floatsight.mpandroidchart.customcharts.GlideOverlayChart;
import com.watchthybridle.floatsight.mpandroidchart.customcharts.markerviews.TouchAbleMarkerView;
import com.watchthybridle.floatsight.data.FlySightTrackData;
import com.watchthybridle.floatsight.datarepository.DataRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.watchthybridle.floatsight.MainActivity.TAG_MAIN_MENU_FRAGMENT;
import static com.watchthybridle.floatsight.MainActivity.TAG_PLOT_FRAGMENT;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TouchableMarkerViewTest {

    private PlotFragment plotFragment;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        plotFragment = new PlotFragment();
        FragmentTransaction transaction = rule.getActivity().getSupportFragmentManager().beginTransaction();
        transaction
                .replace(R.id.fragment_container, plotFragment,
                        TAG_PLOT_FRAGMENT)
                .addToBackStack(TAG_MAIN_MENU_FRAGMENT)
                .commit();
        rule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DataRepository<FlySightTrackData> repository = new DataRepository<>(
                FlySightTrackData.class, rule.getActivity().getContentResolver(), new FlySightCsvParser());

        Uri testTrackUri = Uri.parse("android.resource://com.watchthybridle.floatsight.test/" + com.watchthybridle.floatsight.test.R.raw.sample_track);
        repository.load(testTrackUri, rule.getActivity().getFlySightTrackDataViewModel());
    }

    @Test
    public void testTouchableMarkerShown() {
        onView(isRoot()).perform(Actions.wait(500));

        assertEquals(0, getNumberOfHighlights());
        onView(withId(R.id.root_chart_view))
                .perform(Actions.clickOnPosition(getChart().getRight() / 2, getChart().getBottom() / 2));
        assertEquals(1, getNumberOfHighlights());
    }

    @Test
    public void testTouchableMarkerDismiss() {
        onView(isRoot()).perform(Actions.wait(500));

        assertEquals(0, getNumberOfHighlights());
        onView(withId(R.id.root_chart_view))
                .perform(Actions.clickOnPosition(getChart().getRight() / 2, getChart().getBottom() / 2));
        assertEquals(1, getNumberOfHighlights());
        onView(isRoot()).perform(Actions.wait(500));

        Rect markerArea = ((TouchAbleMarkerView) getChart().getMarker()).getMarkerViewDrawArea();
        onView(withId(R.id.root_chart_view))
                .perform(Actions.clickOnPosition(markerArea.centerX(), markerArea.centerY()));
        assertEquals(0, getNumberOfHighlights());
    }

    @Test
    public void testKeepMarkerOnOrientationChange() {
        onView(isRoot()).perform(Actions.wait(500));

        assertEquals(0, getNumberOfHighlights());
        onView(withId(R.id.root_chart_view))
                .perform(Actions.clickOnPosition(getChart().getRight() / 2, getChart().getBottom() / 2));
        assertEquals(1, getNumberOfHighlights());

        rule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        onView(isRoot()).perform(Actions.wait(500));

        assertEquals(1, getNumberOfHighlights());
    }

    @Test
    public void testMarkerStaysDismissedAfterRotation() {
        onView(isRoot()).perform(Actions.wait(500));

        assertEquals(0, getNumberOfHighlights());
        onView(withId(R.id.root_chart_view))
                .perform(Actions.clickOnPosition(getChart().getRight() / 2, getChart().getBottom() / 2));
        assertEquals(1, getNumberOfHighlights());
        onView(isRoot()).perform(Actions.wait(500));

        Rect markerArea = ((TouchAbleMarkerView) getChart().getMarker()).getMarkerViewDrawArea();
        onView(withId(R.id.root_chart_view))
                .perform(Actions.clickOnPosition(markerArea.centerX(), markerArea.centerY()));
        assertEquals(0, getNumberOfHighlights());

        rule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        onView(isRoot()).perform(Actions.wait(500));

        assertEquals(0, getNumberOfHighlights());
    }

    private int getNumberOfHighlights() {
        Highlight[] highlights = getChart().getHighlighted();
        if (highlights == null) {
            return 0;
        } else {
            return highlights.length;
        }
    }

    private GlideOverlayChart getChart() {
        PlotFragment plotFragment =
                (PlotFragment) rule.getActivity().getSupportFragmentManager()
                        .findFragmentByTag(MainActivity.TAG_PLOT_FRAGMENT);
        return plotFragment.getChart();
    }
}