package com.cab404.graphview.impl;

import android.graphics.PointF;

import com.cab404.graphview.DataPoint;

public class Point2D implements DataPoint, GridGraphRenderer.LabeledDataPoint {
    public float x;
    public float y;

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point2D() {
    }

    public Point2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + ":" + y;
    }

    @Override
    public void toWorld(PointF to) {
        to.x = x;
        to.y = y;
    }

    @Override
    public String getLabel() {
        return toString();
    }
}
