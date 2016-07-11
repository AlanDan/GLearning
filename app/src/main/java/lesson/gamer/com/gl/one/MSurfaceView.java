package lesson.gamer.com.gl.one;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.LinkedList;

import lesson.gamer.com.gl.R;

/**
 * Created by daiepngfei on 7/11/16
 */
public class MSurfaceView extends SurfaceView implements SurfaceHolder.Callback, DirtyDrawer, Dirtyable {

    private SurfaceHolder mSurfaceHolder;
    private DrawThread mDrawThread;
    private Actor2D mActor2D;
    private Paint mBackgroundPaint;

    public MSurfaceView(Context context) {
        super(context);
        initialized();
    }

    public MSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialized();
    }

    public void initialized() {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(Color.BLACK);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        System.out.println("Holder is " + holder.getSurfaceFrame().toString());

        if (mActor2D == null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            mActor2D = new Actor2D(this, getResources(), R.drawable.sprite, dm.widthPixels / 2, dm.heightPixels / 2, 4, 4);
        }

        if (mDrawThread == null || mDrawThread.hasStopped()) {
            mDrawThread = new DrawThread();
            mDrawThread.start();
        } else {
            mDrawThread.doResume();
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        onPause();
    }

    @Override
    public void draw(Canvas canvas) {
        //canvas.drawRect(mSurfaceHolder.getSurfaceFrame(), mBackgroundPaint);
        if (mActor2D != null) {
            mActor2D.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mActor2D != null) {
            mActor2D.onTouchEvent(event);
        }
        return true;
    }

    @Override
    public void requestDrawDirty(RectF rectF) {
        if(mDrawThread != null){
            mDrawThread.postDirty(rectF);
        }
    }

    @Override
    public boolean drawDirty(Canvas canvas, RectF dirty, long refreshTime, boolean force) {

        canvas.drawRect(dirty, mBackgroundPaint);

        return false;
    }

    private class DrawThread extends Thread {

        private static final int DRAW_INTERVAL = 17;
        private boolean mRunning = true;
        private long mTickTime;
        private final LinkedList<RectF> mDirtyQueue = new LinkedList<>();

        @Override
        public void run() {
            updateTickTime();
            while (mRunning) {
                Canvas canvas = mSurfaceHolder.lockCanvas();
                if(mDirtyQueue.size() > 0) {
                    RectF dirty = mDirtyQueue.poll();
                    synchronized (mDirtyQueue){
                        drawDirty(canvas, dirty, System.currentTimeMillis(), false);
                    }
                } else {
                    draw(canvas);
                }
                mSurfaceHolder.unlockCanvasAndPost(canvas);
                tryingKeepFrames();
            }

        }

        private void tryingKeepFrames() {
            final long deltaTime = System.currentTimeMillis() - mTickTime;
            if (deltaTime < DRAW_INTERVAL) {
                try {
                    Thread.sleep(DRAW_INTERVAL - deltaTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            updateTickTime();
        }

        public void doResume() {
            this.notify();
            updateTickTime();
        }

        public void doPause() {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void doStop() {
            mRunning = false;
        }

        public boolean hasStopped() {
            return !mRunning;
        }

        private void updateTickTime() {
            mTickTime = System.currentTimeMillis();
        }

        public void postDirty(RectF dirty){

            if(dirty == null){
                return;
            }
            synchronized (mDirtyQueue) {
                mDirtyQueue.offer(dirty);
            }
        }

    }

    public void onPause() {
        mDrawThread.doStop();
    }

}
