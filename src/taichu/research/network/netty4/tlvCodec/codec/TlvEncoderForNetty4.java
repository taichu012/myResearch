package taichu.research.network.netty4.tlvCodec.codec;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import taichu.research.network.netty4.tlvCodec.core.Byte;
import taichu.research.network.netty4.tlvCodec.core.TlvEncoder;
import taichu.research.network.netty4.tlvCodec.protocol.Smp;

public class TlvEncoderForNetty4 extends TlvEncoder{
	private static Logger log = Logger.getLogger(TlvEncoderForNetty4.class);
	
	public static void outputBytes(ByteBuf out, Field f, int bytesOfField,Smp smp) throws 
		IllegalArgumentException,IllegalAccessException,UnsupportedEncodingException{
		try {
			switch (bytesOfField) {
			case 1: out.writeByte((int)f.get(smp));break;
			case 2: out.writeShort((int)f.get(smp));break;
			case 4: out.writeInt((int)f.get(smp));break;
			case 8: out.writeLong((long)f.get(smp));break;
			
			case 0: break;
			//TODO:如何处理不定长的Object，byte[]，String等，也许需要修改@Byte或增加其他@
			case -1: out.writeBytes(f.get(smp).toString().getBytes("utf-8"));
			default: out.writeShort((int)f.get(smp));
			
			
			};
		
		}catch (IllegalArgumentException | IllegalAccessException | UnsupportedEncodingException e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
    public static void abc(Smp smp,ByteBuf out) throws Exception{  
    	//用反射遍历smp中所有字段，并根据base class的公共方法获得字段对应的字节数，然后匹配专门的函数输出为网络字节流；
		Annotation[] annotations;
		int bytesOfField=-1;
		
        Class cls = smp.getClass();  
        Field[] fields = cls.getDeclaredFields();  
        
        //遍历smp的字段
        for(int i=0; i<fields.length; i++){  
            Field f = fields[i];  
            f.setAccessible(true);  
            
            try {
    			annotations = f.getAnnotations();
    			for (Annotation annotation : annotations) {
    				//检查Byte的annotation是否存在；
    				if (annotation instanceof Byte) {
    					log.debug("Got Annotation:" + annotation);
    					Byte byteNbr = (Byte) annotation;
    					bytesOfField= byteNbr.value();
    					outputBytes(out,f,bytesOfField,smp);
    				}
    			}
    		} catch (SecurityException e) {
    			log.error(e.getMessage());
    			throw e;
    		}
        }   
   }  
}
