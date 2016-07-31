package taichu.research.ai.tag.diskview3.jna;

//Kernel32.java

import java.util.Arrays;
import java.util.List;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.*;

public interface Kernel32 extends StdCallLibrary {

	//MSDN:http://msdn.microsoft.com/zh-cn/library/ms724950(v=vs.85).aspx
	public static class SYSTEMTIME extends Structure {
		public short wYear;
		public short wMonth;
		public short wDayOfWeek;
		public short wDay;
		public short wHour;
		public short wMinute;
		public short wSecond;
		public short wMilliseconds;

		//可能是新版本JNA的关系，继承Structure必须重载下面函数，不管field实际的类型是什么，用于反射的“字段排布”只能用String[]！！！
		//如果继承的不是Structure，而是“an existing Structure subclass”，那么需要先“List fields = new ArrayList(super.getFieldOrder());”！！
		//然后再添加自己fields！！！
		
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] {"wYear","wMonth","wDayOfWeek","wDay","wHour","wMinute","wSecond","wMilliseconds"});
		}
	}
	
	//MSDN:http://msdn.microsoft.com/zh-cn/library/ms724284(v=vs.85).aspx
	public static class FILETIME extends Structure {
		public int dwLowDateTime;
		public int dwHighDateTime;
		
		public static class ByReference extends FILETIME implements Structure.ByReference { }
		public static class ByValue extends FILETIME implements Structure.ByValue{}
		
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] {"dwLowDateTime","dwHighDateTime"});
		}
	}
	
	//原型：http://msdn.microsoft.com/zh-cn/library/ms724338(v=VS.85).aspx
	void GetLocalTime(SYSTEMTIME result);
	
	//原型：http://msdn.microsoft.com/zh-cn/library/ms724320(v=VS.85).aspx 
	boolean GetFileTime(HANDLE handleFile, 
			FILETIME lpfileCreateTime,
			FILETIME lpfileAccessTime,
			FILETIME lpfileWriteTime);
	
	
	//MSDN:http://msdn.microsoft.com/zh-cn/library/windows/desktop/aa365282(v=vs.85).aspx
	/*
	 * typedef struct _OFSTRUCT {
		  BYTE cBytes;
		  BYTE fFixedDisk;
		  WORD nErrCode;
		  WORD Reserved1;
		  WORD Reserved2;
		  CHAR szPathName[OFS_MAXPATHNAME];
} OFSTRUCT, *POFSTRUCT;
	 */
		public static class OFSTRUCT extends Structure {
			public byte cBytes;
			public byte cFixedDisk;
			public short nErrCode;
			public short Reserved1;
			public short Reserved2;
			public byte szPathName;
			
			public static class ByReference extends OFSTRUCT implements Structure.ByReference { }
			public static class ByValue extends OFSTRUCT implements Structure.ByValue{}
			
			@Override
			protected List<String> getFieldOrder() {
				return Arrays.asList(new String[] {"cBytes","cFixedDisk","nErrCode","Reserved1","Reserved2","szPathName"});
			}
		}
	
			
	/*
	 * HFILE WINAPI OpenFile(
  		_In_   LPCSTR lpFileName,
  		_Out_  LPOFSTRUCT lpReOpenBuff,
  		_In_   UINT uStyle //INFO: http://baike.baidu.com/view/1979721.htm (win32系統的32位無符號整形肯定是正的，只要用java的long大於它就可以了！）
		);

	 */
	HANDLE OpenFile(String filename, OFSTRUCT lpReOpenBuff, long uStyle );
	
}

/*

//文件操作 
Function long FindFirstFileA (ref string filename, ref os_finddata findfiledata) library "kernel32.dll" 
Function boolean FindNextFileA (long handle, ref os_finddata findfiledata) library "kernel32.dll" 
Function boolean FindClose (long handle) library "kernel32.dll" 
Function long    OpenFile (ref string filename, ref os_fileopeninfo of_struct, ulong action) LIBRARY "kernel32.dll" 
Function boolean CloseHandle (long file_hand) LIBRARY "kernel32.dll" 
Function boolean GetFileTime(long hFile, ref os_filedatetime  lpCreationTime, ref os_filedatetime  lpLastAccessTime, ref os_filedatetime  lpLastWriteTime  )  library "kernel32.dll" 
Function boolean FileTimeToSystemTime(ref os_filedatetime lpFileTime, ref os_systemtime lpSystemTime) library "kernel32.dll" 
Function boolean FileTimeToLocalFileTime(ref os_filedatetime lpFileTime, ref os_filedatetime lpLocalFileTime) library "kernel32.dll" 
Function boolean SetFileTime(long hFile, os_filedatetime  lpCreationTime, os_filedatetime  lpLastAccessTime, os_filedatetime  lpLastWriteTime  )  library "kernel32.dll" 
Function boolean SystemTimeToFileTime(os_systemtime lpSystemTime, ref os_filedatetime lpFileTime) library "kernel32.dll" 
Function boolean LocalFileTimeToFileTime(ref os_filedatetime lpLocalFileTime, ref os_filedatetime lpFileTime) library "kernel32.dll"


*/
/*
Java Type C Type Native Representation

boolean
int
32-bit integer (customizable)

byte
char
8-bit integer

char
wchar_t
platform-dependent

short
short
16-bit integer

int
int
32-bit integer

long
long long, __int64
64-bit integer

float
float
32-bit floating point

double
double
64-bit floating point

Buffer
Pointer 
pointer
platform-dependent (32- or 64-bit pointer to memory)

<T>[] (array of primitive type)
pointer
array
32- or 64-bit pointer to memory (argument/return)
contiguous memory (struct member)

除了上面的类型，JNA还支持常见的数据类型的映射。

String
char*
NUL-terminated array (native encoding or jna.encoding)

WString
wchar_t*
NUL-terminated array (unicode)

String[]
char**
NULL-terminated array of C strings

WString[]
wchar_t**
NULL-terminated array of wide C strings

Structure
struct*
struct
pointer to struct (argument or return) (or explicitly)
struct by value (member of struct) (or explicitly)

Union
union
same as Structure

Structure[]
struct[]
array of structs, contiguous in memory

Callback
<T> (*fp)()
function pointer (Java or native)

NativeMapped
varies
depends on definition

NativeLong
long
platform-dependent (32- or 64-bit integer)

PointerType

======================================================

Native Type Size Java Type Common Windows Types 
char 8-bit integer byte BYTE, TCHAR 
short 16-bit integer short WORD 
wchar_t 16/32-bit character char TCHAR 
int 32-bit integer int DWORD 
int boolean value boolean BOOL 
long 32/64-bit integer NativeLong LONG 
long long 64-bit integer long __int64 
float 32-bit FP float   
double 64-bit FP double   
char* C string String LPTCSTR 
void* pointer Pointer LPVOID, HANDLE, LPXXX 


*/

