package com.cab404.graphview.impl;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.cab404.graphview.DataPoint2D;
import com.cab404.graphview.GraphRenderer;
import com.cab404.graphview.Point2D;

import java.util.List;

/**
 * Created by cab404 on 08.12.17.
 */

public class BezierGraphRenderer implements GraphRenderer {

    public Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float fpf = .05f;
    private PointF
            start = new PointF(),
            end = new PointF(),
            approach_p1 = new PointF(),
            approach_p2 = new PointF();
    private Point2D tmpP2D = new Point2D();
    private Path path = new Path();

    @Override
    public void render(Canvas canvas, RectF viewport, List<? extends DataPoint2D> points) {
        path.reset();

        for (int i = 0; i < points.size() - 1; i++) {
            // Calculate graph start and end points
            points.get(i).toWorld(tmpP2D);
            start.set(
                    (tmpP2D.x - viewport.left) / viewport.width() * canvas.getWidth(),
                    (tmpP2D.y - viewport.top) / viewport.height() * canvas.getHeight()
            );
            points.get(i + 1).toWorld(tmpP2D);
            end.set(
                    (tmpP2D.x - viewport.left) / viewport.width() * canvas.getWidth(),
                    (tmpP2D.y - viewport.top) / viewport.height() * canvas.getHeight()
            );


            approach_p1.set((start.x + end.x) / 2 + (start.x - end.x) * fpf, start.y);
            approach_p2.set((start.x + end.x) / 2 - (start.x - end.x) * fpf, end.y);

            if (i == 0)
                path.moveTo(start.x, start.y);

            path.cubicTo(
                    approach_p1.x, approach_p1.y,
                    approach_p2.x, approach_p2.y,
                    end.x, end.y
            );

        }

        canvas.drawPath(path, strokePaint);

    }
}
