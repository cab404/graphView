package com.cab404.graphview;

import java.util.ArrayList;
import java.util.List;

/**
 * yah, that's all we need to know about a dataset
 * Created by cab404 on 12.12.17.
 */

@SuppressWarnings("WeakerAccess")
public class GraphData<DP extends DataPoint> {
    public Graph2D<DP> dataset;
    public List<GraphRenderer<DP>> renderers = new ArrayList<>();
}
