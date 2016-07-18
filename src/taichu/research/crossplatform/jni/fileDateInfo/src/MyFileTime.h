/*
 * MyFileTime.h
 *
 *  Created on: 2013-3-1
 *      Author: chen.chao
 */

#ifndef MYFILETIME_H_
#define MYFILETIME_H_

#include <jni.h>
/* Header for class MyFileTime */

#ifndef _Included_MyFileTime
#define _Included_MyFileTime
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     MyFileTime
 * Method:    getFileCreationTime
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_cn_imu_jni_MyFileTimegetFileCreationTime
  (JNIEnv *, jobject, jstring);

#ifdef __cplusplus
}
#endif
#endif




#endif /* MYFILETIME_H_ */
