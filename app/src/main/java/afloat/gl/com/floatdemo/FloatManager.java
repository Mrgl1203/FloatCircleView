package afloat.gl.com.floatdemo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import view.FloatCircleView;

/**
 * Created by gl152 on 2017/8/9.
 */

public class FloatManager {
    private static FloatManager instance;
    private Context context;
    private WindowManager wm;
    private FloatCircleView floatCircleView;
    private float startx;
    private float starty;
    private WindowManager.LayoutParams lp;
    private float x0;
    private float y0;
    private boolean isShowing = false;

    private FloatManager(final Context context) {
        this.context = context;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        floatCircleView = new FloatCircleView(context);
        floatCircleView.setOnTouchListener(floatviewTouchListener);
        floatCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击之后隐藏悬浮  弹出对话框
                floatCircleView.startClickAnimation();
            }
        });
    }

    public static FloatManager getInstance(Context context) {
        if (instance == null) {
            synchronized (FloatManager.class) {
                if (instance == null) {
                    instance = new FloatManager(context);
                }
            }
        }
        return instance;
    }

    //展示浮窗小球
    public void createFloatCircleView() {
        isShowing = true;
        lp = new WindowManager.LayoutParams();
        lp.width = floatCircleView.width;
        lp.height = floatCircleView.heiight;
        lp.gravity = Gravity.TOP | Gravity.LEFT;
        lp.x = 0;
        lp.y = 0;
        lp.type = WindowManager.LayoutParams.TYPE_PHONE;//设置成像来电一样悬浮在其他应用上面
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;//不获取其他应用的焦点和触摸
        lp.format = PixelFormat.RGBA_8888;//设置背景为透明
        wm.addView(floatCircleView, lp);
    }

    public void hideFloatCircleView() {
        isShowing = false;
        floatCircleView.setVisibility(View.GONE);
    }
    public void showFloatCircleView(){
        isShowing = true;
        floatCircleView.setVisibility(View.VISIBLE);
    }


    public boolean isFloatShowing() {
        return isShowing;
    }

    //点击事件和触摸事件会同时触发，需要对用户滑动做判断将时间消费掉
    private View.OnTouchListener floatviewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    floatCircleView.isDragging(false);
                    startx = motionEvent.getRawX();
                    starty = motionEvent.getRawY();

                    x0 = motionEvent.getRawX();//记录按下的初始位置
                    y0 = motionEvent.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = motionEvent.getRawX();
                    float y = motionEvent.getRawY();
                    float dx = x - startx;
                    float dy = y - starty;
                    if (Math.abs(dx) > 6 || Math.abs(dy) > 6) {
                        floatCircleView.isDragging(true);
                    }
                    lp.x += dx;
                    lp.y += dy;
                    wm.updateViewLayout(floatCircleView, lp);//更新悬浮位置
                    startx = x;
                    starty = y;
                    break;
                case MotionEvent.ACTION_UP:
                    float x1 = motionEvent.getRawX();
                    float y1 = motionEvent.getRawY();
                    floatCircleView.isDragging(false);

                    if (Math.abs(x1 - x0) > 6 || Math.abs(y1 - y0) > 6) {//如果发生移动就将时间消费掉，不传递给click
                        return true;
                    } else {
                        return false;
                    }

                default:
                    break;
            }
            return false;
        }
    };
}
