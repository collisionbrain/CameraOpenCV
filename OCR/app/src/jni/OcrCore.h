#include <jni.h>

#ifndef _Included_libre_com_ocrsmart_core_OcrCore
#define _Included_libre_com_ocrsmart_core_OcrCore
#endif

#ifdef __cplusplus
extern "C"{
#endif

JNIEXPORT void JNICALL Java_libre_com_ocrsmart_core_OcrCore_getCardArea
        (JNIEnv *, jclass, jlong, jlong );

JNIEXPORT void JNICALL Java_libre_com_ocrsmart_core_OcrCore_getTextArea
        (JNIEnv *, jclass, jlong , jlong);

JNIEXPORT void JNICALL Java_libre_com_ocrsmart_core_OcrCore_getLetters
        (JNIEnv *, jclass, jlong , jlong);

        
#ifdef __cplusplus
}
#endif