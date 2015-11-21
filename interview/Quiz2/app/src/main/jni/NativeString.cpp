#include "com_toast_android_NativeString.h"
#include <stdio.h>
#include <android/log.h>

JNIEXPORT jint JNICALL Java_com_toast_android_NativeString_find
        (JNIEnv *env, jobject obj, jstring arg1, jstring arg2) {

    // 두개의 문자를 char pointer로 바꾼다
    const char *s1 = env->GetStringUTFChars(arg1, JNI_FALSE);
    const char *s2 = env->GetStringUTFChars(arg2, JNI_FALSE);

    int i=0;
    int j=0;

    // while문을 돌면서 두번째 문자열의 시작과 같은 문자를 첫번째 문자열에서 찾고
    // 찾으면 다음 문자가 같은지 비교하여 전체 문자열이 포함되어있으면 시작 인덱스를 리턴한다
    while(s1[i]!='\0'){
        if(s1[i] == s2[j])
        {
            int init = i;
            while (s1[i] == s2[j] && s2[j]!='\0')
            {
                j++;
                i++;
            }
            if(s2[j]=='\0'){
                return init;
            }
            j=0;
        }
        i++;
    }

    // 못찾으면 -1 리턴
   return -1;
}