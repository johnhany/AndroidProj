#include <OpencvJni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <string>
#include <vector>
#include <math.h>

#define MAX_CHANNEL 3
#define MAX_CORE_X 11
#define MAX_CORE_Y 11
#define MAX_CORE_LENGTH MAX_CORE_X*MAX_CORE_Y
#define PI 3.1415926
#define E 2.7182818

using namespace cv;
using namespace std;

static int gaussianCore(float core[], int cx, int cy, double lamda)
{
    double lamda2 = 2.0*lamda*lamda;
    double pi1 = 1.0 / PI / lamda2;
    int sq;
    double power;
    double gaus;
    double sum=0.0;

    for(int j=0; j<cy; j++)
    {
        for(int i=0; i<cx; i++)
        {
            sq = (cx/2-i)*(cx/2-i) + (cy/2-j)*(cy/2-j);
            power = -(double)sq / lamda2;
            gaus = pi1*pow(E,power);
            core[j*cx+i] = gaus;
            sum += gaus;
        }
    }

    for(int j=0; j<cy; j++)
    {
        for(int i=0; i<cx; i++)
        {
            core[j*cx+i] /= sum;
        }
    }

    return 1;
}

static int meanCore(float* core,int width, int height)
{
    if(width > MAX_CORE_X || height > MAX_CORE_Y)
        return 0;

    int count = width*height;
    for(int i=0; i<count; i++)
        core[i] = 1.0/count;
    return 1;
}

static int motionBlurCore(float* core, int width, int height, int dir)
{
    if(height != 1 || width%2 != 1)
        return 0;

    int count = width/2+1;
    if(dir == 0)
    {
        for(int i=0; i<count; i++)
            core[i] = 1.0/count;
        for(int i=count; i<width; i++)
            core[i] = 0.0;
    }else{
        for(int i=0; i<count-1; i++)
            core[i] = 0.0;
        for(int i=count-1; i<width; i++)
            core[i] = 1.0/count;
    }
    return 1;
}

static void filter(Mat src, Mat dst, const float core[], int cx, int cy)
{
    int width = src.cols;
    int height = src.rows;
    int channel = src.channels();
    int hasAlpha = channel>3 ? 1 : 0;

    uchar *pout;
    uchar *tmp;

    int line[MAX_CORE_LENGTH]={0};
    int cx2 = cx/2;
    int cy2 = cy/2;

    for(int j=0; j<height; j++) {

        pout = dst.ptr<uchar>(j);

        for(int i=0; i<width; i++) {

            float sum[MAX_CHANNEL]={0};

            for(int y=0; y<cy; y++) {

                for(int x=0; x<cx; x++) {

                    int tx = i+x-cx2;
                    int cp = y*cx+x;

                    for(int c=0; c<channel; c++) {

                    	if(c>2 && hasAlpha)
                    		continue;

                        if(j+y-cy2 < 0) {

                            tmp = src.ptr<uchar>(0);
                            if(tx < 0)
                                sum[c] += tmp[c]*core[cp];
                            else if(tx >= width)
                                sum[c] += tmp[(width-1)*channel+c]*core[cp];
                            else
                                sum[c] += tmp[tx*channel+c]*core[cp];

                        }else if(j+y-cy2 >= height) {

                            tmp = src.ptr<uchar>(height-1);
                            if(tx < 0)
                                sum[c] += tmp[c]*core[cp];
                            else if(tx >= width)
                                sum[c] += tmp[(width-1)*channel+c]*core[cp];
                            else
                                sum[c] += tmp[tx*channel+c]*core[cp];

                        }else {

                            tmp = src.ptr<uchar>(j);
                            int ty = (y-cy2)*width*channel;
                            if(tx < 0)
                                sum[c] += tmp[ty+c]*core[cp];
                            else if(tx >= width)
                                sum[c] += tmp[ty+(width-1)*channel+c]*core[cp];
                            else
                                sum[c] += tmp[ty+tx*channel+c]*core[cp];
                        }
                    }
                }
            }
            if(hasAlpha) {
				for(int r=0; r<channel; r++)
					pout[i*channel+r] = r<3 ? (int)sum[r] : 255;
            }else {
            	for(int r=0; r<channel; r++)
            		pout[i*channel+r] = (int)sum[r];
            }
        }
    }
}

