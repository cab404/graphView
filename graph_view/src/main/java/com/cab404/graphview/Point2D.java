package com.cab404.graphview;

public class Point2D extends DataPoint2D<Float, Float> {
    @Override
    public void toWorld(Point2D to) {
        to.set(x, y);
    }

    public Point2D(Float x, Float y) {
        super(x, y);
    }

    public Point2D() {
        super(0f, 0f);
    }

}
