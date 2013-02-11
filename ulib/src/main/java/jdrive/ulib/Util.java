package jdrive.ulib;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

	public static boolean isEmpty(final String json) {
		return (json == null) || (json.length() == 0);
	}

	public static void log(final String msg) {
		System.out.println(msg);
	}

	public static void download(final String url, final String output)
			throws IOException {
		download(new URL(url).openStream(), output);
	}

	public static void download(final InputStream is, final String output)
			throws IOException {
		FileOutputStream fos = null;
		try {
			final ReadableByteChannel rbc = Channels.newChannel(is);
			fos = new FileOutputStream(output);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		} finally {
			close(fos);
		}
	}

	/**
	 * @param data
	 * @return checksum of data in hex (length: ?)
	 */
	public static String md5(final InputStream input) {
		return toHex(alg("MD5", input));
	}

	/**
	 * @param data
	 * @return checksum of data in hex (length: ?)
	 */
	public static byte[] alg(final String alg, final InputStream input) {
		if (isEmpty(alg) || (input == null)) {
			return null;
		}
		try {
			final MessageDigest algorithm = MessageDigest.getInstance(alg);
			algorithm.reset();
			int c;
			final byte[] buffer = new byte[8192];
			while ((c = input.read(buffer)) != -1) {
				algorithm.update(buffer, 0, c);
			}
			return algorithm.digest();
		} catch (final NoSuchAlgorithmException nsae) {
			throw new RuntimeException("NoSuchAlgorithmException: " + alg);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Converts a number of bytes into their hexadecimal representation.
	 *
	 * @param b
	 *            an array of bytes.
	 * @return a string containing the hexadecimal representation of the
	 *         provided bytes.
	 */
	public static String toHex(final byte[] b) {
		if (b != null) {
			return toHex(b, 0, b.length);
		}
		return null;
	}

	/**
	 * Converts a number of bytes into their hexadecimal representation.
	 *
	 * @param b
	 *            the source array.
	 * @param offset
	 *            starting position in the source array.
	 * @param length
	 *            the number bytes to convert.
	 * @return a string containing the hexadecimal representation of the
	 *         provided bytes.
	 * @exception ArrayIndexOutOfBoundsException
	 *                if conversion would cause access of data outside array
	 *                bounds.
	 * @exception IllegalArgumentException
	 *                if <code>b</code> is <code>null</code> or
	 *                <code>length</code> is less than zero.
	 */
	public static String toHex(final byte[] b, final int offset,
			final int length) {
		if (b == null) {
			throw new IllegalArgumentException("source array is null");
		}
		if (length < 0) {
			throw new IllegalArgumentException("length is < 0");
		}
		if (offset < 0) {
			throw new ArrayIndexOutOfBoundsException(offset);
		}

		final int end = offset + length;

		if (end > b.length) {
			throw new ArrayIndexOutOfBoundsException(end);
		}

		final StringBuilder sb = new StringBuilder(length * 2);

		for (int i = offset; i < end; i++) {
			sb.append(convertDigit(((b[i] >> 4) & 0x0f)));
			sb.append(convertDigit((b[i] & 0x0f)));
		}
		return (sb.toString());
	}

	/**
	 * Convert the specified value (0 .. 15) to the corresponding hexadecimal
	 * digit.
	 *
	 * @param value
	 *            Value to be converted.
	 * @return the hexadecimal representation of the value.
	 */
	private static char convertDigit(final int value) {
		if (value >= 10) {
			return ((char) ((value - 10) + 'A'));
		} else {
			return ((char) (value + '0'));
		}
	}

	public static void close(final Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

}
