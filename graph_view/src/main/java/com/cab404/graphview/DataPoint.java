package com.cab404.graphview;

import android.graphics.PointF;

/**
 * all the graph data points should implement this.
 */

public interface DataPoint {
    /**
     * returns world coordinate of this point.
     */
    void toWorld(PointF to);
}
