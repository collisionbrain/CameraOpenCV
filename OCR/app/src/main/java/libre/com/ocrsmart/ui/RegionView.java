package libre.com.ocrsmart.ui;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.List;

import libre.com.ocrsmart.R;
import libre.com.ocrsmart.interfaces.Action;
import libre.com.ocrsmart.interfaces.ActionItemText;
import libre.com.ocrsmart.interfaces.ActionView;
import libre.com.ocrsmart.pojos.RoiObjetc;
import libre.com.ocrsmart.utils.Utls;

/**
 * Created by ProBook on 06/10/2016.
 */
public class RegionView extends FrameLayout   {

    protected Context context;
    private Paint paint;
    private RegionView polygonView;
    private Rect[] listRect;
    private List<Rect> rectListUsed=new ArrayList<>();
    private boolean seted=false;
    private Action actionGetROI;
    private ActionView actionView;
    private final RoiObjetc roiObjetc=new RoiObjetc();
    private ActionItemText actionItem;
    private Action cutTextAreaInterface;


    public RegionView(Context context) {
        super(context);
        this.context = context;
    }

    public RegionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public RegionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }
    public void setItemActionMenu(ActionItemText actionItem) {
        this.actionItem = actionItem;
    }
    /*public void setAction( Action actionGetROI) {
       this.actionGetROI = actionGetROI;
    }
    public void setActionView(ActionView actionView) {
        this.actionView = actionView;
    }
    private void init() {
        polygonView = this;
    }
*/
    @Override
    protected void attachViewToParent(View child, int index, ViewGroup.LayoutParams params) {
        super.attachViewToParent(child, index, params);
    }

    private void initPaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
    }


    public void setTextAreaInterface(Action cutTextAreaInterface) {
        this.cutTextAreaInterface=cutTextAreaInterface;

    }
    public void setRects(Rect[] listRect) {
        this.listRect=listRect;
        initPaint();

    }
   public int getSizeRectList() {

        return rectListUsed.size();

    }
    public void restoreViewDraged(Rect rect) {
        ImageView imageView = imageViewSelector(rect.x, rect.y,rect.height , rect.width );
        addView(imageView);


    }

    public void removeViewDraged(Rect rect) {
        ImageView imageView = imageViewSelector(rect.x, rect.y, rect.width, rect.height);
        removeView(imageView);



    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(listRect!=null) {
            if (!seted) {
                for (int x = 0; x <= listRect.length - 2; x++) {
                    Rect rect = listRect[x];
                    Point px = new Point();
                    px.x = rect.x ;
                    px.y = rect.y;
                    ImageView imageView = imageViewSelector(rect.x, rect.y, rect.width + 5, rect.height + 5);

                    addView(imageView);


                }
                seted = true;
            }
        }

    }

    private ImageView imageViewSelector(int x, int y, int w, int h ) {
        ImageView imageView = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.height=h;
        layoutParams.width=w;
        imageView.setLayoutParams(layoutParams);
        imageView.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_round_corners));
        imageView.setX(x);
        imageView.setY(y);
        imageView.setAlpha(125);
        imageView.setOnDragListener(onDragListener);
        imageView.setOnTouchListener(onTouchListener);
        return imageView;
    }
    private View.OnDragListener onDragListener=new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {

            switch(event.getAction())
            {


                case DragEvent.ACTION_DROP:
                    ImageView view =  (ImageView) event.getLocalState();
                    int targetWidth=view.getWidth();
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String stDta=item.getText().toString();
                    Rect rect= Utls.StringTorRect(stDta);
                    int dragedX=rect.x;
                    int dragedWidth=rect.width;
                    ImageView imageView = imageViewSelector(dragedX, rect.y, dragedWidth+targetWidth, rect.height);
                    addView(imageView);
                    removeView(v);
                    actionItem.setMenuVisibility(View.INVISIBLE,0,0);


                    break;

                default: break;
            }
            return true;
        }
    };

    private OnTouchListener onTouchListener=new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    float x=v.getX();
                    float y=v.getY();
                    actionItem.setMenuVisibility(View.VISIBLE,x,y);

                    cutTextAreaInterface.action((int)x, (int)y,  v.getWidth(),v.getHeight());
                    String dataObject= x+"," +y+"," +v.getWidth()+"," + v.getHeight();
                    ClipData data = ClipData.newPlainText("Item", dataObject);
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDrag(data, shadowBuilder, v, 0);
                    v.setVisibility(View.INVISIBLE);
                    break;
                case MotionEvent.ACTION_UP:

                    break;
            }
            return false;
        }
    };


}