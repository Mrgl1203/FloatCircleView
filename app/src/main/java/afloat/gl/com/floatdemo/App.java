package afloat.gl.com.floatdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by gl152 on 2017/8/10.
 */

public class App extends Application {
    private   Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
    }

    public  Context getContext() {
        return context;
    }
}
