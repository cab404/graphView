package com.cab404.graphview;

import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.List;


public interface GraphRenderer {

    void render(Canvas canvas, RectF viewport, List<? extends DataPoint2D> points);

}
