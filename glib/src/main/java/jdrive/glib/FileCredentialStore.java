package jdrive.glib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jdrive.ulib.Util;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.gson.Gson;

public class FileCredentialStore implements CredentialStore {

	private final File store;

	private final Map<String, CredentialData> storeMap;

	public FileCredentialStore(final String fileName)
			throws FileNotFoundException, IOException {
		store = new File(fileName);
		final Gson gson = new Gson();
		final String json = Util.readStream(new FileInputStream(store));
		if (Util.isEmpty(json)) {
			storeMap = new HashMap<String, CredentialData>();
		} else {
			storeMap = gson.fromJson(json, Map.class);
		}
	}

	@Override
	public boolean load(final String userId, final Credential credential)
			throws IOException {
		return storeMap.get(userId).populate(credential);
	}

	@Override
	public void store(final String userId, final Credential credential)
			throws IOException {
		storeMap.put(userId, new CredentialData(credential));
		persistMap();
	}

	@Override
	public void delete(final String userId, final Credential credential)
			throws IOException {
		final CredentialData data = storeMap.remove(userId);
		data.populate(credential);
		persistMap();
	}

	private void persistMap() throws IOException {
		final Gson gson = new Gson();
		final String json = gson.toJson(storeMap);
		final FileOutputStream fos = new FileOutputStream(store);
		fos.write(json.getBytes());
		fos.close();
	}

}
