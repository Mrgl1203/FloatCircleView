package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import afloat.gl.com.floatdemo.CommonUtil;
import afloat.gl.com.floatdemo.R;

/**
 * Created by gl152 on 2017/8/9.
 */

public class FloatCircleView extends View {
    public int width;
    public int heiight;
    private Paint circlePaint;
    private Paint textPaint;
    private String text = "50%";
    private Context context;
    private boolean isDrag = false;
    private Bitmap dragsrc;
    private Paint progressPaint;
    private Canvas bitmapCanvas;
    private Bitmap transparentBitmap;
    private Path path;
    private int progress = 100;
    private int max = 100;
    private int currentProgress = 0;

    Handler handler = new Handler();

    // 通过new方法构造
    public FloatCircleView(Context context) {
        super(context);
        initPaints(context);
    }

    //运用在xml文件里,有自定义属性
    public FloatCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaints(context);
    }

    //运用在xml文件里,有style样式
    public FloatCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints(context);
    }

    private void initPaints(Context context) {
        this.context = context;
        circlePaint = new Paint();
        circlePaint.setColor(getResources().getColor(R.color.colorCircle));
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setDither(true);
        progressPaint.setColor(getResources().getColor(R.color.colorProgress));
        progressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//显示重叠部分

        path = new Path();

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(25);
        textPaint.setFakeBoldText(true);//文字加粗

        width = CommonUtil.dip2px(context, 60);
        heiight = CommonUtil.dip2px(context, 60);

        //创建底层的bitmapt透明画布
        transparentBitmap = Bitmap.createBitmap(width, heiight, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(transparentBitmap);

        Bitmap src = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_diamonds_6);
        dragsrc = Bitmap.createScaledBitmap(src, width, heiight, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isDrag) {//在拖动将样式更改
            canvas.drawBitmap(dragsrc, 0, 0, null);
        } else {
            bitmapCanvas.drawCircle(width / 2, heiight / 2, CommonUtil.dip2px(context, 30), circlePaint);
            path.reset();//重置
            float progressy = (1 - (float) currentProgress / max) * heiight;//根据progress算出当前进度波浪的宽度
            path.moveTo(width, progressy);//定位圆内波浪右端点
            path.lineTo(width, heiight);//连线到右下角
            path.lineTo(0, heiight);//连线到左下角
            path.lineTo(0, progressy);//圆内波浪坐端点
            //波浪采用贝塞尔曲线完成
            float d = (1 - ((float) currentProgress / progress)) * 10;//接近指定进度时的波幅
            for (int i = 0; i < width / 40; i++) {
                path.rQuadTo(10, d, 20, 0);//rQuadTo接着上一个path的笔画继续绘制,这里一段波浪的长度为40，根据控件宽度算出次数绘制波浪效果
                path.rQuadTo(10, d, 20, 0);
            }
            path.close();
            bitmapCanvas.drawPath(path, progressPaint);
            text = (int) (((float) currentProgress / max) * 100) + "%";
            float textwidth = textPaint.measureText(text);//测量文本长度
            float x = width / 2 - textwidth / 2;
            Paint.FontMetrics metrics = textPaint.getFontMetrics();//获取字体规格
            float dy = -(metrics.descent + metrics.ascent) / 2;//descent+asecnt相加就是文字的高度
            float y = heiight / 2 + dy;
            bitmapCanvas.drawText(text, x, y, textPaint);//文本绘制在圆的中心
            canvas.drawBitmap(transparentBitmap, 0, 0, null);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, heiight);
    }

    public void isDragging(boolean flag) {
        isDrag = flag;
        invalidate();//必须要刷新控件
    }

    public void startClickAnimation() {
        currentProgress = 0;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentProgress++;
                if (currentProgress <=progress) {
                    invalidate();
                    handler.postDelayed(this, 50);
                } else {
                    handler.removeCallbacks(this);
                    currentProgress=progress;
                }
            }
        }, 50);
    }

}
