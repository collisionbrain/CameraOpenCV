package libre.com.ocrsmart.ui;

/**
 * Created by hugo on 5/12/16.
 */

import android.graphics.Bitmap;
import android.util.Log;
import com.googlecode.tesseract.android.TessBaseAPI;

public class OcrFinder {
    private TessBaseAPI mTess;


    public OcrFinder(String folderName) {
        // TODO Auto-generated constructor stub
        mTess = new TessBaseAPI();
        String language = "spa";
        mTess.init(folderName, language);
    }

    public String getOCRResult(Bitmap bitmap) {

        mTess.setImage(bitmap);
        String result = mTess.getUTF8Text();
        return result;
    }



    public void onDestroy() {
        if (mTess != null)
            mTess.end();
    }


}