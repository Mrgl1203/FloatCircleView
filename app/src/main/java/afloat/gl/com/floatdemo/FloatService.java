package afloat.gl.com.floatdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by gl152 on 2017/8/9.
 */

public class FloatService extends Service {
    private FloatManager floatManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        floatManager = FloatManager.getInstance(this);
        floatManager.createFloatCircleView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!floatManager.isFloatShowing()) {
            floatManager.showFloatCircleView();
        }
        return super.onStartCommand(intent, flags, startId);
    }


}
