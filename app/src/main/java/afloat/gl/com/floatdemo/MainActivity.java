package afloat.gl.com.floatdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private Intent floatintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void startService(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果版本6.0
            if (!Settings.canDrawOverlays(this)) {//没有允许权限
                //启动activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 100);
            } else {
                startFloatService();
            }
        }

    }

    public void hideFloat(View view) {
        FloatManager.getInstance(this).hideFloatCircleView();
    }

    public void startFloatService() {
        floatintent = new Intent(this, FloatService.class);
        startService(floatintent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                startFloatService();
                break;
        }
    }
}
