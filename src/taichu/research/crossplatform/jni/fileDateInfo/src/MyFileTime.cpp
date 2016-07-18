#include <windows.h>   
#include "MyFileTime.h"   
  
JNIEXPORT jstring JNICALL Java_cn_imu_jni_MyFileTime_getFileCreationTime(JNIEnv *env, jobject cls, jstring FileName)   
{   
    HANDLE hFile;   
    FILETIME creationTime;   
    FILETIME lastAccessTime;   
    FILETIME lastWriteTime;   
    FILETIME creationLocalTime;   
    SYSTEMTIME creationSystemTime;   
    jstring result;   
    char fileTimeString[30];   
       
    hFile = CreateFile((char *)env->GetStringUTFChars(FileName, 0), GENERIC_READ, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL, OPEN_EXISTING, 0, NULL);   
    if(hFile == INVALID_HANDLE_VALUE) return env->NewStringUTF("");   
    if(GetFileTime(hFile, &creationTime, &lastAccessTime, &lastWriteTime))   
    {   
        if(FileTimeToLocalFileTime(&creationTime, &creationLocalTime))   
        {   
            if(FileTimeToSystemTime(&creationLocalTime, &creationSystemTime))   
            {   
                sprintf(fileTimeString,    
                    "%04d-%02d-%02d %02d:%02d:%02d\0",    
                        creationSystemTime.wYear,    
                        creationSystemTime.wMonth,    
                        creationSystemTime.wDay,    
                        creationSystemTime.wHour,    
                        creationSystemTime.wMinute,    
                        creationSystemTime.wSecond);   
                result = env->NewStringUTF(fileTimeString);   
            }   
            else  
                result = env->NewStringUTF("");   
        }   
        else  
            result = env->NewStringUTF("");   
    }   
    else  
        result = env->NewStringUTF("");   
    CloseHandle(hFile);   
    return result;   
}  
