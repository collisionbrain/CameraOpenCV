//
// Created by collisionbrain on 29/09/2016.
//

#include "OcrCore.h"
#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <string>
#include <android/log.h>
#define LOG_TAG "CcrCore"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
using namespace std;
using namespace cv;

double angle(Point pt1, Point pt2, Point pt0)
{
    double dx1 = pt1.x - pt0.x;
    double dy1 = pt1.y - pt0.y;
    double dx2 = pt2.x - pt0.x;
    double dy2 = pt2.y - pt0.y;
    return (dx1*dx2 + dy1*dy2)/sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
}

void findLargestSquare(const vector< vector<Point> >& squares,
                       vector<Point>& biggest_square)
{
    if (!squares.size())
    {

        return;
    }

    int max_width = 0;
    int max_height = 0;
    int max_square_idx = 0;
    for (size_t i = 0; i < squares.size(); i++)
    {
        Rect rectangle = boundingRect(Mat(squares[i]));
        if ((rectangle.width >= max_width) && (rectangle.height >= max_height))
        {
            max_width = rectangle.width;
            max_height = rectangle.height;
            max_square_idx = i;
        }
    }

    biggest_square = squares[max_square_idx];
}
vector<Rect> getCardArea(Mat image){

    vector<vector<Point> > squares;
    vector<Point> largest_square;
    vector<Rect> rectFounded;
    int newHeigth= image.cols/4;
	int newWidth= image.rows/4;
	Size size(newHeigth,newWidth);
	Mat resized;
	resize(image,resized,size);

	Mat src_gray,filtered;
	cvtColor(resized, src_gray, CV_BGR2GRAY);
    blur(src_gray, filtered, Size(3, 3));
    Mat edges;
    int thresh = 128;
    Canny(filtered, edges, 10,100, 3);
    Mat dilated_edges;
    dilate(edges, dilated_edges,Mat(),  Point(-1, -1), 2, 1, 1);
    vector< vector<Point> > contours;
    findContours(dilated_edges, contours,RETR_LIST, CHAIN_APPROX_SIMPLE);
    vector<Point> approx;
    for (size_t i = 0; i < contours.size(); i++)
        {
            approxPolyDP(Mat(contours[i]), approx,arcLength(Mat(contours[i]), true)*0.02, true);
            if (approx.size() == 4 && fabs(contourArea(Mat(approx))) > 1000 &&
                isContourConvex(Mat(approx)))
            {
                double maxCosine = 0;
                for (int j = 2; j < 5; j++)
                {
                    double cosine =  fabs(angle(approx[j%4], approx[j-2], approx[j-1]));
                    maxCosine = MAX(maxCosine, cosine);
                }

                if (maxCosine < 0.3)
                    squares.push_back(approx);
            }
        }
         findLargestSquare(squares, largest_square);
         Rect rectangle = boundingRect(Mat(largest_square));
         rectFounded.push_back(rectangle);
         return  rectFounded;
}



vector<Rect> getLettersArea(Mat word){
    Mat  pMatSmall;
    vector<Rect> roiFounded;
    Mat pMatGray=word.clone();
    Size size(3,3);
    GaussianBlur(pMatGray,pMatGray,size,0);
    adaptiveThreshold(pMatGray,pMatGray,255,ADAPTIVE_THRESH_GAUSSIAN_C,  THRESH_BINARY,11,2);
    Mat pMatBw;

    vector < vector<Point> > contours;
    vector <Vec4i> hierarchy;
    findContours(pMatGray, contours, hierarchy, CV_RETR_LIST, CHAIN_APPROX_SIMPLE, Point(0, 0) );
    for(int i= 0; i < contours.size(); i++)
    {
        if (contourArea(contours[i]) > 50 ){
            Rect rect = boundingRect(contours[i]);
            int midWidth=pMatBw.cols/2;
            int midHeigth=pMatBw.rows/2;
            int z=pMatBw.rows/4;
            int zx=pMatBw.rows-z;
            int h=pMatBw.rows;
            if (rect.height >=zx  && rect.width < midWidth ){
                Rect roi= Rect(
                        Point(rect.x, rect.y),
                        Point(rect.x + rect.width, rect.y +  (h-rect.y )));
                roiFounded.push_back(roi);
            }

        }


    }

    return roiFounded;
}

