package jdrive.ulib;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Util {

	public static boolean isEmpty(final String json) {
		return (json == null) || (json.length() == 0);
	}

	public static String readStream(final InputStream inp) throws IOException {
		final StringBuffer sb = new StringBuffer();
		final BufferedInputStream bis = new BufferedInputStream(inp);
		int ch = -1;
		while ((ch = bis.read()) != -1) {
			sb.append((char) ch);
		}
		bis.close();
		return sb.toString();
	}

	public static void log(final String msg) {
		System.out.println(msg);
	}

}
