package com.cab404.graphview.impl;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.cab404.graphview.DataPoint;
import com.cab404.graphview.GraphRenderer;
import com.cab404.graphview.GraphView;

import java.util.List;

/**
 * renders bezier curve through points
 * modify everything using paint
 * Created by cab404 on 08.12.17.
 */

public class BezierGraphRenderer<A extends DataPoint> implements GraphRenderer<A> {

    public Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * moves bezier approach vectors closer or further to start points
     */
    private float smoothness = .05f;
    private PointF
            start = new PointF(),
            end = new PointF(),
            approach_p1 = new PointF(),
            temp = new PointF(),
            approach_p2 = new PointF();
    private RectF rejectRect = new RectF();
    private Path path = new Path();

    @Override
    public void render(GraphView view, Canvas canvas, RectF viewport, List<A> points) {
        path.reset();

        for (int i = 0; i < points.size() - 1; i++) {

            // Calculate segment start and end points
            points.get(i).toWorld(temp);
            rejectRect.left = temp.x;
            rejectRect.top = temp.y;
            start.set(view.worldToCanvas(temp));

            points.get(i + 1).toWorld(temp);
            rejectRect.right = temp.x;
            rejectRect.bottom = temp.y;
            end.set(view.worldToCanvas(temp));

            // rejecting some oob segments
            rejectRect.sort();
            if (!rejectRect.intersect(viewport)) continue;

            // making some approach vectors
            approach_p1.set((start.x + end.x) / 2 + (start.x - end.x) * smoothness, start.y);
            approach_p2.set((start.x + end.x) / 2 - (start.x - end.x) * smoothness, end.y);

            path.moveTo(start.x, start.y);

            path.cubicTo(
                    (start.x + end.x) / 2 + (start.x - end.x) * smoothness, start.y,
                    (start.x + end.x) / 2 - (start.x - end.x) * smoothness, end.y,
                    end.x, end.y
            );

        }

        canvas.drawPath(path, strokePaint);

    }
}
