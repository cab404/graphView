package com.cab404.graphview.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.cab404.graphview.DataPoint;
import com.cab404.graphview.GraphRenderer;
import com.cab404.graphview.GraphView;

import java.util.List;

/**
 * Draws grid of lines and labels
 */

public class GridGraphRenderer<A extends GridGraphRenderer.LabeledDataPoint & DataPoint> implements GraphRenderer<A> {

    private Path path = new Path();
    private PointF tmp = new PointF();
    public Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    {
        paint.setStrokeWidth(1);
        paint.setColor(Color.GRAY);
        paint.setTextSize(24);
    }

    @Override
    public void render(GraphView view, Canvas canvas, RectF viewport, List<A> points) {
        path.reset();

        // drawing lines
        for (A point : points) {
            point.toWorld(tmp);
            tmp.y = viewport.top;
            view.worldToCanvas(tmp);
            path.moveTo(tmp.x, tmp.y);

            point.toWorld(tmp);
            tmp.y = viewport.bottom;
            view.worldToCanvas(tmp);
            path.lineTo(tmp.x, tmp.y);
        }
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, paint);

        // and drawing titles
        for (A point : points) {
            point.toWorld(tmp);
            view.worldToCanvas(tmp);
//            tmp.y *= -1;
            canvas.drawText(point.getLabel(), tmp.x, tmp.y, paint);
        }

    }

    public interface LabeledDataPoint {
        String getLabel();
    }

}
