#include "encoder/XvidEncoder.h"
#include "com_zxelec_encoder_XvidEncoder.h"

/*
 * Class:     com_zxelec_encoder_XvidEncoder
 * Method:    _XvidEncoder
 * Signature: (IIII)I
 */
JNIEXPORT jint JNICALL Java_com_zxelec_encoder_XvidEncoder__1XvidEncoder(
		JNIEnv *env, jobject object, jint width, jint height, jint colorspace,
		jint framerate) {
	CEncoderXvid *xvidEncoder = NULL;
	xvidEncoder = new CEncoderXvid(width, height, colorspace, framerate);
	return (jint) xvidEncoder;
}

/*
 * Class:     com_zxelec_encoder_XvidEncoder
 * Method:    _destoryXvidEncoder
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_zxelec_encoder_XvidEncoder__1destoryXvidEncoder
(JNIEnv *env, jobject object, jint hInstance) {
	CEncoderXvid *xvidEncoder = (CEncoderXvid *)hInstance;
	delete xvidEncoder;
}

/*
 * Class:     com_zxelec_encoder_XvidEncoder
 * Method:    _init
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_zxelec_encoder_XvidEncoder__1init(JNIEnv *env,
		jobject object, jint hInstance, jint useAssembler) {
	CEncoderXvid *xvidEncoder = (CEncoderXvid *) hInstance;
	return (jint) xvidEncoder->enc_init(useAssembler);
}

/*
 * Class:     com_zxelec_encoder_XvidEncoder
 * Method:    _stop
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_zxelec_encoder_XvidEncoder__1stop(JNIEnv *env,
		jobject object, jint hInstance) {
	CEncoderXvid *xvidEncoder = (CEncoderXvid *) hInstance;
	return (jint) xvidEncoder->enc_stop();
}

/*
 * Class:     com_zxelec_encoder_XvidEncoder
 * Method:    _encode
 * Signature: (I[B[BIIIII)I
 */
JNIEXPORT jint JNICALL Java_com_zxelec_encoder_XvidEncoder__1encode(JNIEnv *env,
		jobject object, jint hInstance, jbyteArray source, jbyteArray target,
		jint key, jint statsType, jint statsQuant, jint statsLength,
		jint framenum) {
	CEncoderXvid *xvidEncoder = (CEncoderXvid *) hInstance;
	jbyte *_source = env->GetByteArrayElements(source, JNI_FALSE);
	jbyte *_target = env->GetByteArrayElements(target, JNI_FALSE);

	if (xvidEncoder->xvidparam.ARG_SINGLE && (!xvidEncoder->xvidparam.bitrate) && (!xvidEncoder->xvidparam.ARG_CQ)){
		xvidEncoder->xvidparam.ARG_CQ = DEFAULT_QUANT;
	}
	if (inputnum >= (unsigned int)xvidEncoder->xvidparam.ARG_MAXFRAMENR-1 && xvidEncoder->xvidparam.ARG_MAXBFRAMES){
		statsType = XVID_TYPE_PVOP;
	}
	else{
		statsType = XVID_TYPE_AUTO;
	}

	jint length = xvidEncoder->enc_main((unsigned char *) _source,
			(unsigned char *) _target, &key, &statsType, &statsQuant,
			&statsLength, framenum);
	env->ReleaseByteArrayElements(source, _source, JNI_COMMIT);
	env->ReleaseByteArrayElements(target, _target, JNI_COMMIT);
	return (jint) length;
}

/*
 * Class:     com_zxelec_encoder_XvidEncoder
 * Method:    _removedivxp
 * Signature: (I[BI)V
 */
JNIEXPORT void JNICALL Java_com_zxelec_encoder_XvidEncoder__1removedivxp
(JNIEnv *env, jobject object, jint hInstance, jbyteArray buffer, jint size) {
	CEncoderXvid *xvidEncoder = (CEncoderXvid *)hInstance;
	jbyte *_buffer = env->GetByteArrayElements(buffer, JNI_FALSE);
	xvidEncoder->removedivxp((char *)_buffer, size);
	env->ReleaseByteArrayElements(buffer, _buffer, JNI_COMMIT);
}
