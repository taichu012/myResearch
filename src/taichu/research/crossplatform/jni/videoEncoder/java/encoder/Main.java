package taichu.research.crossplatform.jni.videoEncoder.java.encoder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		int width = 352;
	    int height = 288;
	    int framerate = 25;
	    int colorspace = 2; // XVID_CSP_I420;
	    int use_assemble = 1;
		InputStream in = new FileInputStream("test.yuv");
		OutputStream out = new FileOutputStream("test.m4v");
		XvidEncoder encoder = new XvidEncoder(width, height, colorspace, framerate);
		encoder.init(use_assemble);
		byte[] source = new byte[480 * 320 * 3/2];
		byte[] target = new byte[480 * 320 * 3/2];
		int size = -1;
		int framenum = 0;
		while((size = in.read(source)) > 0){
			int len = encoder.encode(source, target,framenum);
			out.write(target, 0, len);
			framenum++;
		}
		
		out.close();
		in.close();
		System.out.println(System.currentTimeMillis() - start);
	}

}
