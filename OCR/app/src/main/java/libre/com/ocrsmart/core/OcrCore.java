package libre.com.ocrsmart.core;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;

/**
 * Created by hugo on 16/11/16.
 */

public class OcrCore  {
    private Mat imputImageMat;
    private MatOfRect txtArea;

    static{
        System.loadLibrary("opencv_java3");
        System.loadLibrary("ocr_core");



    }

    public OcrCore(){

    }
    public void detectCard(  Mat imputImageMat,    MatOfRect cardArea) {

        getCardArea(imputImageMat.getNativeObjAddr(), cardArea.getNativeObjAddr());

    }

    public void detectText(  Mat imputImageMat,    MatOfRect cardArea) {

        getTextArea(imputImageMat.getNativeObjAddr(), cardArea.getNativeObjAddr());

    }
    public native  long  getCardArea(long inputImage,long outputArea);
    public native  void  getTextArea(long inputImage,long outputArea);
    public native  void  getLetters(long inputImage,long outputArea);

}
