package lesson.gamer.com.gl.one;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

/**
 * Created by daiepngfei on 7/11/16
 */
public class Actor2D extends BitmapSprite2D implements Moveable, OnStateChangedListener {

    private Rect[][] mBitmapPieces;
    private Rect mClipRect;
    private int mBitmapRows, mBitmapColumns;
    private int mStep, mLastStep;
    private long mLastClipTime;
    private Paint mBlueTestPaint = new Paint();
    private short[] mFacesOrder;
    private static final int FACT_TIME = 450;

    /**
     * @param drawer
     * @param resources
     * @param resourceId
     * @param x
     * @param y
     * @param row
     * @param column
     */
    public Actor2D(DirtyDrawer drawer, Resources resources, int resourceId, float x, float y, int row, int column) {
        this(drawer, resources, resourceId, x, y, row, column, null);
    }

    /**
     * @param drawer
     * @param resources
     * @param resourceId
     * @param x
     * @param y
     * @param row
     * @param column
     * @param facesOrder
     */
    public Actor2D(DirtyDrawer drawer, Resources resources, int resourceId, float x, float y, int row, int column, short[] facesOrder) {
        super(drawer, resources, resourceId, x, y);
        if (row < 1 || column < 1) {
            throw new IllegalArgumentException("Can't use params like row or column < 1 ");
        }
        if (facesOrder == null) {
            facesOrder = generateDefaultFaceOrder();
        }

        //test
        if (BuildConfig.DEBUG) {
            mBlueTestPaint.setColor(Color.argb(255, 121, 86, 209));
            mBlueTestPaint.setStrokeWidth(1.0f);
        }

        // get row & column
        mFacesOrder = facesOrder;
        mBitmapRows = row;
        mBitmapColumns = column;

    }

    /**
     * @return
     */
    private short[] generateDefaultFaceOrder() {
        return new short[]{
                ActorState.FACE_S,
                ActorState.FACE_W,
                ActorState.FACE_E,
                ActorState.FACE_N,
        };
    }

    @Override
    protected void onLoadedDidComplete(Bitmap bitmap) {
        // get bounds' width & height
        final Rect bounds = getBounds();
        final int width = bounds.width() / mBitmapColumns;
        final int height = bounds.height() / mBitmapRows;

        // init pieces
        mBitmapPieces = new Rect[mBitmapRows][mBitmapColumns];
        for (int i = 0; i < mBitmapRows; i++) {
            for (int j = 0; j < mBitmapColumns; j++) {
                final int px = j * width;
                final int py = i * height;
                mBitmapPieces[i][j] = new Rect(px, py, px + width, py + height);
            }
        }

        updateAllDrawRegions();
    }

    @Override
    public void onDrawDirty(Canvas canvas, RectF dirty) {
        onDraw(canvas);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (isReadyToDraw()) {
            updateSteps();

            if(BuildConfig.DEBUG) {
                canvas.drawRect(getDrawingRect(), mBlueTestPaint);
            }

            updateAllDrawRegions();
            requestDrawDirty(getDrawingRect());

            canvas.save();
            canvas.clipRect(getDrawingRect());
            canvas.drawBitmap(getBitmap(), mClipRect, getDrawingRect(), null);
            canvas.restore();


        }
    }

    /**
     * @return
     */
    private boolean isReadyToDraw() {
        return getBitmap() != null && mClipRect != null && getDrawingRect() != null && getBounds() != null;
    }

    /**
     * @return
     */
    private Rect getClipRect() {

        if (mBitmapPieces == null) {
            return null;
        }

        final int i = findFaceIndex(((ActorState) getState()).getFace());
        final int j = mStep % mBitmapColumns;
        return mBitmapPieces[i][j];
    }

    /**
     *
     */
    private boolean updateSteps() {
        final long now = System.currentTimeMillis();
        final boolean isMoving = ((ActorState) getState()).isMoving();
        if (isMoving || (mStep % mBitmapColumns) > 0) {
            // /*缓冲*/final int factTime = isMoving ? FACT_TIME : (int) (FACT_TIME * (1 + mStep * 1.0f / mBitmapColumns));
            final int factTime = FACT_TIME;
            if (now - mLastClipTime > (factTime / mBitmapColumns)) {
                mStep++;
                mLastClipTime = System.currentTimeMillis();
            }
        } else {
            mStep = 0;
        }

        boolean changed = mStep == mLastStep;
        if(changed) {
            mLastStep = mStep;
        }
        return changed;
    }

    /**
     * @param face
     * @return
     */
    public int findFaceIndex(short face) {
        int faceIndex = 0;
        for (int i = 0; i < mFacesOrder.length; i++) {
            if (mFacesOrder[i] == face) {
                faceIndex = i;
                break;
            }
        }
        return faceIndex;
    }

    @Override
    public void moveTo(float x, float y) {

    }

