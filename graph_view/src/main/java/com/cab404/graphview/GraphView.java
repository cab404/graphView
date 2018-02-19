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

    /**
     * list of all graphs, self explanatory
     */
    private List<GraphData> graphs = new ArrayList<>();

    /**
     * we draw a graph with it on {@link #graphTarget}
     */
    private Canvas graphCanvas = null;

    /**
     * we draw a graph on it
     */
    private Bitmap graphTarget = null;

    /**
     * really we only draw a bitmap on a canvas with it
     */
    private Paint graphPaint = new Paint();

    /**
     * min viewport size
     */
    public PointF min = new PointF(10, 10);

    /**
     * max viewport size
     */
    public PointF max = new PointF(200, 200);

    /**
     * pinch rect
     */
    private RectF tPinch = new RectF();

    /**
     * previous pinch rect
     */
    private RectF tHPinch = new RectF();

    /**
     * bounds for viewport
     */
    public RectF bounds = new RectF(0, 0, 200, 200);

    /**
     * viewport. basically, which part of graph is visible on the screen.
     */
    public RectF viewport = new RectF(0, 0, 100, 100);

    /**
     * matrix for moving origin to bottom left corner (where it belongs) and pointing Y origin upwards.
     */
    public Matrix inverseMatrix = new Matrix();

    /**
     * graph canvas width - basically width without padding
     */
    private int wh = 1;
    /**
     * graph canvas width - basically width without padding
     */
    private int ww = 1;

    /**
     * speed with which graph currently moves.
     */
    private RectF kinetic = new RectF();

    /**
     * whether view is currently being touched or not. mostly for applying kinetic energy.
     */
    private boolean touched = false;

    /**
     * deceleration speed of kinetic movement
     */
    private float kineticFalloff = .95f;


    /**
     * adds a graph to render list
     */
    public void addGraph(GraphData data) {
        graphs.add(data);
        invalidate();
    }

    /**
     * removes a graph from render list
     */
    public void removeGraph(GraphData data) {
        graphs.remove(data);
        invalidate();
    }

    /**
     * minimum distance between two same-axes finger points to trigger scaling.
     * probably not really worth tuning for library end user, so whatever
     * if you see something strange while
     */
    float minTrigger;

    {
        minTrigger = getResources().getDisplayMetrics().density * 20;
    }

    Matrix graphCanvasMatrix = new Matrix();

    {
        graphCanvasMatrix.postScale(1, -1);
    }

    /**
     * transforms world coordinates (that are returned by {@link DataPoint#toWorld(PointF) toWorld}) to graph canvas coordinates.
     * <br/>useful in graph renderers
     */
    public PointF worldToCanvas(PointF use) {
        use.x = (use.x - viewport.left) / viewport.width() * ww;
        use.y = (use.y - viewport.top) / viewport.height() * wh;
        return use;
    }

    private void startRecordingScroll() {
        kinetic.set(viewport);
    }

    private void endRecordingScroll() {
        kinetic.top -= viewport.top;
        kinetic.left -= viewport.left;
        kinetic.right -= viewport.right;
        kinetic.bottom -= viewport.bottom;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        System.out.println(e.getAction());

        if (e.getAction() == MotionEvent.ACTION_MOVE && e.getHistorySize() > 0) {

            touched = true;

            // saving viewport for the sake of calculating speed later
            startRecordingScroll();

            // we're taking padding from all of out further work with coordinates

            // i hate padding
            // it always ruins everything
            // except for buttons, padding is good there

            float xm = viewport.width() / ww;
            float ym = viewport.height() / wh;

            // also we invert y because fuck android canvas

            float x1 = e.getX(0) - getPaddingLeft();
            float hx1 = e.getHistoricalX(0, 0) - getPaddingLeft();
            float y1 = wh - e.getY(0) + getPaddingTop();
            float hy1 = wh - e.getHistoricalY(0, 0) + getPaddingTop();

            if (e.getPointerCount() == 1) {

                float dx = hx1 - x1;
                float dy = hy1 - y1;

                // not really much to look at in 1-fingered assault

                viewport.offset(dx * xm, dy * ym);

                invalidate();
            }

            if (e.getPointerCount() >= 2) {

                float x2 = e.getX(1) - getPaddingLeft();
                float hx2 = e.getHistoricalX(1, 0) - getPaddingLeft();
                float y2 = wh - e.getY(1) + getPaddingTop();
                float hy2 = wh - e.getHistoricalY(1, 0) + getPaddingTop();

                /* converting screen coords to viewport coords */
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

                // capping off minimal distance between finger axis to something reasonable.
                // this prevents flying off to infinity and beyond when finger axis are crossing.
                if (Math.abs(x1 - hx1) < minTrigger) dsx = 1f;
                if (Math.abs(y1 - hy1) < minTrigger) dsy = 1f;

/*
                ||||
                ||||
                ||||
                ||||

           [ HUGE PRISM ASCII ART HERE ]

                v   =    +      -    *
                v    =     +       -     *
                v     =      +        -      *
                v      =       +         -       *
                v       =        +          -        *
                v        =         +           -         *
                v         =          +            -          *
                v          =           +             -           *
                v           =            +              -            */
                viewport.top = tHPinch.top + (viewport.top - tPinch.top) * dsy;
                viewport.left = tHPinch.left + (viewport.left - tPinch.left) * dsx;
                viewport.right = tHPinch.right + (viewport.right - tPinch.right) * dsx;
                viewport.bottom = tHPinch.bottom + (viewport.bottom - tPinch.bottom) * dsy;

                invalidate();

            }

            endRecordingScroll();

        }

        if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_CANCEL) {
            touched = false;
            invalidate();
        }

        viewportCheck();
        return true;
    }

    private boolean isBroken(float val) {
        return Float.isNaN(val) || Float.isInfinite(val);
    }

    /**
     * Checks for any non-finite numbers in viewport
     */
    private void checkBroken() {
        if (isBroken(viewport.top)) viewport.top = 0;
        if (isBroken(viewport.left)) viewport.left = 0;
        if (isBroken(viewport.right)) viewport.right = 0;
        if (isBroken(viewport.bottom)) viewport.bottom = 0;
    }

    /**
     * Stuffs user inbounds and shuts the lid
     */
    private void forceBounds() {

        if (viewport.top < bounds.top) {
            viewport.top = bounds.top;
            if (viewport.height() < min.y)
                viewport.bottom = bounds.top + min.y;
        }
        if (viewport.left < bounds.left) {
            viewport.left = bounds.left;
            if (viewport.width() < min.x)
                viewport.right = bounds.left + min.x;
        }
        if (viewport.right > bounds.right) {
            viewport.right = bounds.right;
            if (viewport.width() < min.x)
                viewport.left = bounds.right - min.x;
        }
        if (viewport.bottom > bounds.bottom) {
            viewport.bottom = bounds.bottom;
            if (viewport.height() < min.y)
                viewport.top = bounds.bottom - min.y;
        }


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

    }

    /**
     * Tries its best to gently move user inbounds
     */
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
        ww = w - getPaddingLeft() - getPaddingRight();
        wh = h - getPaddingTop() - getPaddingBottom();

        // recreating canvas and bitmap
        if (graphTarget != null)
            if (graphTarget.getWidth() != ww || graphTarget.getHeight() != wh) {
                graphTarget.recycle();
                graphTarget = null;
            }

        if (graphTarget == null) {
            graphTarget = Bitmap.createBitmap(ww, wh, Bitmap.Config.ARGB_8888);
            graphCanvas = new Canvas(graphTarget);
        }

        // updating origin correction matrix
        inverseMatrix.reset();
        inverseMatrix.postScale(1, -1);
        inverseMatrix.postTranslate(0, wh);

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

    <DP extends DataPoint> void render(GraphData<DP> data, Canvas on) {

        List<DP> points = data.dataset.lookupPoints(viewport, 1);

        for (int i = 0; i < data.renderers.size(); i++)
            data.renderers.get(i).render(this, on, viewport, points);

    }

    private boolean applySpeed() {
        viewport.top -= kinetic.top;
        viewport.left -= kinetic.left;
        viewport.right -= kinetic.right;
        viewport.bottom -= kinetic.bottom;


        kinetic.top *= kineticFalloff;
        kinetic.left *= kineticFalloff;
        kinetic.right *= kineticFalloff;
        kinetic.bottom *= kineticFalloff;

        if (Math.abs(kinetic.top) < .01f) kinetic.top = 0;
        if (Math.abs(kinetic.left) < .01f) kinetic.left = 0;
        if (Math.abs(kinetic.right) < .01f) kinetic.right = 0;
        if (Math.abs(kinetic.bottom) < .01f) kinetic.bottom = 0;


        if (kinetic.top != 0) return true;
        if (kinetic.left != 0) return true;
        if (kinetic.right != 0) return true;
        if (kinetic.bottom != 0) return true;

        return false;
    }

    private void viewportCheck() {
        checkBroken();
        tryShiftingViewport();
        forceBounds();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!touched && applySpeed()) invalidate();
        viewportCheck();

        if (!bounds.contains(viewport)) {
            System.out.println("viewport is still OOB, check your bound functions!!! " + viewport);
        }

        graphTarget.eraseColor(0);
        for (int i = 0; i < graphs.size(); i++)
            render(graphs.get(i), graphCanvas);

        canvas.drawBitmap(graphTarget, getPaddingLeft(), getPaddingTop(), graphPaint);

    }

}
