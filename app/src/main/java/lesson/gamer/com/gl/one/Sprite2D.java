package lesson.gamer.com.gl.one;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

/**
 * Created by daiepngfei on 7/11/16
 */
public abstract class Sprite2D implements Dirtyable, Releaseable {

    private State mState;
    private Rect mBounds;
    private RectF mDrawingRect;
    private RectF mDrawingDirtyRect;
    private final DirtyDrawer fDrawer;
    private long mLastFrameRefresTime;
    private float mPositionX;
    private float mPositionY;

    public Sprite2D(DirtyDrawer drawer, float x, float y){
        this(drawer);
        setPositionX(x);
        setPositionY(y);
    }

    /**
     *
     * @param r
     */
    protected void requestDrawDirty(RectF r) {
        if (fDrawer != null) {
            fDrawer.requestDrawDirty(r);
        }
    }

    /**
     *
     * @param r
     */
    protected void setBounds(Rect r){
        this.mBounds = r;
    }

    private Sprite2D(DirtyDrawer drawer) {
        if(drawer == null){
            throw new IllegalArgumentException("We must apply a DirtyDrawer for every sprite!");
        }
        this.fDrawer = drawer;
        mState = onCreatCustomState();
        if(mState == null){
            mState = new State();
        }
    }

    public Rect getBounds() {
        return mBounds;
    }

    public RectF getDrawingRect() {
        return mDrawingRect;
    }

    protected State getState() {
        return mState;
    }

    /**
     *
     * @param motionEvent
     * @return
     */
    public abstract boolean onTouchEvent(MotionEvent motionEvent);

    /**
     *
     * @return
     */
    protected abstract State onCreatCustomState();

    @Override
    public boolean drawDirty(Canvas canvas, RectF dirty, long refreshTime, boolean force) {
        if(mLastFrameRefresTime == refreshTime && !force){
            return false;
        }
        return true;
    }

    public void setPositionX(float mPositionX) {
        this.mPositionX = mPositionX;
    }

    public float getPositionX() {
        return mPositionX;
    }

    public float getPositionY() {
        return mPositionY;
    }

    public void setPositionY(float mPositionY) {
        this.mPositionY = mPositionY;
    }

    protected static class State {
        public boolean invisible;
    }

    protected boolean draw(Canvas canvas){
        if(mState.invisible){
            return false;
        }
        return true;
    }


    protected void setDrawingRect(RectF drawingRect){
        this.mDrawingDirtyRect = this.mDrawingRect = drawingRect;
    }

    protected long getLastDirtyRefreshTime() {
        return mLastFrameRefresTime;
    }

}