    @Override
    public void moveBy(float x, float y) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        final int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                ((ActorState) getState()).setMoving();
                ((ActorState) getState()).setFace(getFaceTo(motionEvent.getRawX(), motionEvent.getRawY(), getDrawingRect()));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                ((ActorState) getState()).setStopped();
                break;
            default:
        }
        return true;
    }

    /**
     *
     */
    private void updateAllDrawRegions() {
        if (mBitmapPieces != null) {
            mClipRect = getClipRect();
            float halfWidth = mClipRect.width() / 2.0f;
            setDrawingRect(new RectF(getPositionX() - halfWidth, getPositionY() - halfWidth, getPositionX() + mClipRect.width(), getPositionY() + mClipRect.height()));
        }
    }

    @Override
    protected State onCreatCustomState() {
        return new ActorState(this);
    }

    /**
     * @param x
     * @param y
     * @param r
     * @return
     */
    private short getFaceTo(float x, float y, RectF r) {
        short face = ActorState.FACE_A;
        final float qw = r.width() / 4.0f;
        final float qh = r.height() / 4.0f;

        r = new RectF(r.left + qw, r.top + qh, r.right - qw, r.bottom - qh);
        if (!r.contains(x, y)) {
            // 这里可以再细化，暂时先用中心代替
            final boolean verticle = Math.abs(y - r.centerY()) > Math.abs(x - r.centerX());
            if (verticle) {
                face = y > r.centerY() ? ActorState.FACE_S : ActorState.FACE_N;
            } else {
                face = x > r.centerX() ? ActorState.FACE_E : ActorState.FACE_W;
            }
        }
        return face;
    }

    @Override
    public void onStateChanged(State state) {
        //updateAllDrawRegions();
    }

    /**
     *
     */
    public class ActorState extends State {

        /***/
        public static final short MOVING = 0x0000;
        /***/
        public static final short STOPPED = 0x0001;
        /***/
        public static final short FACE_S = 0x0100;
        /***/
        public static final short FACE_W = 0x0200;
        /***/
        public static final short FACE_E = 0x0400;
        /***/
        public static final short FACE_N = 0x0800;
        /***/
        public static final short FACE_A = 0x0a00;
        /***/
        private short action = STOPPED;


        private OnStateChangedListener l;

        /**
         * @param l
         */
        public ActorState(OnStateChangedListener l) {
            this.l = l;
        }

        /**
         * @return
         */
        public boolean isMovingToW() {
            return isMoving() && ((action & FACE_W) == FACE_W);
        }

        /**
         * @return
         */
        public boolean isMovingToN() {
            return isMoving() && ((action & FACE_N) == FACE_N);
        }

        /**
         * @return
         */
        public boolean isMovingToE() {
            return isMoving() && ((action & FACE_E) == FACE_E);
        }

        /**
         * @return
         */
        public boolean isMovingToS() {
            return isMoving() && ((action & FACE_S) == FACE_S);
        }

        /**
         * @return
         */
        public boolean isMoving() {
            return !((action & STOPPED) == STOPPED);
        }

        /**
         * @param dirction
         */
        public void setFace(short dirction) {

            if (dirction != FACE_E && dirction != FACE_W && dirction != FACE_N && dirction != FACE_S) {
                return;
            }

            if (((byte) dirction) > 0) {
                dirction = (short) (dirction >> 8);
                dirction = (short) (dirction << 8);
            }


            if (action > 0x00FF) {
                action = (short) (action << 8);
                action = (short) (action >> 8);
            }

            action |= dirction;

            if (l != null) {
                l.onStateChanged(this);
            }

            // debugging
            if (BuildConfig.DEBUG) {
                printFacing();
            }
        }


        /**
         * @return
         */
        public short getFace() {
            short face = (short) (action >> 8);
            return (short) (face << 8);
        }

        /**
         *
         */
        public void setStopped() {

            if (!isMoving()) {
                return;
            }

            action |= STOPPED;

            if (l != null) {
                l.onStateChanged(this);
            }

            // debugging
            if (BuildConfig.DEBUG) {
                System.out.println("Sprite has stopped !");
            }
        }

        /**
         *
         */
        public void setMoving() {
            if (isMoving()) {
                return;
            }

            action = (short) (action >> 8);
            action = (short) (action << 8);

            if (l != null) {
                l.onStateChanged(this);
            }

            // debugging
            if (BuildConfig.DEBUG) {
                System.out.println("Sprite start moving !");
            }

        }

        private void printFacing() {
            if (isMovingToE()) {
                System.out.println("Sprite is facing to Right!");
            } else if (isMovingToW()) {
                System.out.println("Sprite is facing to Left!");
            } else if (isMovingToN()) {
                System.out.println("Sprite is facing to Top!");
            } else if (isMovingToS()) {
                System.out.println("Sprite is facing to Bottom!");
            } else {
                System.out.println("Sprite is moving [" + isMoving() + "], action now is " + action);
            }
        }

    }

}
