package libre.com.ocrsmart;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import libre.com.ocrsmart.core.OcrCore;
import libre.com.ocrsmart.interfaces.Action;
import libre.com.ocrsmart.interfaces.ActionCut;
import libre.com.ocrsmart.interfaces.ActionItemSetText;
import libre.com.ocrsmart.interfaces.ActionItemText;
import libre.com.ocrsmart.interfaces.ActionView;
import libre.com.ocrsmart.system.Constant;
import libre.com.ocrsmart.task.CropTask;
import libre.com.ocrsmart.ui.RadialButtonLayout;
import libre.com.ocrsmart.ui.RegionView;
import libre.com.ocrsmart.utils.Utls;

/**
 * Created by hugo on 17/11/16.
 */

public class OcrActivity extends Activity {
    private String path;
    private Bitmap sourceBitmap,  bitmapFinal;
    private ImageView imgSource;
    private RegionView frameLayoutView;
    private List<Rect> listRegion=new ArrayList<>();
    private  Rect[] textAreaArray;
    private Mat srcMat;
    private int visible;
    private ArrayList itemList;
    private Context context;
    private Rect ItemRect;
    private  int screenHeight;
    private int screenWidth;
    private FrameLayout parentFrame;
    public boolean wasScaled=false;
    public boolean wasCutted=false;
    public int x,y,h,w;
    public float mX,mY;
    private RadialButtonLayout radialButtonLayout;
    public int  visibility;
    public int  midScreenX,midScreenY,areAx,areaAy,areBx,areaBy,areCx,areaCy,areDx,areaDy;
    public int idItem;
    public String textItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.ocr);
        Bundle bundle=getIntent().getExtras();
        x=bundle.getInt("x");
        y=bundle.getInt("y");
        w=bundle.getInt("width");
        h=bundle.getInt("height");
        context=this;
        System.loadLibrary("opencv_java3");
        path= Constant.dirImage;//bundle.getString("path");
        sourceBitmap = BitmapFactory.decodeFile(path);
        imgSource=(ImageView) findViewById(R.id.imgSource);
        parentFrame=(FrameLayout) findViewById(R.id.parentFrame);
        frameLayoutView=(RegionView) findViewById(R.id.regionLayout);
        frameLayoutView.setItemActionMenu(actionItem);
        frameLayoutView.setOnDragListener(onDragListener);
        frameLayoutView.setTextAreaInterface(cutTextAreaInterface);
        radialButtonLayout=(RadialButtonLayout) findViewById(R.id.menuCircle);
        radialButtonLayout.setInterfaceMenu(actionSetItem);
        radialButtonLayout.setInterfaceMenuHide(actionhideMenu);
        imgSource.setImageBitmap(sourceBitmap);
        imgSource.setOnClickListener(listener);
        ViewTreeObserver vto = parentFrame.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                screenHeight= parentFrame.getHeight();
                screenWidth= parentFrame.getWidth();
                midScreenX=screenWidth/2;
                midScreenY=screenHeight/2;
                if(!wasCutted ) {
                    CropTask cropTask = new CropTask(context, x, y, w, h, cutImage);
                    cropTask.execute(sourceBitmap);


                }
            }
        });
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

    }


    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }
    View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            OcrCore textDetector=new OcrCore();
            srcMat=new Mat();
            Bitmap tempBitmap = ((BitmapDrawable) imgSource.getDrawable()).getBitmap();
            Utils.bitmapToMat(tempBitmap, srcMat);
            Mat matGray=new Mat();
            Imgproc.cvtColor(srcMat, matGray, Imgproc.COLOR_RGB2GRAY);
            MatOfRect txtArea =new MatOfRect();
            textDetector.detectText(matGray, txtArea);
            textAreaArray = txtArea.toArray();
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    tempBitmap.getWidth(),
                    tempBitmap.getHeight());
            layoutParams.gravity = Gravity.CENTER;
            frameLayoutView.setLayoutParams(layoutParams);
            frameLayoutView.setRects(textAreaArray);



        }
    };

    public void onDestroy() {

        setResult(RESULT_CANCELED);
        super.onDestroy();

    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {

        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        Bitmap bitmap1=Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        wasScaled=true;
        return bitmap1;
    }

    private ActionCut cutImage=new ActionCut() {
        @Override
        public void setBitmap(Bitmap bitmap) {
            Bitmap bitmapScaled=scaledBitmap(bitmap,screenWidth,screenHeight);
            imgSource.setImageBitmap(bitmapScaled);
            wasCutted=true;
        }
    };
    private Action cutTextAreaInterface=new Action() {
        @Override
        public void action(int x, int y, int width, int height) {

            Rect roiRect=new  Rect(
                    x,
                    y,
                    width ,
                    height);
            Mat result=srcMat.submat(roiRect);
            Imgcodecs.imwrite(Constant.dirImagePart + "TextArea.jpg", result);

        }
    };


    private ActionItemSetText actionSetItem=new ActionItemSetText(){
        @Override
        public void setMenuItemText(int id,String text) {
            idItem=id;
            textItem=text;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView textView=(TextView) radialButtonLayout.findViewById(idItem);
                    Log.e("TEXT VIEW LABEL: ",""+textView.getText().toString());
                    Log.e("TEXT FOUNDED: ",""+textItem);
                    textView.setText(textItem);
                    radialButtonLayout.setClickMenu();
                }
            });

        }

    };

    private ActionView actionhideMenu=new ActionView(){
        @Override
        public void action(){
            radialButtonLayout.setVisibility(View.INVISIBLE);
        }

    };
    private ActionItemText actionItem=new ActionItemText(){
        @Override
        public void setMenuVisibility(int visibility,float x,float y) {
            setMenu(visibility,x,y);
        }

    };
    private void setMenu(int visibilitXY,float x,float y){
         this.visibility=visibilitXY;
        this.mX=x;
        this.mY=y;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                radialButtonLayout.setX(mX);
                radialButtonLayout.setY(mY);
                radialButtonLayout.setVisibility(visibility);
                radialButtonLayout.setClickMenu();
            }
        });
    }
    //DRAG LISTENER FOR PRINCIPAL LAYOUT
    private View.OnDragListener onDragListener=new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {

            switch(event.getAction())
            {


                case DragEvent.ACTION_DROP:
                    radialButtonLayout.setVisibility(View.INVISIBLE);
                    radialButtonLayout.setClickMenu();
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    String stDta=item.getText().toString();
                    Rect rect= Utls.StringTorRect(stDta);
                    frameLayoutView.restoreViewDraged(rect);
                    break;

                default: break;
            }
            return true;
        }
    };
}
