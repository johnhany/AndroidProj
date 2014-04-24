LOCAL_PATH := $(call my-dir)  
include $(CLEAR_VARS)  
include D:\Digital-Working\Android\OpenCV-2.4.8-android-sdk\sdk\native\jni\OpenCV.mk  

LOCAL_SRC_FILES  := OpencvJni.cpp  
LOCAL_MODULE     := opencv_jni  
include $(BUILD_SHARED_LIBRARY)  
