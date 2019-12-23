//
// Created by Administrator on 2019/8/28.
//

#include "strconv.h"
#include <android/log.h>

jstring CStr2Jstring(JNIEnv *env, const char *pat) {
    //定义java String类 strClass
    jclass strClass = (env)->FindClass("java/lang/String");

    //获取java String类方法String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");

    //建立byte数组
    jbyteArray bytes = (env)->NewByteArray((jsize) strlen(pat));

    //将char* 转换为byte数组
    (env)->SetByteArrayRegion(bytes, 0, (jsize) strlen(pat), (jbyte *) pat);

    //设置String, 保存语言类型,用于byte数组转换至String时的参数
    jstring encoding = (env)->NewStringUTF("UTF-8");

    return (jstring) (env)->NewObject(strClass, ctorID, bytes, encoding);
}

const char *Jstring2CStr(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("GB2312");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(size_t(alen + 1)); //new char[alen+1];
        memcpy(rtn, ba, size_t(alen));
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

jbyteArray CBytes2JByteArray(JNIEnv *env, const char *bytes, size_t size) {

    jbyteArray j_bs_arr = env->NewByteArray(size);

    env->SetByteArrayRegion(j_bs_arr, 0, size, (jbyte *) bytes);

    return j_bs_arr;
}