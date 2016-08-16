package taichu.research.network.netty4.tlvCodec.core;

import java.lang.annotation.Annotation;
import org.apache.log4j.Logger;

import taichu.research.network.netty4.tlvCodec.core.annotation.Byte;

public class EncoderHelper {

	private static Logger log = Logger.getLogger(EncoderHelper.class);

	public static int getByteNbr(Object obj, String fieldName) throws NoSuchFieldException, SecurityException {
		Annotation[] annotations;
		int ret = 0;
		try {
			annotations = obj.getClass().getField(fieldName).getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof Byte) {
					log.debug("Got Annotation:" + annotation);
					Byte byteNbr = (Byte) annotation;
					ret = byteNbr.value();
				}
			}
			return ret;
		} catch (NoSuchFieldException | SecurityException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

}
