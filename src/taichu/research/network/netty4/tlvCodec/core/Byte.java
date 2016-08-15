/**
 * 
 */
package taichu.research.network.netty4.tlvCodec.core;

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
	/**
	 * 定义字段根据协议（TLV类型）实际占用多少byte.
	 * 默认为1（可省略），-1为不定长，其他类似“@Byte(4)”
	 * @return 返回字段的byte数，供后续codec使用.
	 */
	int value() default 1; //-1为不定长值 
	
	/*
	 * Field mapping & handling rules:
	 * 
	 * (1)For number or byte(s) fields:
	 * field has 1 byte, need define as container class (int), and use writeByte()
	 * field has 2 byte, need define as container class (int), and use writeShort()
	 * field has 4 byte, need define as container class (int), and use writeInt()
	 * field has 8 byte, need define as container class (long), and use writeLong()
	 * field has 3/5/6/7/X byte, need define as container class (bytes), and use writeBytes()
	 * 
	 * (2)For String or other fields:
	 * field is a String, need define as container class (String), and use writeBytes()
	 * field is a boolean, need define as container class(boolean), and use writeByte()
	 * 
	 * 说明：
	 * 1.实际上字段（field）在网络输出的时候（writeXXX）总是体现为字节（流）的，所以字段本身定义，及容器定义最终体现为写字节；
	 * 2.字段（field）本身定义是按照协议（如SMP协议）定义，协议如果基于TLV类型，一般field都是定义为字节（byte）；
	 * 2a.field的本身定义请用Annotation（Byte）来体现，比如“@Byte(2)”,默认为1不用写，-1为不定长，供codec类使用；
	 * 2b.field的容器class是java代码中定义的实际承载field的类，因为协议定义的field（字节定义）不一定正好有对应的java类型，所以必须用容器承载
	 * 2c.field的容器class只要足够大，能容下协议地冠以的字节数，一般就行，当然也考虑到代码行文方便和实际逻辑含义，比如用boolean；
	 * 2d.field的容器class最终通过writeXXX写到网络字节流，此时只将符合定义的field中所需的byte写到网络！
	 */
	
	
	/*
	//定义数据类型的描述annotation，便于动态处理；
	DataDef DD() default DataDef.B1;
	//定义数据类型容器的描述annotation，便于动态处理；
	DataContainer DC() default DataContainer.Int;
	//定义数据类型的发送函数，只写其中低位若干字节；
	DataWriter DW() default DataWriter.writeByte;

	
	//标记改数据类型实质上是几个字节
	public static enum DataDef{B1,B2,B3,B4,B5,B6,B7,B8,B16,B32,BX} 
	//因为JAVA的数据类型无法完全满足要求，所以用接近但更大的type来包裹，但网络发送的时候只按需发送必要的有意义的低位若干字节；
	public static enum DataContainer{Byte,Int,Long,String,Boolean,Bytes}
	//依次为写入最低的1个字节，2个字节，4个字节，8个字节，更多的为字节数组
	public static enum DataWriter{writeByte,writeShort,writeInt,writeLong,writeBytes}
	
		*/
}
