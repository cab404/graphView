package com.cab404.graphview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GraphView extends View {

    public GraphView(Context context) {
        super(context);
    }

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private List<GraphData> graphs = new ArrayList<>();
    private Bitmap graphTarget = null;
    private Canvas graphCanvas = null;
    private Paint graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public PointF
            min = new PointF(10, 10),
            max = new PointF(200, 200);
    /**
     * pinch rect
     */
    public RectF tPinch = new RectF();
    /**
     * previous pinch rect
     */
    public RectF tHPinch = new RectF();
    public RectF
            bounds = new RectF(0, 0, 200, 200),
            viewport = new RectF(0, 0, 100, 100),
            targetViewport = new RectF();

    public void addGraph(GraphData data) {
        graphs.add(data);
    }

    public void removeGraph(GraphData data) {
        graphs.remove(data);
    }

    /**
     * minimum distance between two same-axes finger points to trigger scaling
     */
    float minTrigger;

    {
        minTrigger = getResources().getDisplayMetrics().density * 10;
    }

    Matrix reverseMatrix = new Matrix();

    {
        reverseMatrix.postScale(1, -1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE && e.getHistorySize() > 0) {
            int ww = getWidth() - getPaddingLeft() - getPaddingRight();
            int wh = getHeight() - getPaddingTop() - getPaddingBottom();
            float xm = viewport.width() / ww;
            float ym = viewport.height() / wh;

            float x1 = e.getX(0) - getPaddingLeft();
            float hx1 = e.getHistoricalX(0, 0) - getPaddingLeft();
            float y1 = e.getY(0) - getPaddingTop();
            float hy1 = e.getHistoricalY(0, 0) - getPaddingTop();

            if (e.getPointerCount() == 1) {

                float dx = hx1 - x1;
                float dy = hy1 - y1;

                viewport.offset(dx * xm, dy * ym);

                invalidate();
            }

            if (e.getPointerCount() >= 2) {

                float x2 = e.getX(1) - getPaddingLeft();
                float hx2 = e.getHistoricalX(1, 0) - getPaddingLeft();
                float y2 = e.getY(1) - getPaddingTop();
                float hy2 = e.getHistoricalY(1, 0) - getPaddingTop();

                tPinch.set(
                        x1 / ww * viewport.width(),
                        y1 / wh * viewport.height(),
                        x2 / ww * viewport.width(),
                        y2 / wh * viewport.height()
                );
                tPinch.sort();

                tHPinch.set(
                        hx1 / ww * viewport.width(),
                        hy1 / wh * viewport.height(),
                        hx2 / ww * viewport.width(),
                        hy2 / wh * viewport.height()
                );
                tHPinch.sort();


                float dsx = tHPinch.width() / tPinch.width();
                float dsy = tHPinch.height() / tPinch.height();

                if (Math.abs(x1 - hx1) < minTrigger) dsx = 1f;
                if (Math.abs(y1 - hy1) < minTrigger) dsy = 1f;

/*
                ||||
                ||||
                ||||
                ||||


                HUGE PRISM ASCII ART


                *  =   +     -   *
                *   =    +      -    *
                *    =     +       -     *
                *     =      +        -      *
                *      =       +         -       *
                *       =        +          -        *
                *        =         +           -         *
                *         =          +            -          *
                *          =           +             -           *
                *           =            +              -            */
                viewport.top = tHPinch.top + (viewport.top - tPinch.top) * dsy;
                viewport.left = tHPinch.left + (viewport.left - tPinch.left) * dsx;
                viewport.right = tHPinch.right + (viewport.right - tPinch.right) * dsx;
                viewport.bottom = tHPinch.bottom + (viewport.bottom - tPinch.bottom) * dsy;

                invalidate();

            }

        }

        checkBroken();
        tryShiftingViewport();
        forceBounds();
        return true;
    }

    private boolean isBroken(float val) {
        return Float.isNaN(val) || Float.isInfinite(val);
    }

    private void checkBroken() {
        if (isBroken(viewport.top)) viewport.top = 0;
        if (isBroken(viewport.left)) viewport.left = 0;
        if (isBroken(viewport.right)) viewport.right = 0;
        if (isBroken(viewport.bottom)) viewport.bottom = 0;
    }

    private void forceBounds() {

        if (viewport.width() < min.x) {
            float c = viewport.centerX();
            viewport.left = c - min.x / 2;
            viewport.right = c + min.x / 2;
        }
        if (viewport.height() < min.y) {
            float c = viewport.centerY();
            viewport.top = c - min.y / 2;
            viewport.bottom = c + min.y / 2;
        }
        if (viewport.width() > max.x) {
            float c = viewport.centerX();
            viewport.left = c - max.x / 2;
            viewport.right = c + max.x / 2;
        }
        if (viewport.height() > max.y) {
            float c = viewport.centerY();
            viewport.top = c - max.y / 2;
            viewport.bottom = c + max.y / 2;
        }

        if (viewport.top < bounds.top) viewport.top = bounds.top;
        if (viewport.left < bounds.left) viewport.left = bounds.left;
        if (viewport.right > bounds.right) viewport.right = bounds.right;
        if (viewport.bottom > bounds.bottom) viewport.bottom = bounds.bottom;

    }

    private void tryShiftingViewport() {

        if (viewport.width() < max.x)
            if (viewport.left < bounds.left)
                viewport.offset(bounds.left - viewport.left, 0);
            else if (viewport.right > bounds.right)
                viewport.offset(bounds.right - viewport.right, 0);

        if (viewport.height() < max.y)
            if (viewport.top < bounds.top)
                viewport.offset(0, bounds.top - viewport.top);
            else if (viewport.bottom > bounds.bottom)
                viewport.offset(0, bounds.bottom - viewport.bottom);

    }

    private void onResize(int w, int h) {
        int graphW = w - getPaddingLeft() - getPaddingRight();
        int graphH = h - getPaddingTop() - getPaddingBottom();
        if (graphTarget != null)
            if (graphTarget.getWidth() != graphW || graphTarget.getHeight() != graphH) {
                graphTarget.recycle();
                graphTarget = null;
            }
        if (graphTarget == null) {
            graphTarget = Bitmap.createBitmap(graphW, graphH, Bitmap.Config.ARGB_8888);
            graphCanvas = new Canvas(graphTarget);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        onResize(w, h);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable("viewport", viewport);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable p) {
        super.onRestoreInstanceState(p);
        Bundle state = (Bundle) p;
        viewport = state.getParcelable("viewport");
    }

    private Point2D t1 = new Point2D(), t2 = new Point2D();

    <DP extends DataPoint2D> void render(GraphData<DP> data, Canvas on) {
        targetViewport.set(viewport);

        t1.set(targetViewport.left, targetViewport.bottom);
        t2.set(targetViewport.right, targetViewport.top);

        List<DP> points = data.dataset.lookupPoints(t1, t2, 1);
        for (int i = 0; i < data.renderers.size(); i++)
            data.renderers
                    .get(i)
                    .render(on, targetViewport, points);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        graphTarget.eraseColor(0);
        for (int i = 0; i < graphs.size(); i++) {
            GraphData data = graphs.get(i);
            render(data, graphCanvas);
        }
        canvas.drawBitmap(graphTarget, getPaddingLeft(), getPaddingTop(), graphPaint);
    }

}
