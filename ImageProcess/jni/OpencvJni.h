#include <jni.h>

#ifndef _Included_net_johnhany_imageprocess_OpencvJni
#define _Included_net_johnhany_imageprocess_OpencvJni
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jintArray JNICALL Java_net_johnhany_imageprocess_OpencvJni_gray
  (JNIEnv *, jclass, jintArray, jint, jint);

JNIEXPORT jintArray JNICALL Java_net_johnhany_imageprocess_OpencvJni_edgeDetect
  (JNIEnv *, jclass, jintArray, jint, jint);

JNIEXPORT jintArray JNICALL Java_net_johnhany_imageprocess_OpencvJni_contrastEnhance
  (JNIEnv *, jclass, jintArray, jint, jint);

JNIEXPORT jintArray JNICALL Java_net_johnhany_imageprocess_OpencvJni_gaussianBlur
  (JNIEnv *, jclass, jintArray, jint, jint);

JNIEXPORT jintArray JNICALL Java_net_johnhany_imageprocess_OpencvJni_motionBlur
  (JNIEnv *, jclass, jintArray, jint, jint);

JNIEXPORT jintArray JNICALL Java_net_johnhany_imageprocess_OpencvJni_meanFilter
  (JNIEnv *, jclass, jintArray, jint, jint);

JNIEXPORT jintArray JNICALL Java_net_johnhany_imageprocess_OpencvJni_touchGray
  (JNIEnv *, jclass, jintArray, jint, jint, jfloat, jfloat);

#ifdef __cplusplus
}
#endif
#endif
