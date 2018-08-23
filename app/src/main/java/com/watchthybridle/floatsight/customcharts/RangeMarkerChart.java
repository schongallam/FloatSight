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

package com.watchthybridle.floatsight.customcharts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.chartfragments.AllMetricsTimeChartMarker;
import com.watchthybridle.floatsight.linedatasetcreation.AllMetricsVsTimeChartDataSetHolder;
import com.watchthybridle.floatsight.linedatasetcreation.ChartDataSetProperties;

import java.text.DecimalFormat;
import java.util.List;

import static com.github.mikephil.charting.components.YAxis.YAxisLabelPosition.INSIDE_CHART;

public class RangeMarkerChart extends LineChart {

    private Paint rangePaint;
    private int limitLineColor;

    public RangeMarkerChart(Context context) {
        super(context);
        rangePaint = new Paint();
        rangePaint.setStyle(Paint.Style.FILL);
        rangePaint.setColor(ContextCompat.getColor(context, R.color.rangeColor));
        limitLineColor = ContextCompat.getColor(context, R.color.limitLineColor);
    }

    public void setRangeMarker() {
        List<LimitLine> limitLines = getXAxis().getLimitLines();
        if (limitLines.size() > 1) {
            limitLines.remove(0);
        }
        Highlight[] highlights = getHighlighted();
        if (highlights != null && highlights.length > 0) {
            float xPos = highlights[0].getX();
            LimitLine limitLine = createLimitLine(xPos);
            limitLines.add(limitLine);
        }
        invalidate();
    }

    private LimitLine createLimitLine(float position) {
        LimitLine limitLine = new LimitLine(position);
        limitLine.setLineColor(limitLineColor);
        limitLine.setLineWidth(2);
        return limitLine;
    }

    public void clearRangeMarkers() {
        getXAxis().removeAllLimitLines();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        List<LimitLine> limitLines = mXAxis.getLimitLines();
        if (limitLines != null && limitLines.size() == 2) {
            float[] pointValues = new float[4];
            float limitA = limitLines.get(0).getLimit();
            float limitB = limitLines.get(1).getLimit();
            if (limitA > limitB) {
                pointValues[0] = limitB;
                pointValues[2] = limitA;
            } else {
                pointValues[0] = limitA;
                pointValues[2] = limitB;
            }

            mLeftAxisTransformer.pointValuesToPixel(pointValues);
            canvas.drawRect(pointValues[0], mViewPortHandler.contentBottom(), pointValues[2], mViewPortHandler.contentTop(), rangePaint);
        }

        super.onDraw(canvas);
    }
}