JNIEXPORT jintArray JNICALL Java_net_johnhany_imageprocess_OpencvJni_gray(JNIEnv* env, jclass obj, jintArray buf, jint w, jint h)
{
    jint *cbuf;
    cbuf = env->GetIntArrayElements(buf, (jboolean*) false);
    if(cbuf == NULL) {
        return 0;
    }

    Mat imgData(h, w, CV_8UC4, (unsigned char*)cbuf);

    int grayScale;

	for(int y=0; y<imgData.rows; y++) {
		for(int x=0; x<imgData.cols; x++) {
			grayScale = (int)(imgData.at<Vec4b>(y,x)[0]*0.114 + imgData.at<Vec4b>(y,x)[1]*0.587 + imgData.at<Vec4b>(y,x)[2]*0.299);
			imgData.at<Vec4b>(y,x)[0] = imgData.at<Vec4b>(y,x)[1] = imgData.at<Vec4b>(y,x)[2] = grayScale;
		}
	}

    int size=w * h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, cbuf);
    env->ReleaseIntArrayElements(buf, cbuf, 0);
    return result;
}

JNIEXPORT jintArray JNICALL Java_net_johnhany_imageprocess_OpencvJni_edgeDetect(JNIEnv* env, jclass obj, jintArray buf, jint w, jint h)
{
    jint *cbuf;
    jint *dbuf;
    cbuf = env->GetIntArrayElements(buf, (jboolean*) false);
    if(cbuf == NULL) {
        return 0;
    }

    Mat srcImg(h, w, CV_8UC4, (unsigned char*)cbuf);
    Mat grayImg(h, w, CV_8UC1);
    Mat dstImg(h, w, CV_8UC4);

    int lowThreshold = 50;
    int ratio = 3;

    cvtColor(srcImg, grayImg, CV_RGBA2GRAY);
    blur(grayImg, grayImg, Size(3,3));
    Canny(grayImg, grayImg, lowThreshold, lowThreshold*ratio, 3);
    cvtColor(grayImg, dstImg, CV_GRAY2RGBA);

    dbuf = (jint*)dstImg.ptr(0);
    int size=w * h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, dbuf);
    env->ReleaseIntArrayElements(buf, dbuf, 0);
    return result;
}

JNIEXPORT jintArray JNICALL Java_net_johnhany_imageprocess_OpencvJni_contrastEnhance(JNIEnv* env, jclass obj, jintArray buf, jint w, jint h)
{
    jint *cbuf;
    cbuf = env->GetIntArrayElements(buf, (jboolean*) false);
    if(cbuf == NULL) {
        return 0;
    }

    Mat imgData(h, w, CV_8UC4, (unsigned char*)cbuf);

    int grayScale;
    float alpha=0.8;
    int beta=30;

	for(int y=0; y<imgData.rows; y++) {
		for(int x=0; x<imgData.cols; x++) {
			for(int c=0; c<3; c++) {
				imgData.at<Vec4b>(y,x)[c] = saturate_cast<uchar>(alpha * imgData.at<Vec4b>(y,x)[c] + beta);
			}
		}
	}

    int size=w * h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, cbuf);
    env->ReleaseIntArrayElements(buf, cbuf, 0);
    return result;
}

