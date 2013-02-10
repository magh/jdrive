package jdrive.ulib;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import jdrive.ulib.Util;

import junit.framework.Assert;

import org.junit.Test;

public class UtilTest {

	@Test
	public void testIsEmpty() {
		assertTrue(Util.isEmpty(null));
		assertTrue(Util.isEmpty(""));
		assertFalse(Util.isEmpty(" "));
	}

	@Test
	public void testReadStream() throws FileNotFoundException, IOException {
		final String data = Util.readStream(new FileInputStream(
				"target/test-classes/test"));
		Assert.assertEquals("test", data);
	}

}
