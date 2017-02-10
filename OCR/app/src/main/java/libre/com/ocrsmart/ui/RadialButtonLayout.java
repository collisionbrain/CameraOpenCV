package libre.com.ocrsmart.ui;

/**
 * Created by hugo on 29/11/16.
 */
import android.animation.Animator;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import libre.com.ocrsmart.R;
import libre.com.ocrsmart.interfaces.ActionItemSetText;
import libre.com.ocrsmart.interfaces.ActionView;
import libre.com.ocrsmart.system.Constant;

/**
 *
 */
public class RadialButtonLayout extends FrameLayout {

    private final static long DURATION_SHORT = 400;
    private WeakReference<Context> weakContext;
    public ActionItemSetText actionSetItem;
    public ActionView actionhideMenu;
    TextView btnMain;
    TextView btnOrange;
    TextView btnYellow;
    TextView btnGreen;
    TextView btnBlue;

    private boolean isOpen = false;
    private Toast toast;
    /**
     * Default constructor
     * @param context
     */
    public RadialButtonLayout(final Context context) {
        this(context, null);
    }

    public RadialButtonLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadialButtonLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        weakContext = new WeakReference<Context>( context );
        LayoutInflater.from(context).inflate( R.layout.layout_radial_buttons, this);
          btnOrange=(TextView) findViewById(R.id.btn_orange);
          btnYellow=(TextView) findViewById(R.id.btn_yellow);
          btnGreen=(TextView) findViewById(R.id.btn_green);
          btnBlue=(TextView) findViewById(R.id.btn_blue);
          btnMain=(TextView) findViewById(R.id.btn_main);
          btnMain.setOnClickListener(openMenuListener);



    }
    public void setClickMenu(){
        btnMain.performClick();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    public  View.OnClickListener openMenuListener=new View.OnClickListener() {

        @Override

        public void onClick(View view) {
            int resId = 0;
            if ( isOpen ) {
                // close
                hide(btnOrange);
                hide(btnYellow);
                hide(btnGreen);
                hide(btnBlue);

                isOpen = false;
                resId = R.string.close;
            } else {

                show(btnOrange, 1, 300);
                show(btnYellow, 2, 300);
                show(btnGreen, 3, 300);
                show(btnBlue, 4, 300);

                isOpen = true;
                resId = R.string.open;
            }

        }
    };



    private final void hide( final View child) {
        child.setOnDragListener(null);
        child.animate()
                .setDuration(DURATION_SHORT)
                .translationX(0)
                .translationY(0)
                .setListener(animatiLIstener)
                .start();
    }

    private final void show(final TextView child, final int position, final int radius) {
        float angleDeg = 180.f;
        int dist = radius;
        switch (position) {
            case 1:
                angleDeg += 0.f;
                break;
            case 2:
                angleDeg += 45.f;
                break;
            case 3:
                angleDeg += 90.f;
                break;
            case 4:
                angleDeg += 135.f;
                break;
            case 5:
                angleDeg += 180.f;
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                break;
        }

        final float angleRad = (float) (angleDeg * Math.PI / 180.f);
        final Float x = dist * (float) Math.cos(angleRad);
        final Float y = dist * (float) Math.sin(angleRad);
        child.setOnDragListener(onDragListener);
        child.animate()
                .setDuration(DURATION_SHORT)
                .translationX(x)
                .translationY(y)
                .setListener(null)
                .start();
    }
    public void setInterfaceMenu(ActionItemSetText actionSetItem){
        this.actionSetItem= actionSetItem;

    }
    public void setInterfaceMenuHide(ActionView actionhideMenu){
        this.actionhideMenu= actionhideMenu;

    }


    private Animator.AnimatorListener animatiLIstener=new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            //if(isOpen)
            Log.e("###########","MENU OPEN :"+isOpen);
                actionhideMenu.action();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);


    }

    private View.OnDragListener onDragListener=new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {

            switch(event.getAction())
            {
                case DragEvent.ACTION_DRAG_STARTED:
                    int x_cord = (int) event.getX();
                    int y_cord = (int) event.getY();

                    break;

                case DragEvent.ACTION_DRAG_ENTERED:

                    x_cord = (int) event.getX();
                    y_cord = (int) event.getY();


                    break;

                case DragEvent.ACTION_DRAG_EXITED :
                    x_cord = (int) event.getX();
                    y_cord = (int) event.getY();
                    break;

                case DragEvent.ACTION_DRAG_LOCATION  :
                    x_cord = (int) event.getX();
                    y_cord = (int) event.getY();

                    break;

                case DragEvent.ACTION_DRAG_ENDED   :
                    //
                    break;

                case DragEvent.ACTION_DROP:
                    TextView textView=(TextView)v;
                    v.getParent();
                    Bitmap analyzedRoi= BitmapFactory.decodeFile(Constant.dirImagePart+"TextArea.jpg");
                    OcrFinder ocrFinder =new OcrFinder(Constant.pathHostTesserAct);
                    String textFounded=new String();
                    textFounded= ocrFinder.getOCRResult(analyzedRoi);
                    textView.setText(textFounded);
                    try{
                        Thread.sleep(1000);

                    }catch(InterruptedException e){ }
                    btnMain.performClick();

                    break;
                default: break;
            }
            return true;
        }
    };

}