vector<Rect>  getTextArea (Mat img)
{

    LOGD("CLONING MAT ");
    Mat pMatLarge=img.clone();
    LOGD("FINISH CLONE");
    Mat  pMatRgb;
    Mat  pMatSmall;
    vector<Rect> rectTextFounded;
    LOGD("INSIDE FUNCTION");
    try{
        Mat pMatGrd;
        LOGD("START MORPH_ELLIPSE");
        Mat morphKernel = getStructuringElement(MORPH_ELLIPSE, Size(3, 3));
        LOGD("FINISH MORPH_ELLIPSE");
        LOGD("INSIDE FUNCTION : morphologyEx");
        morphologyEx(pMatLarge, pMatGrd, MORPH_GRADIENT, morphKernel);
        LOGD("INSIDE FUNCTION : morphologyEx");
        Mat pMatBw;
        threshold(pMatGrd, pMatBw,0.0, 255.0, THRESH_BINARY | THRESH_OTSU);

        Mat pMatConn;
        morphKernel = getStructuringElement(MORPH_RECT, Size(9, 1));
        morphologyEx(pMatBw, pMatConn, MORPH_CLOSE, morphKernel );
        Mat mask = Mat::zeros(pMatBw.size(), CV_8UC1);
        vector < vector<Point> > contours;
        vector <Vec4i> hierarchy;
        findContours(pMatConn, contours, hierarchy, CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, Point(0, 0) );
        for( int idx = 0; idx >= 0; idx = hierarchy[idx][0] )
        {
            Rect rect = boundingRect(contours[idx]);
            Mat maskROI(mask, rect);
            maskROI = Scalar(0, 0, 0);
            drawContours(mask, contours, idx, Scalar(255, 255, 255), CV_FILLED);
            double r = (double)countNonZero(maskROI)/(rect.width*rect.height);
            if (r > .45  && (rect.height > 8 && rect.width > 8) )
            {
                rectTextFounded.push_back(rect);

            }

        }
         return rectTextFounded;
        }catch(Exception& exception){
        LOGD("===================> EXCEPTION");
        }
}
JNIEXPORT void JNICALL Java_libre_com_ocrsmart_core_OcrCore_getCardArea
        (JNIEnv *, jclass,jlong imageMat, jlong squareMat)
{
    LOGD("STARTING PROCESS ");
    Mat& imgMat=*((Mat*)imageMat);
    LOGD("GET CARD AREA");
   vector<Rect>  founded = getCardArea(imgMat);
   *((Mat*)squareMat) = Mat(founded, true);

}
JNIEXPORT void JNICALL Java_libre_com_ocrsmart_core_OcrCore_getTextArea
        (JNIEnv *, jclass,jlong wordMat, jlong letterMat)
{
    LOGD("STARTING PROCESS");
    Mat& imgMat=*((Mat*)wordMat);
    LOGD("GET TEXT AREA");
    vector<Rect> letteres = getTextArea(imgMat);
    *((Mat*)letterMat) = Mat(letteres, true);


}
JNIEXPORT void JNICALL Java_libre_com_ocrsmart_core_OcrCore_getLetters
        (JNIEnv *, jclass,jlong wordMat, jlong letterMat)
{
    LOGD("STARTING PROCESS");
    Mat& imgMat=*((Mat*)wordMat);
    LOGD("GET TEXT BY LETTER");
    vector<Rect> letteres = getLettersArea(imgMat);
    *((Mat*)letterMat) = Mat(letteres, true);


}
