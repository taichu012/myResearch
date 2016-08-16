package taichu.research.network.netty4.tlvCodec.codec.wrapper;

import taichu.research.network.netty4.tlvCodec.protocol.smp.Smp;

public interface IMyEncoder {
	//输入消息实例对象一个，转换为字节流；
	public static byte[] genPack(Object o) {
		return null;
	}
//	@Override
//	public byte[] genPack(Object o) {
//		if (o instanceof Smp){
//			//将协议消息smp转化为字节流输出；
//			Smp smp = (Smp)o;
//			//TODO:
//			return null;
//		} else {
//			log.warn("对象不是协议支持的消息类型！ object.class=["+o.getClass()+"].");
//		}
//		return null;
//	}
}
