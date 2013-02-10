package jdrive.glib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

public class FileSingleCredentialStoreTest {

	private final static String CLIENT_ID = "clientId";
	private final static String CLIENT_SECRET = "clientSecret";
	private final static String STORE = "target/teststore";

	@Test
	public void testLoadStore() throws IOException {
		final FileSingleCredentialStore store = new FileSingleCredentialStore(
				STORE);
		store.clear();
		final GoogleCredential in = GoogleUtil.newGoogleCredential(CLIENT_ID,
				CLIENT_SECRET);
		in.setAccessToken("accessToken");
		in.setRefreshToken("refreshToken");
		in.setExpirationTimeMilliseconds(3600l);
		store.store(in);
		final GoogleCredential out = GoogleUtil.newGoogleCredential(CLIENT_ID,
				CLIENT_SECRET);
		store.load(out);
		assertEquals("accessToken", out.getAccessToken());
		assertEquals("refreshToken", out.getRefreshToken());
		assertEquals(Long.valueOf(3600l), out.getExpirationTimeMilliseconds());
	}

	@Test
	public void testLoadStore_oldRefreshToken() throws IOException {
		final FileSingleCredentialStore store = new FileSingleCredentialStore(
				STORE);
		store.clear();
		final GoogleCredential in = GoogleUtil.newGoogleCredential(CLIENT_ID,
				CLIENT_SECRET);
		in.setAccessToken("accessToken");
		in.setRefreshToken("refreshToken");
		in.setExpirationTimeMilliseconds(3600l);
		store.store(in);
		final GoogleCredential in2 = GoogleUtil.newGoogleCredential(CLIENT_ID,
				CLIENT_SECRET);
		in2.setAccessToken("accessToken2");
		in2.setExpirationTimeMilliseconds(3602l);
		store.store(in2);
		final GoogleCredential out = GoogleUtil.newGoogleCredential(CLIENT_ID,
				CLIENT_SECRET);
		store.load(out);
		assertEquals("accessToken2", out.getAccessToken());
		assertEquals("refreshToken", out.getRefreshToken());
		assertEquals(Long.valueOf(3602l), out.getExpirationTimeMilliseconds());
	}

	@Test
	public void testClear() throws IOException {
		final FileSingleCredentialStore store = new FileSingleCredentialStore(
				STORE);
		final GoogleCredential in = GoogleUtil.newGoogleCredential(CLIENT_ID,
				CLIENT_SECRET);
		in.setAccessToken("accessToken");
		in.setRefreshToken("refreshToken");
		in.setExpirationTimeMilliseconds(3600l);
		store.store(in);
		store.clear();
		final GoogleCredential out = GoogleUtil.newGoogleCredential(CLIENT_ID,
				CLIENT_SECRET);
		store.load(out);
		assertNull(out.getAccessToken());
		assertNull(out.getRefreshToken());
		assertNull(out.getExpirationTimeMilliseconds());
	}

}
