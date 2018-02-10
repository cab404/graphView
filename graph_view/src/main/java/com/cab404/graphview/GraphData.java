package com.cab404.graphview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by cab404 on 12.12.17.
 */

public class GraphData<DP extends DataPoint2D> {
    public Graph2D<DP> dataset;
    public List<GraphRenderer> renderers = new ArrayList<>();
}
