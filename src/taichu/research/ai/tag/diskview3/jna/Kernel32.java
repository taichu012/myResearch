package taichu.research.ai.tag.diskview3.jna;

//Kernel32.java

import java.util.Arrays;
import java.util.List;

import com.sun.jna.*;
import com.sun.jna.win32.*;

public interface Kernel32 extends StdCallLibrary {
	public static class SYSTEMTIME extends Structure {
		public short wYear;
		public short wMonth;
		public short wDayOfWeek;
		public short wDay;
		public short wHour;
		public short wMinute;
		public short wSecond;
		public short wMilliseconds;

		//�������°汾JNA�Ĺ�ϵ���̳�Structure�����������溯��������fieldʵ�ʵ�������ʲô�����ڷ���ġ��ֶ��Ų���ֻ����String[]������
		//����̳еĲ���Structure�����ǡ�an existing Structure subclass������ô��Ҫ�ȡ�List fields = new ArrayList(super.getFieldOrder());������
		//Ȼ��������Լ�fields������
		
		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList(new String[] {"wYear","wMonth","wDayOfWeek","wDay","wHour","wMinute","wSecond","wMilliseconds"});
		}
	}
	
	void GetLocalTime(SYSTEMTIME result);
}

