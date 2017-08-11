# FloatCircleView
## 防360悬浮小球
### 实现步骤
#### 自定义view在ondraw中进行绘制
```
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
```
#### 用WindowManager的addview方法将控件添加入悬浮窗，设置OnTouchListener监听触摸事件，通过updateViewLayout更新偏移坐标
```
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
```
#### android6.0以上版本需要额外添加权限请求
```
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
```
#### 最终效果如下：
![](https://github.com/Mrgl1203/FloatCircleView/blob/master/floatcircleview-gif.gif)
