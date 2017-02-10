package libre.com.ocrsmart.system;

import android.os.Environment;

/**
 * Created by hugo on 17/11/16.
 */

public class Constant {
    public static String DIRNAME = "ocr";
    public static String imageName ="OriginalPicture.jpg";
    public static String dirImage = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DIRNAME + "/"+imageName;
    public static String dirImagePart = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DIRNAME + "/";
    public static String pathHost = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DIRNAME ;
    public static String pathHostTesserAct = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +DIRNAME;
    public static String tesseractFileTrainedData="spa.traineddata";
    public static String tesseractFullPath=pathHostTesserAct+ "/" +tesseractFileTrainedData;
}
