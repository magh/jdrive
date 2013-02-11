package jdrive.ulib;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UtilTest {

	@Test
	public void testIsEmpty() {
		assertTrue(Util.isEmpty(null));
		assertTrue(Util.isEmpty(""));
		assertFalse(Util.isEmpty(" "));
	}

}
