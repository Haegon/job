LOCAL_PATH          := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE        := quiz
LOCAL_SRC_FILES     := NativeString.cpp
LOCAL_LDLIBS        := -llog
include $(BUILD_SHARED_LIBRARY)