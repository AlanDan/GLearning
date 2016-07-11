package lesson.gamer.com.gl.one;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by daiepngfei on 7/11/16
 */
public abstract class BitmapSprite2D extends Sprite2D {

    private Bitmap mBitmap;
    private final Resources mResources;
    private final int mResouceId;

    public BitmapSprite2D(DirtyDrawer drawer, Resources resources, int resourceId, float x, float y) {
        super(drawer, x, y);
        this.mResources = resources;
        this.mResouceId = resourceId;
        SpriteBitmapPool.newInstance(resources).fetch(resourceId, new CCallback<Bitmap>() {
            @Override
            public void callback(Bitmap bitmap) {
                mBitmap = bitmap;
                setBounds(new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight()));
                onLoadedDidComplete(bitmap);
            }

            @Override
            public void reject(Exception e) {
                // TODO:
            }
        });
    }

    /**
     *
     * @param bitmap
     */
    protected void onLoadedDidComplete(Bitmap bitmap){

    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    @Override
    public boolean drawDirty(Canvas canvas, RectF dirty, long refreshTime, boolean force) {
        if(!super.drawDirty(canvas, dirty, refreshTime, force)){
            return false;
        }
        onDrawDirty(canvas, dirty);
        return true;
    }

    @Override
    protected boolean draw(Canvas canvas) {
        if(!super.draw(canvas) || (mBitmap == null)){
            return false;
        }
        onDraw(canvas);
        return true;
    }

    @Override
    public void release() {
        SpriteBitmapPool.newInstance(mResources).doReturn(mResouceId);
    }

    /**
     *
     * @param canvas
     * @param dirty
     */
    public abstract void onDrawDirty(Canvas canvas, RectF dirty);

    /**
     *
     * @param canvas
     */
    protected abstract void onDraw(Canvas canvas);
}
