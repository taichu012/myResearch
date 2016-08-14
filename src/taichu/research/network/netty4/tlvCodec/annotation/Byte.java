/**
 * 
 */
package taichu.research.network.netty4.tlvCodec.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author taichu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Byte {
	
	//当仅有一个函数且名为Value时，调用可从“XXX（value=YYY）”简化为“XXX（YYY）”
	int value() default 1; //-1为不定长值 
	
	/*
	//定义数据类型的描述annotation，便于动态处理；
	DataDef DD() default DataDef.B1;
	//定义数据类型容器的描述annotation，便于动态处理；
	DataContainer DC() default DataContainer.Int;
	//定义数据类型的发送函数，只写其中低位若干字节；
	DataWriter DW() default DataWriter.writeByte;
	*/

	
	
	//标记改数据类型实质上是几个字节
	public static enum DataDef{B1,B2,B3,B4,B5,B6,B7,B8,B16,B32,BX} 
	//因为JAVA的数据类型无法完全满足要求，所以用接近但更大的type来包裹，但网络发送的时候只按需发送必要的有意义的低位若干字节；
	public static enum DataContainer{Byte,Int,Long,String,Boolean,Bytes}
	//依次为写入最低的1个字节，2个字节，4个字节，8个字节，更多的为字节数组
	public static enum DataWriter{writeByte,writeShort,writeInt,writeLong,writeBytes}
}
