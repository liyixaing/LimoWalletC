#include <jni.h>
#include <string>

#ifndef LEMONWALLETC_STRCONV_H
#define LEMONWALLETC_STRCONV_H

using namespace std;

jstring CStr2Jstring(JNIEnv* env, const char* pat);

const char* Jstring2CStr(JNIEnv* env, jstring jstr);

jbyteArray CBytes2JByteArray(JNIEnv* env, const char* bytes, size_t size);

#endif //LEMONWALLETC_STRCONV_H
