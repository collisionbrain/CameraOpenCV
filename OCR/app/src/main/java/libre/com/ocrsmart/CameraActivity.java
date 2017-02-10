package libre.com.ocrsmart;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import libre.com.ocrsmart.core.OcrCore;
import libre.com.ocrsmart.pojos.RotationDegree;
import libre.com.ocrsmart.system.Constant;
import libre.com.ocrsmart.ui.Viewport;
import libre.com.ocrsmart.utils.Utls;

/**
 * Created by hugo on 16/11/16.
 */

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar CARD_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        OCR_ACTIVITY       = 101;
    private Display display;
    private Mat mRgba;
    private Mat                    mGray;
    private RelativeLayout rlMain;
    private CameraBridgeViewBase mOpenCvCameraView;
    private int  screenHeigth,screenWidth;
    private Viewport viewport;
    public  boolean detected=false;
    public ImageButton btnCamera,btnClose,btnGallery;
    public OcrCore core=new OcrCore();
    Rect[] cardDetected;
    MatOfRect card;
    public Rect rectCardRoi;
    public Mat output;
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");



                    mOpenCvCameraView.enableView();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        Utls.creatDirectories(Constant.pathHost);
        Utls.creatDirectories(Constant.pathHostTesserAct);
        Utls.prepareFiles(getAssets());
        rlMain= (RelativeLayout) findViewById(R.id.rlMain);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_surface_view);
        btnCamera= (ImageButton) findViewById(R.id.camera);
        btnCamera.setOnClickListener(listenerCamera) ;
        btnClose= (ImageButton) findViewById(R.id.close);
        btnClose.setOnClickListener(listenerClose);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        ViewTreeObserver vto = mOpenCvCameraView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewport = (Viewport) findViewById(R.id.viewport);
                viewport.setHeight(rlMain.getHeight());
                display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

                screenHeigth=display.getHeight();
                screenWidth=display.getWidth();
                int rotation=display.getRotation();
                int degrees=0;
                int orientation=getResources().getConfiguration().orientation;
                if( orientation== Configuration.ORIENTATION_LANDSCAPE){
                    if(rotation== Surface.ROTATION_270){
                        degrees=180;
                    }else if(rotation== Surface.ROTATION_180){
                        degrees=180;
                    }
                }else if(orientation== Configuration.ORIENTATION_PORTRAIT){
                    if(rotation== Surface.ROTATION_270){
                        degrees=90;
                    }else if(rotation== Surface.ROTATION_90){
                        degrees=270;
                    }else if(rotation== Surface.ROTATION_180){
                        degrees=180;
                    }else if(rotation== Surface.ROTATION_0){
                        degrees=90;
                    }
                }

                mOpenCvCameraView.setRotationDegrees(degrees);
                mOpenCvCameraView.setScreenSize(screenHeigth,screenWidth);

            }
        });

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
       // mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
       // mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        card = new MatOfRect();
        core.detectCard(mRgba,card);
        cardDetected = card.toArray();
        if(cardDetected[0].x>0 && cardDetected[0].y>0){
            detected=true;


            Log.e("############3"," CUADRO : "+cardDetected.length);
            Log.e("############3"," CUADRO X : "+cardDetected[0].x);
            Log.e("############3"," CUADRO Y : "+cardDetected[0].y);
            Log.e("############3"," CUADRO height : "+cardDetected[0].height);
            Log.e("############3"," CUADRO width  : "+cardDetected[0].width);

        }else{
            detected=false;

        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewport.setCardAlert(detected,cardDetected);
            }
        });

        rectCardRoi =new Rect(cardDetected[0].x*4,
                cardDetected[0].y*4,
                cardDetected[0].width*4,
                cardDetected[0].height*4);
       output=mRgba.clone();
       Imgproc.rectangle(mRgba, rectCardRoi.tl(), rectCardRoi.br(),new Scalar(100, 100, 200), 2);

        return mRgba;
    }


    View.OnClickListener listenerClose=new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            finish();
        }

    };
    View.OnClickListener listenerCamera=new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            Bitmap bmpPicture= Bitmap.createBitmap(output.width(),output.height(),Bitmap.Config.RGB_565);
            Utils.matToBitmap(output,bmpPicture);
             try {
                FileOutputStream fos = new FileOutputStream(Constant.dirImage);
                bmpPicture.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                bmpPicture.recycle();
                startOCR();

            } catch (FileNotFoundException e) {
                Log.e("#############3","FileNotFoundException");

            } catch (IOException e) {
                Log.e("#############3","IOException");
            }
        }

    };
    private void startOCR(){

        Intent processActivityIntent=new Intent(this,OcrActivity.class);

        processActivityIntent.putExtra("x",cardDetected[0].x*4);
        processActivityIntent.putExtra("y",cardDetected[0].y*4);
        processActivityIntent.putExtra("width",cardDetected[0].width*4);
        processActivityIntent.putExtra("height",cardDetected[0].height*4);
        startActivity(processActivityIntent);


    }

    View.OnClickListener listenerGallery=new View.OnClickListener(){

        @Override
        public void onClick(View v) {

        }

    };

}