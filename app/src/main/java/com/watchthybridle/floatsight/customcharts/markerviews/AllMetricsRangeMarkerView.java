package com.watchthybridle.floatsight.customcharts.markerviews;

import android.content.Context;
import android.widget.TextView;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;
import com.watchthybridle.floatsight.customcharts.GlideOverlayChart;
import com.watchthybridle.floatsight.linedatasetcreation.ChartDataSetProperties;

import java.util.List;

public class AllMetricsRangeMarkerView extends RangeMarkerView {

    public AllMetricsRangeMarkerView(Context context) {
        super(context, R.layout.all_metrics_range_marker);
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        GlideOverlayChart chart = (GlideOverlayChart) AllMetricsRangeMarkerView.this.getChartView();
        List<LimitLine> limitLines = chart.getXAxis().getLimitLines();
        float limitStart = limitLines.get(0).getLimit();
        float limitEnd = limitLines.get(1).getLimit();
        FlySightTrackData flySightTrackData = chart.getDataSetHolder().getFlySightTrackData();

        List<ChartDataSetProperties> dataSetPropertiesList = chart.getDataSetHolder().getDataSetPropertiesList();
        for (ChartDataSetProperties chartDataSetProperties : dataSetPropertiesList) {
            setTextView(chartDataSetProperties, flySightTrackData);
        }
        TextView textView = findViewById(R.id.time_marker_text_view);
        textView.setTextColor(getResources().getColor(R.color.time));
        float timeDiff = limitEnd - limitStart;

        textView.setText(getContext().getString(R.string.seconds, ChartDataSetProperties.TIME_FORMAT.format(timeDiff)));
        super.refreshContent(entry, highlight);
    }

    private void setTextView(ChartDataSetProperties dataSetProperties, FlySightTrackData trackData) {
        GlideOverlayChart chart = (GlideOverlayChart) AllMetricsRangeMarkerView.this.getChartView();
        List<LimitLine> limitLines = chart.getXAxis().getLimitLines();
        float limitStart = limitLines.get(0).getLimit();
        float limitEnd = limitLines.get(1).getLimit();
        String formattedValue = dataSetProperties.getFormattedValueForRange(getContext(), limitStart, limitEnd, trackData);

        TextView textView = findViewById(dataSetProperties.markerTextView);
        textView.setTextColor(getResources().getColor(dataSetProperties.color));
        textView.setText(formattedValue);
    }
}
