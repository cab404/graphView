package com.cab404.graphview;

/**
 * Created by cab404 on 07.12.17.
 */

public abstract class DataPoint2D<X, Y> {
    public X x;
    public Y y;

    public void set(X x, Y y){
        this.x = x;
        this.y = y;
    }

    public abstract void toWorld(Point2D to);

    public DataPoint2D(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + ":" + y;
    }
}
