package taichu.research.network.netty4.tlvCodec.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface TLV {
	
	IS value();

	//定义TLV的三个内容
	public static enum IS {Type, Length, Value};
	
}
