package jdrive.ulib;

import org.junit.Assert;
import org.junit.Test;

public class FileMapTest {

	@Test
	public void testContains() {
		final FileMap<String> folders = new FileMap<String>();
		folders.setRoot("rootId");
		final String put1 = folders.put("dirId1", "rootId", "dirName1");
		final String put2 = folders.put("dirId2", "dirId1", "dirName2");
		Assert.assertTrue(folders.contains("rootId"));
		Assert.assertTrue(folders.contains("dirId1"));
		Assert.assertTrue(folders.contains("dirId2"));
		Assert.assertEquals("./dirName1", folders.get("dirId1"));
		Assert.assertEquals("./dirName1", put1);
		Assert.assertEquals("./dirName1/dirName2", folders.get("dirId2"));
		Assert.assertEquals("./dirName1/dirName2", put2);
	}

	@Test
	public void testMissingParent() {
		try {
			final FileMap<String> folders = new FileMap<String>();
			folders.setRoot("rootIdOk");
			folders.put("dirId1", "rootIdOk", "dirName1");
			folders.put("dirId2", "rootIdWrong", "dirName2");
			Assert.fail();
		} catch (final RuntimeException e) {
		}
	}
}
