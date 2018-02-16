package com.cab404.graphview;

import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.List;


public interface GraphRenderer<A extends DataPoint> {

    /**
     * <br/>so, you actually draw graph with inverted values,
     * because android y is not invertible without fonts pain on canvas, and I don't pre-invert with matrix it because fonts.
     * <br/>because of that you should drow as if your y is inverted — basically if you want to draw (x:y), you should draw (x:-y)
     * <br/>
     * <br/>but you actually can skip that trouble unless you are drawing text by applying {@link GraphView#inverseMatrix}
     * <br/>just don't forget to remove it afterwards
     */
    void render(GraphView view, Canvas canvas, RectF viewport, List<A> points);

}
