package lesson.gamer.com.gl.one;

import android.graphics.RectF;

/**
 * Created by daiepngfei on 7/11/16
 */
public interface DirtyDrawer {

    /**
     *
     * @param rectF
     */
    void requestDrawDirty(RectF rectF);
}
