package lesson.gamer.com.gl.one;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Created by daiepngfei on 7/11/16
 */
class SpriteBitmapPool {

    private Hashtable<String, CountableBitmap> mBitmapTable = new Hashtable<>();
    private Resources mResouces;
    private static SpriteBitmapPool sInstance = new SpriteBitmapPool();
    private DecodeThread mDecodeThread;

    /**
     * @param resources
     * @return
     */
    public static SpriteBitmapPool newInstance(Resources resources) {
        if (sInstance.mResouces == null) {
            sInstance.setResources(resources);
        }
        return sInstance;
    }

    /**
     *
     * @param resId
     */
    public void doReturn(int resId) {
        CountableBitmap bitmap = mBitmapTable.get(makeKeyOfResource(resId));
        if(bitmap != null){
            bitmap.doReturn();
        }
    }


    /**
     *
     */
    private SpriteBitmapPool() {
        mDecodeThread = new DecodeThread();
        mDecodeThread.start();
    }

    /**
     * @param resources
     */
    private void setResources(Resources resources) {
        this.mResouces = resources;
    }

    /**
     * @param resouce
     * @param cCallback
     */
    public void fetch(int resouce, CCallback<Bitmap> cCallback) {
        final String key = makeKeyOfResource(resouce);
        CountableBitmap countableBitmap = mBitmapTable.get(key);
        if (countableBitmap == null || countableBitmap.isInvalid()) {
            decodeBitmap(resouce, cCallback);
        } else if (cCallback != null) {
            cCallback.callback(countableBitmap.doFetch());
        }
    }

    /**
     * @param resouce
     * @return
     */
    private String makeKeyOfResource(int resouce) {
        return String.valueOf(resouce);
    }

    /**
     * @param resouce
     * @param cCallback
     */
    private void decodeBitmap(int resouce, CCallback<Bitmap> cCallback) {
        mDecodeThread.decode(new DecodeSession(resouce, cCallback));
    }

    /**
     *
     */
    private class DecodeSession {
        private int resource;
        private CCallback<Bitmap> cCallback;

        /**
         *
         * @param resource
         * @param bitmapCCallback
         */
        public DecodeSession(int resource, CCallback<Bitmap> bitmapCCallback) {
            this.resource = resource;
            this.cCallback = bitmapCCallback;
        }

        /**
         *
         * @return
         */
        public int getResource() {
            return resource;
        }

        /**
         *
         * @return
         */
        public CCallback<Bitmap> getCCallback() {
            return cCallback;
        }
    }

    /**
     *
     */
    private class DecodeThread extends Thread {

        private LinkedList<DecodeSession> mSessions = new LinkedList<>();
        private boolean isWaiting;
        private final String mWaiter = "waiter";

        @Override
        public void run() {
            while (true) {
                synchronized (mWaiter) {
                    final DecodeSession session = mSessions.poll();
                    if (session == null) {
                        try {
                            this.isWaiting = true;
                            mWaiter.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (!mBitmapTable.containsKey(makeKeyOfResource(session.getResource()))) {
                        final Bitmap bitmap = BitmapFactory.decodeResource(mResouces, session.getResource());
                        if (session.getCCallback() != null) {
                            if (bitmap != null) {
                                session.getCCallback().callback(bitmap);
                            } else {
                                session.getCCallback().reject(new NullPointerException("The bitmap we want decode failed"));
                            }
                        }
                    }
                }

            }
        }

        /**
         *
         * @param session
         */
        public void decode(DecodeSession session) {
            if (session != null) {
                mSessions.add(session);
            }

            synchronized (mWaiter) {
                if (isWaiting) {
                    mWaiter.notifyAll();
                }
            }
        }

    }

    /**
     *
     */
    private class CountableBitmap {
        int count;
        private Bitmap bitmap;
        private String key;

        /**
         * @param key
         * @param bitmap
         */
        public CountableBitmap(String key, Bitmap bitmap) {
            this.bitmap = bitmap;
            this.key = key;
            count++;
        }

        /**
         * @return
         */
        public Bitmap doFetch() {
            count++;
            return bitmap;
        }

        /**
         *
         */
        public void doReturn() {
            count--;
            makeInvalidIfNeedy();
        }

        /**
         * @return
         */
        public boolean isInvalid() {
            makeInvalidIfNeedy();
            return bitmap == null || bitmap.isRecycled();
        }

        /**
         *
         */
        private void makeInvalidIfNeedy() {
            if (count <= 0) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                    mBitmapTable.remove(key);
                }
            }
        }
    }

}
