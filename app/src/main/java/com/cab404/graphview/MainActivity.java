package com.cab404.graphview;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cab404.graphview.impl.BezierGraphRenderer;
import com.cab404.graphview.impl.SimpleGraph2D;

/**
 * Created by cab404 on 04.02.18.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GraphView view = new GraphView(this);
        setContentView(view);
        GraphData data = new GraphData();
        data.dataset = new SimpleGraph2D<Point2D>() {
            {
                for (int i = 0; i < 100; i += 5) {
                    points.add(new Point2D((float) i, (float) i));
                }
                System.out.println(points);
            }
        };
        view.setPadding(30, 30, 30, 30);
        BezierGraphRenderer renderer = new BezierGraphRenderer();
        renderer.strokePaint.setColor(Color.BLACK);
        renderer.strokePaint.setStrokeWidth(5);
        renderer.strokePaint.setStyle(Paint.Style.STROKE);
        data.renderers.add(renderer);

        view.viewport.set(0, 0, 100, 100);
        view.min.set(30, 50);
        view.max.set(100, 100);
        view.bounds.set(0, 0, 100, 100);
        view.addGraph(data);

    }

}
