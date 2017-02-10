LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
OPENCV_INSTALL_MODULES:=on
include C:/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk
LOCAL_MODULE     := ocr_core
LOCAL_SRC_FILES  := OcrCore.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl
include $(BUILD_SHARED_LIBRARY)
