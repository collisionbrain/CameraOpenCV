package libre.com.ocrsmart.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import libre.com.ocrsmart.interfaces.ActionCut;
import libre.com.ocrsmart.utils.Utls;

/**
 * Created by hugo on 28/11/16.
 */

public class CropTask extends AsyncTask<Bitmap ,Bitmap ,Bitmap> {

    private int x;
    private int y;
    private int width;
    private int height;
    private ProgressDialog progressDialog;
    private Context context;
    private ActionCut cutImage;
    private Mat srcMat;

    public CropTask(Context context, int x, int y, int width, int height,  ActionCut cutImage){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        this.context=context;
        this.cutImage=cutImage;

        progressDialog=new ProgressDialog(this.context);
        progressDialog.setMessage("Obteniendo Imagen");
        progressDialog.setCancelable(false);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        Bitmap bitmap=params[0];
        Bitmap bitmapFinal = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        this.srcMat=new Mat();
        Utils.bitmapToMat(bitmap,srcMat);
        Rect roi=new Rect(
                x,
                y,
                width ,
                height);
        Mat result=srcMat.submat(roi);
        Utils.matToBitmap(result,bitmapFinal);

        return bitmapFinal;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        progressDialog.dismiss();
        cutImage.setBitmap(bitmap);
    }


}