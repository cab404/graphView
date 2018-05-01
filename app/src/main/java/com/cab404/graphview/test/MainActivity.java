package com.cab404.graphview.test;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;

import com.cab404.graphview.GraphData;
import com.cab404.graphview.GraphView;
import com.cab404.graphview.impl.BezierGraphRenderer;
import com.cab404.graphview.impl.GridGraphRenderer;
import com.cab404.graphview.impl.Point2D;
import com.cab404.graphview.impl.PointsGraphRenderer;
import com.cab404.graphview.impl.SimpleGraph;

/**
 * Created by cab404 on 04.02.18.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GraphView view = findViewById(R.id.vGraph);
        GraphData<Point2D> data = new GraphData<>();
        data.dataset = new SimpleGraph<Point2D>() {
            {
                for (int i = 0; i <= 400; i += 5) {
                    final int finalI = i;
                    points.add(new Point2D((float) finalI, (float) Math.random() * 100) {
                        @Override
                        public String toString() {
                            return finalI + "";
                        }
                    });
                }
            }
        };

        BezierGraphRenderer<Point2D> renderer = new BezierGraphRenderer<>();
        renderer.strokePaint.setColor(Color.BLACK);
        renderer.strokePaint.setStrokeWidth(5);
        renderer.strokePaint.setStyle(Paint.Style.STROKE);

        data.renderers.add(new GridGraphRenderer<Point2D>());
        data.renderers.add(renderer);
        data.renderers.add(new PointsGraphRenderer<Point2D>());


        view.viewport.set(0, 0, 100, 100);
        view.min.set(30, 100);
        view.max.set(100, 100);
        view.bounds.set(0, 0, 400, 100);
        view.addGraph(data);

    }

}
