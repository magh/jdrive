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

	@Test
	public void testValueOfBoolean() {
		assertFalse(Util.valueOf(null));
		assertFalse(Util.valueOf(Boolean.valueOf(false)));
		assertTrue(Util.valueOf(Boolean.valueOf(true)));
	}

}
