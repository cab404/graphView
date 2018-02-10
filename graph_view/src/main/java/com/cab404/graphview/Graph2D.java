package com.cab404.graphview;

import java.util.List;

public interface Graph2D<DP extends DataPoint2D> {

    /**
     * @param from Lower bounds of points.
     * @param to
     * @param extra
     *
     * @return all the points in given range.
     * if any parameter is null, that means it extends to corresponding infinity.
     */
    List<DP> lookupPoints(Point2D from, Point2D to, int extra);
}
