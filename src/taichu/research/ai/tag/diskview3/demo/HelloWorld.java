package taichu.research.ai.tag.diskview3.demo;


import taichu.research.ai.tag.diskview3.jna.Kernel32;

import com.sun.jna.*;
import com.sun.jna.win32.*;

/** Simple example of JNA interface mapping and usage. */
public class HelloWorld {

    // This is the standard, stable way of mapping, which supports extensive
    // customization and mapping of Java to native types.

    public interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary)
            Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"),
                               CLibrary.class);
        void printf(String format, Object... args);
    }
    
  //Kernel32 接口（The Kernel32 interface）

    public static void main(String[] args) {
        CLibrary.INSTANCE.printf("Hello, World\n");
        for (int i=0;i < args.length;i++) {
            CLibrary.INSTANCE.printf("Argument %d: %s\n", i, args[i]);
        }
        
//        this.PrintFileDate(pathFilename);
         
        Kernel32 lib = (Kernel32)Native.loadLibrary ("kernel32",Kernel32.class);
        Kernel32.SYSTEMTIME time = new Kernel32.SYSTEMTIME();
        lib.GetLocalTime(time);
        System.out.println ("Year is "+time.wYear);
        System.out.println ("Month is "+time.wMonth);
        System.out.println ("Day of Week is "+time.wDayOfWeek);
        System.out.println ("Day is "+time.wDay);
        System.out.println ("Hour is "+time.wHour);
        System.out.println ("Minute is "+time.wMinute);
        System.out.println ("Second is "+time.wSecond);
        System.out.println ("Milliseconds are "+time.wMilliseconds);
}
    
    
//    private void PrintFileDate(String pathFilename)   
//    {   
//    	//打开文件，并获取创建文件的时间
//        HANDLE hFile;   
//        FILETIME creationTime;   
//        FILETIME lastAccessTime;   
//        FILETIME lastWriteTime;   
//        FILETIME creationLocalTime;   
//        SYSTEMTIME creationSystemTime;   
//        jstring result;   
//        char fileTimeString[30];   
//           
//        hFile = CreateFile((char *)env->GetStringUTFChars(FileName, 0), GENERIC_READ, FILE_SHARE_READ | FILE_SHARE_WRITE, NULL, OPEN_EXISTING, 0, NULL);   
//        if(hFile == INVALID_HANDLE_VALUE) return env->NewStringUTF("");   
//        if(GetFileTime(hFile, &creationTime, &lastAccessTime, &lastWriteTime))   
//        {   
//            if(FileTimeToLocalFileTime(&creationTime, &creationLocalTime))   
//            {   
//                if(FileTimeToSystemTime(&creationLocalTime, &creationSystemTime))   
//                {   
//                	CLibrary.INSTANCE.printf(fileTimeString,    
//                        "%04d-%02d-%02d %02d:%02d:%02d\0",    
//                            creationSystemTime.wYear,    
//                            creationSystemTime.wMonth,    
//                            creationSystemTime.wDay,    
//                            creationSystemTime.wHour,    
//                            creationSystemTime.wMinute,    
//                            creationSystemTime.wSecond);   
//                }   
//            }   
//        }   
        

}