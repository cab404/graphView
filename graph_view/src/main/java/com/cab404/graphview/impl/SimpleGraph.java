package com.cab404.graphview.impl;

import android.graphics.PointF;
import android.graphics.RectF;

import com.cab404.graphview.DataPoint;
import com.cab404.graphview.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * simple graph thing, does a reasonable job of getting points
 */

public class SimpleGraph<A extends DataPoint> implements Graph<A> {

    public List<A> points = new ArrayList<>();

    private PointF tmp = new PointF();

    public void sortPoints() {
        Collections.sort(points, new Comparator<A>() {
            @Override
            public int compare(A a, A b) {
                a.toWorld(tmp);
                float x1 = tmp.x;
                b.toWorld(tmp);
                float x2 = tmp.x;
                return Float.compare(x1, x2);
            }
        });
    }

    private PointF world(int index) {
        points.get(index).toWorld(tmp);
        return tmp;
    }

    private int binarySearchApprox(float x, int s, int e, boolean low) {
        if (world(s).x >= x)
            return s;
        if (world(e).x <= x)
            return e;
        if (e - s == 1 && world(s).x < x && world(e).x > x)
            return low ? s : e;
        if (e - s == 0) {
            System.out.println("should not happen!");
            return e;
        }
        int center = (e - s) / 2 + s;
        if (world(center).x < x)
            return binarySearchApprox(x, center, e, low);
        else
            return binarySearchApprox(x, s, center, low);
    }

    @Override
    public List<A> lookupPoints(RectF viewport, int extra) {
        int limit = points.size() - 1;
        int start = binarySearchApprox(viewport.left, 0, limit, true);
        int end = binarySearchApprox(viewport.right, 0, limit, false);
        if (start > end) {
            int swap = end;
            end = start;
            start = swap;
        }
        start -= extra;
        end += extra;
        if (end > limit) end = limit;
        if (start < 0) start = 0;
        return points.subList(start, end + 1);
    }
}
