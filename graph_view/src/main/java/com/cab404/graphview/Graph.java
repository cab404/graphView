package com.cab404.graphview;

import android.graphics.RectF;

import java.util.List;

public interface Graph<DP extends DataPoint> {

    /**
     *
     * @param viewport
     * @param extra number of extra out of bounds points
     *
     * @return all the points in given range.
     * if any parameter is null, that means it extends to corresponding infinity.
     */
    List<DP> lookupPoints(RectF viewport, int extra);
}
