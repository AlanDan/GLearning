package lesson.gamer.com.gl.one;

import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by daiepngfei on 7/11/16
 */
public interface Dirtyable {
    /**
     *
     * @param dirty
     */
    boolean drawDirty(Canvas canvas, RectF dirty, long refreshTime, boolean force);
}
