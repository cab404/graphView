package com.cab404.graphview.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.cab404.graphview.DataPoint;
import com.cab404.graphview.GraphRenderer;
import com.cab404.graphview.GraphView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Draws grid of lines and labels
 */

public class GridGraphRenderer<A extends GridGraphRenderer.LabeledDataPoint & DataPoint> implements GraphRenderer<A> {

    private Path path = new Path();
    private PointF tmp = new PointF();
    public Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * Minimum width between lines before labels will start disappearing
     */
    public int sectionMinimum = 32 * 2;

    private HashMap<Float, Float> visibility = new HashMap<>();
    private float visiblitiyDragUp = .15f;
    private float visiblitiyDragDown = .1f;
    private boolean changedFlag;

    /**
     * drags `what` to `to`, and sets `changedFlag` to true, if drag was performed
     */
    float drag(float what, float to) {
        if (what == to) return to;
        System.out.println("what " + what + " != to" + to );
        changedFlag = true;
        float dragged = what + (to - what) * (what > to ? visiblitiyDragDown : visiblitiyDragUp);
        // If distance to final value is less than .01, we consider
        // them practically same and engaging magnet clamps.
        if (Math.abs(dragged - to) < .01f) dragged = to;
        return dragged;
    }

    void dragPointVisibility(float value, float to) {
        visibility.put(value, drag(visibility(value), to));
    }

    float visibility(float of){
        if (!visibility.containsKey(of)) visibility.put(of, 0f);
        return visibility.get(of);
    }

    {
        paint.setStrokeWidth(1);
        paint.setColor(Color.GRAY);
        paint.setTextSize(24);
    }

    @Override
    public void render(GraphView view, Canvas canvas, RectF viewport, List<A> points) {
        path.reset();

        paint.setAlpha(255);
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

        int distanceSkipAccumulator = -sectionMinimum;

        changedFlag = false;
        // and drawing titles
        for (A point : points) {
            point.toWorld(tmp);
            float id = tmp.x;
            view.worldToCanvas(tmp);
            distanceSkipAccumulator -= tmp.x;
//            tmp.y *= -1;
            if (-distanceSkipAccumulator >= sectionMinimum) {
                dragPointVisibility(id, 1);
                distanceSkipAccumulator = 0;
            } else {
                dragPointVisibility(id, 0f);
            }

            paint.setAlpha((int) (255 * visibility(id)));
            canvas.drawText(point.getLabel(), tmp.x, canvas.getHeight(), paint);
            // marking all on-screen coordinate visibilities with "+1", so we can detect and remove offscreen values
            // (those always will be below 1.)
            visibility.put(id, visibility.get(id) + 1);

            distanceSkipAccumulator += tmp.x;
        }

        // removing offscreen values
        Iterator<Float> iterator = visibility.keySet().iterator();
        while (iterator.hasNext())
            if (visibility.get(iterator.next()) < 1f)
                iterator.remove();
        // removing marks
        for (Float id : visibility.keySet())
            visibility.put(id, visibility.get(id) - 1);


        if (changedFlag)
            view.invalidate();

        changedFlag = false;
    }

    public interface LabeledDataPoint {
        String getLabel();
    }

}
