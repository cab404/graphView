package com.cab404.graphview.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.cab404.graphview.DataPoint;
import com.cab404.graphview.GraphRenderer;
import com.cab404.graphview.GraphView;

import java.util.List;

/**
 * simple renderer of points, for use in conjunction with other renderers. Shows a good example of simple renderer.
 */

public class PointsGraphRenderer<A extends DataPoint> implements GraphRenderer<A> {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private PointF tmp = new PointF();

    /**
     * radius of points, in dp
     */
    public float pointSize = 4;
    /**
     * color of points
     */
    public int pointColor = Color.BLACK;

    @Override
    public void render(GraphView view, Canvas canvas, RectF viewport, List<A> points) {
        paint.setColor(pointColor);
        float dp = view.getResources().getDisplayMetrics().density;
        for (A point : points) {
            point.toWorld(tmp);
            view.worldToCanvas(tmp);
            canvas.drawCircle(tmp.x, tmp.y, pointSize * dp, paint);
        }

    }
}
