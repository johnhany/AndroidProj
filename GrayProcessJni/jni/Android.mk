LOCAL_PATH := $(call my-dir)  
include $(CLEAR_VARS)  
include D:\Digital-Working\Android\OpenCV-2.4.8-android-sdk\sdk\native\jni\OpenCV.mk  

LOCAL_SRC_FILES  := ImageProc.cpp  
LOCAL_MODULE     := image_proc  
include $(BUILD_SHARED_LIBRARY)  
