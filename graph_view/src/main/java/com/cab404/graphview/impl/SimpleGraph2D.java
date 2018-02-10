package com.cab404.graphview.impl;

import com.cab404.graphview.DataPoint2D;
import com.cab404.graphview.Graph2D;
import com.cab404.graphview.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cab404 on 05.02.18.
 */

public class SimpleGraph2D<A extends DataPoint2D> implements Graph2D<A> {

    public List<A> points = new ArrayList<>();

    private Point2D tmp = new Point2D();

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

    private Point2D world(int index) {
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
    public List<A> lookupPoints(Point2D from, Point2D to, int extra) {
        int start = binarySearchApprox(from.x, 0, points.size() - 1, true) - extra;
        int end = binarySearchApprox(to.x, 0, points.size() - 1, false) + extra;
        if (end > points.size() - 1) end = points.size() - 1;
        if (start < 0) start = 0;
        if (start > end) {
            int swap = end;
            end = start;
            start = swap;
        }
        return points.subList(start, end + 1);
    }
}
