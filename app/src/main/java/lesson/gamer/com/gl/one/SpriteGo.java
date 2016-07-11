package lesson.gamer.com.gl.one;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by daiepngfei on 7/8/16
 */
public class SpriteGo extends FragmentActivity {

    private MSurfaceView mSurfaceView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSurfaceView = new MSurfaceView(this);
        setContentView(mSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.onPause();
    }
}
