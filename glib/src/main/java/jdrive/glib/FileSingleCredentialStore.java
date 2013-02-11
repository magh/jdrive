package jdrive.glib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import jdrive.ulib.Util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.gson.Gson;

public class FileSingleCredentialStore implements SingleCredentialStore {

	private final File store;

	public FileSingleCredentialStore(final String fileName) {
		store = new File(fileName);
	}

	@Override
	public boolean load(final Credential credential)
			throws FileNotFoundException, IOException {
		if (!store.exists()) {
			return false;
		}
		final Gson gson = new Gson();
		if (!store.exists()) {
			return false;
		}
		final FileReader reader = new FileReader(store);
		final CredentialData data = gson.fromJson(reader, CredentialData.class);
		return data.populate(credential);
	}

	@Override
	public void store(final Credential credential) throws IOException {
		CredentialData data = new CredentialData(credential);
		if (Util.isEmpty(data.getRefreshToken())) {
			load(credential);
			data.populate(credential);
			data = new CredentialData(credential);
		}
		final Gson gson = new Gson();
		final String json = gson.toJson(data);
		final FileOutputStream fos = new FileOutputStream(store);
		fos.write(json.getBytes());
		fos.close();
	}

	@Override
	public void clear() {
		store.delete();
	}

}
