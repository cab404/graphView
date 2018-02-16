package com.cab404.graphview.impl;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.cab404.graphview.DataPoint;
import com.cab404.graphview.GraphRenderer;
import com.cab404.graphview.GraphView;

import java.util.List;

/**
 * Created by cab404 on 11.02.18.
 */

public class LabelRenderer implements GraphRenderer<LabelRenderer.LabeledDataPoint> {

    @Override
    public void render(GraphView view, Canvas canvas, RectF viewport, List<LabeledDataPoint> points) {

    }

    public interface LabeledDataPoint extends DataPoint {
        String getLabel();
    }

}
