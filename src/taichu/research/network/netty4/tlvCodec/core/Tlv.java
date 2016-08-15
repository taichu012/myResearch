package taichu.research.network.netty4.tlvCodec.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Tlv {
	
	is value();

	//定义TLV的三个内容
	public static enum is {Type, Len, Val};
	
}
