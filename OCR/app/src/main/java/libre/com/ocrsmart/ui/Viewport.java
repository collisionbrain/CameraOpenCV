package libre.com.ocrsmart.ui;

/**
 * Created by ProBook on 09/11/2016.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import org.opencv.core.Rect;

public class Viewport extends ViewGroup {
    private float height=0;
    Paint stroke;
    public int color;
    private boolean detected;
    private  Rect[] cardDetected;
    private  RectF rectCard;

    public Viewport(Context context) {
        super(context);
    }

    public Viewport(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Viewport(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setHeight(float height){
        this.height=height;
    }
    public void setCardAlert(boolean detected ,Rect[] cardDetected){
        this.detected=detected;
        this.cardDetected=cardDetected;
        Log.e("###########",""+detected);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int viewportMargin = 60;
        int viewportCornerRadius = 30;
        Paint eraser = new Paint();
        eraser.setAntiAlias(true);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        float width = (float) getWidth() - viewportMargin;
        float height = (float)getHeight()- viewportMargin ;
        RectF rect = new RectF((float)viewportMargin, (float)viewportMargin, width, height);
        RectF frame = new RectF((float)viewportMargin, (float)viewportMargin, width, height);
        Path path = new Path();

        path.addRoundRect(frame, (float) viewportCornerRadius, (float) viewportCornerRadius, Path.Direction.CW);
        if(detected) {
            stroke = new Paint();
            stroke.setAntiAlias(true);
            color=Color.GREEN;
            stroke.setColor(color);
            stroke.setStrokeWidth(3);
            stroke.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, stroke);



        }else{
            stroke = new Paint();
            stroke.setAntiAlias(true);
            color=Color.WHITE;
            stroke.setColor(color);
            stroke.setStrokeWidth(3);
            stroke.setStyle(Paint.Style.STROKE);
            canvas.drawPath(path, stroke);
        }
        canvas.drawRoundRect(rect, (float) viewportCornerRadius, (float) viewportCornerRadius, eraser);
        if(detected) {
            float centerRectX=width/2;
            float centerRectY=height/2;
            float longitude=cardDetected[0].height;
            canvas.drawLine(centerRectX,centerRectY,centerRectX+longitude,centerRectY,stroke);
            canvas.drawLine(centerRectX,centerRectY,centerRectX,centerRectY+longitude,stroke);
            canvas.drawLine(centerRectX,centerRectY,centerRectX,centerRectY-longitude,stroke);
            canvas.drawLine(centerRectX,centerRectY,centerRectX-longitude,centerRectY,stroke);

        }
        this.invalidate();
    }

}