JNIEXPORT jintArray JNICALL Java_net_johnhany_imageprocess_OpencvJni_gaussianBlur(JNIEnv* env, jclass obj, jintArray buf, jint w, jint h)
{
    jint *cbuf;
    jint *dbuf;
    cbuf = env->GetIntArrayElements(buf, (jboolean*) false);
    if(cbuf == NULL) {
        return 0;
    }

    Mat srcImg(h, w, CV_8UC4, (unsigned char*)cbuf);
	Mat dstImg(h, w, CV_8UC4);

	float core[81];
	int x=9, y=9;
	if(gaussianCore(core, x, y, 1.2)) {
		filter(srcImg,dstImg, core, x, y);
	}

	dbuf = (jint*)dstImg.ptr(0);
	int size=w * h;
	jintArray result = env->NewIntArray(size);
	env->SetIntArrayRegion(result, 0, size, dbuf);
	env->ReleaseIntArrayElements(buf, dbuf, 0);
        return result;
}

JNIEXPORT jintArray JNICALL Java_net_johnhany_imageprocess_OpencvJni_motionBlur(JNIEnv* env, jclass obj, jintArray buf, jint w, jint h)
{
    jint *cbuf;
    jint *dbuf;
    cbuf = env->GetIntArrayElements(buf, (jboolean*) false);
    if(cbuf == NULL) {
        return 0;
    }

    Mat srcImg(h, w, CV_8UC4, (unsigned char*)cbuf);
	Mat dstImg(h, w, CV_8UC4);

	float core[13];
	int x=13, y=1;
	if(motionBlurCore(core, x, y, 0)) {
		filter(srcImg,dstImg, core, x, y);
	}

	dbuf = (jint*)dstImg.ptr(0);
	int size=w * h;
	jintArray result = env->NewIntArray(size);
	env->SetIntArrayRegion(result, 0, size, dbuf);
	env->ReleaseIntArrayElements(buf, dbuf, 0);
        return result;
}

JNIEXPORT jintArray JNICALL Java_net_johnhany_imageprocess_OpencvJni_meanFilter(JNIEnv* env, jclass obj, jintArray buf, jint w, jint h)
{
    jint *cbuf;
    jint *dbuf;
    cbuf = env->GetIntArrayElements(buf, (jboolean*) false);
    if(cbuf == NULL) {
        return 0;
    }

    Mat srcImg(h, w, CV_8UC4, (unsigned char*)cbuf);
	Mat dstImg(h, w, CV_8UC4);

	float core[49];
	int x=7, y=7;
	if(meanCore(core, x, y)) {
		filter(srcImg,dstImg, core, x, y);
	}

	dbuf = (jint*)dstImg.ptr(0);
	int size=w * h;
	jintArray result = env->NewIntArray(size);
	env->SetIntArrayRegion(result, 0, size, dbuf);
	env->ReleaseIntArrayElements(buf, dbuf, 0);
        return result;
}

JNIEXPORT jintArray JNICALL Java_net_johnhany_imageprocess_OpencvJni_touchGray(JNIEnv* env, jclass obj, jintArray buf, jint w, jint h, jfloat cx, jfloat cy)
{
    jint *cbuf;
    cbuf = env->GetIntArrayElements(buf, (jboolean*) false);
    if(cbuf == NULL) {
        return 0;
    }

    float radius = 56.0;
    float dist;
    float grayScale;
    Mat imgData(h, w, CV_8UC4, (unsigned char*)cbuf);

	for(int y=0; y<imgData.rows; y++) {
		for(int x=0; x<imgData.cols; x++) {
			dist = (float)((x-cx)*(x-cx) + (y-cy)*(y-cy));
			if(sqrt(dist) <= radius) {
				grayScale = (int)(imgData.at<Vec4b>(y,x)[0]*0.114 + imgData.at<Vec4b>(y,x)[1]*0.587 + imgData.at<Vec4b>(y,x)[2]*0.299);
				imgData.at<Vec4b>(y,x)[0] = imgData.at<Vec4b>(y,x)[1] = imgData.at<Vec4b>(y,x)[2] = grayScale;
			}
		}
	}

	int size=w * h;
	jintArray result = env->NewIntArray(size);
	env->SetIntArrayRegion(result, 0, size, cbuf);
	env->ReleaseIntArrayElements(buf, cbuf, 0);
	return result;
}
