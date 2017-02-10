package libre.com.ocrsmart.utils;

import android.content.res.AssetManager;

import org.opencv.core.Rect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import libre.com.ocrsmart.system.Constant;

/**
 * Created by hugo on 17/11/16.
 */

public class Utls {
    public static  Rect StringTorRect(String data){
        String []itemsDataString=data.split(",");
        float x =Float.parseFloat(itemsDataString[0]);
        float y=Float.parseFloat(itemsDataString[1]);
        float width=Float.parseFloat(itemsDataString[2]);
        float height=Float.parseFloat(itemsDataString[3]);
        return new Rect((int)x,(int)y,(int)width,(int)height);
    }
    public static boolean checkPaths(String path) {
        File carpetaImg = new File(path);

        if (carpetaImg.exists()) {
            if (carpetaImg.delete()) {
                carpetaImg.mkdirs();
            }

        } else {
            carpetaImg.mkdirs();
        }


        return carpetaImg.exists();
    }
    public static String prepareFiles(AssetManager assetManager){
        String folderName= Constant.pathHostTesserAct  ;
        String fileName=Constant.tesseractFileTrainedData;
        boolean fileExist= checkPaths(folderName);
        if(fileExist) {
            try {

                InputStream in = assetManager.open(fileName);
                OutputStream out = new FileOutputStream(folderName + "/" + fileName);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();



            } catch (IOException e) {
            }
        }
        return folderName ;
    }

    public static void creatDirectories( String path) {
        File carpetaImg = new File(path);
        boolean carpetaExistente = true;

        if (!carpetaImg.exists()) {
            if (carpetaImg.delete()) {
            }
            carpetaExistente = carpetaImg.mkdir();
        } else {
            carpetaExistente = carpetaImg.mkdir();
        }


    }
}
