package taichu.research.network.netty4.tlvCodec.codec.protocol.smp;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import taichu.research.network.netty4.tlvCodec.codec.wrapper.IMyEncoder;
import taichu.research.network.netty4.tlvCodec.codec.wrapper.Netty4EncoderWrapper;
import taichu.research.network.netty4.tlvCodec.core.EncoderHelper;
import taichu.research.network.netty4.tlvCodec.core.annotation.Byte;
import taichu.research.network.netty4.tlvCodec.core.annotation.Byte.ByteDef;
import taichu.research.network.netty4.tlvCodec.protocol.smp.Smp;
import taichu.research.network.netty4.tlvCodec.protocol.smp.Smp.TlvSection;

public class SmpEncoderOnNetty4 extends MessageToByteEncoder<Smp> {
	private static Logger log = Logger.getLogger(SmpEncoderOnNetty4.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Smp msg, ByteBuf out) throws Exception {
		outputMsg(out,msg);
	}
	
	public static void outputBytes(ByteBuf out, Field f, ByteDef byteDef,Smp smp) throws 
		IllegalArgumentException,IllegalAccessException,UnsupportedEncodingException{
		try {
			switch (byteDef) {
			case B1: out.writeByte((int)f.get(smp));break;
			case B2: out.writeShort((int)f.get(smp));break;
			case B4: out.writeInt((int)f.get(smp));break;
			case B8: out.writeLong((long)f.get(smp));break;
			
			case B3: out.writeBytes((byte[])f.get(smp));break;
			case B16: out.writeBytes((byte[])f.get(smp));break;
			case B32: out.writeBytes((byte[])f.get(smp));break;
			
			case B0: break;
			//TODO:如何处理不定长的Object，byte[]，String等，也许需要修改@Byte或增加其他@
			case BObj: out.writeBytes(f.get(smp).toString().getBytes("utf-8"));
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
		int byteDef="";
		
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
    					Byte byteDef = (Byte) annotation;
    					outputBytes(out,f,byteDef,smp);
    				}
    			}
    		} catch (SecurityException e) {
    			log.error(e.getMessage());
    			throw e;
    		}
        }   
   }  
    
    private static final Random rand = new Random();
    //private final int flags = 0xdeadbeef; //=4bytes/8个char

    private static byte[] updateTlvGetBytes(Map.Entry<String, TlvSection> entry){
    	return null;
    	//TODO
    }
    
    private static byte[] getFieldBytes(){
    	return null;
    	//TODO
    }
 
    
	public static byte[] outputMsg(ByteBuf out, Smp smp){
		
		//计算/填充HEAD字段
		smp.getSmpFlag()// is fixed field;
		smp.getSmpVer();// is fixed field;
		smp.getSMP_HEAD_LEN();// is fixed field;
		
		//用基本不重复的ms毫秒级时间做种子；再去int全集范围的随机数，碰撞概率就很小；
		//因为MSG_ID用于收发桩模块的消息核对，一般在几分钟内应该确认并使用完毕，就可以丢弃；（只要几分钟内不重复就可）
		rand.setSeed(System.currentTimeMillis());
		smp.setSMP_MSG_ID(rand.nextInt());
		
		smp.setSMP_BODY_LEN(XXX);//需要获得所有BODY字段后，最后计算；
		smp.getSmpBodyReserved();// is fixed field;
		smp.setSMP_HEAD_CHKSUM(XXX);//需要先计算所有的HEAD值；最后计算CRC
		
		//计算/填充BODY字段；
		getFieldBytes(smp.getSmpBodyFlag());//is fixed field;
		int tlvSecNbr = smp.getTlvSections().size();
		smp.setSMP_DATA_TOTAL(tlvSecNbr);
		smp.getSmpBodyReserved(); //is fixed field;

		
		//计算BODY中的TLV SECTIONS
		ByteBuffer tlvSectionsBytes = null;
		HashMap<String, TlvSection> tlvSections=smp.getTlvSections();
		Iterator<Entry<String, TlvSection>> iter = tlvSections.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, TlvSection> entry = (Map.Entry<String, TlvSection>) iter.next();
			//获得一个tlv section，更新TLV中的len设定，并返回bytes字节数组，以便拼接；
			tlvSectionsBytes.put(updateTlvGetBytes(entry));
		}
		
		
		smp.setSMP_BODY_CHKSUM(XXX);//需要获得所有BODY字段后，最后计算；
		
				
				
		
//				
//				byte[] key = msg.key().getBytes(CharsetUtil.UTF_8);
//				byte[] body = msg.body().getBytes(CharsetUtil.UTF_8);
//				// total size of the body = key size + content size + extras size //3
//				int bodySize = key.length + body.length + (msg.hasExtras() ? 8 : 0);
//
//				// write total body length ( 4 bytes - 32 bit int) //10
//				out.writeInt(bodySize);
//				// write key //14
//				out.writeBytes(key);
//				// write value //15
//				out.writeBytes(body);
	}
	
	




    
    
	//TODO:如下对2种data type的解析及异常抛出放在统一的编码encode函数中；
//	public TlvSection(String value) throws UnsupportedEncodingException {
//		this.SMP_DATA_TYPE=SmpDataTypeDef.get(DATA_TYPE_ENUM.DT_STRING);
//		this.SMP_DATA_LEN=value.length();
//		try {
//			this.SMP_DATA_VAL=value.getBytes("utf-8");
//		} catch (UnsupportedEncodingException e) {
//			log.error("Can not convert string value("+value+") to bytes and create TLV section failed!");
//			throw e;
//		}
//	}
//	
//	public TlvSection(byte[] value) {
//		this.SMP_DATA_TYPE=SmpDataTypeDef.get(DATA_TYPE_ENUM.DT_BYTES);
//		this.SMP_DATA_LEN=value.length;
//	    System.arraycopy( value, 0, this.SMP_DATA_VAL, 0, value.length);
//	}
	

}
