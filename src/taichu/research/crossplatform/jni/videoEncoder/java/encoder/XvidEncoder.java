package taichu.research.crossplatform.jni.videoEncoder.java.encoder;


public class XvidEncoder {

	private int hInstance = 0;

	private int width;
	private int height;

	static {
		System.loadLibrary("xvidencoder");
	}

	public XvidEncoder(int width, int height, int colorspace, int framerate) {
		this.hInstance = _XvidEncoder(width, height, colorspace, framerate);
		this.width = width;
		this.height = height;
	}

	@Override
	public void finalize() {
		if (hInstance != 0) {
			_destoryXvidEncoder(hInstance);
		}
		hInstance = 0;
	}

	public void init(int userAssembler) {
		if (hInstance != 0) {
			_init(hInstance, userAssembler);
		}
	}

	public void stop() {
		if (hInstance != 0) {
			_stop(hInstance);
		}
	}

	public int encode(byte[] source, byte[] target, int framenum) {
		int size = 0;
		if(hInstance != 0){
			size = _encode(hInstance, source, target, 0, 0, 0, 0, framenum);
		}
		return size;
	}

	public void removeDivxp(byte[] buffer) {
		_removedivxp(hInstance, buffer, buffer.length);
	}

	private native int _XvidEncoder(int width, int height, int colorspace, int framerate);

	private native void _destoryXvidEncoder(int hInstance);

	private native int _init(int hInstance, int useAssembler);

	private native int _stop(int hInstance);

	private native int _encode(int hInstance, byte[] source, byte[] target, int key, int statsType, int statsQuant,
			int statsLength, int framenum);

	private native void _removedivxp(int hInstance, byte[] buffer, int size);

}
