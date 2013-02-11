package jdrive.glib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.gson.Gson;

public class FileCredentialStore implements CredentialStore {

	private final File store;

	private final Map<String, CredentialData> storeMap;

	@SuppressWarnings("unchecked")
	public FileCredentialStore(final String fileName)
			throws FileNotFoundException, IOException {
		store = new File(fileName);
		if (store.exists()) {
			final FileReader reader = new FileReader(store);
			final Gson gson = new Gson();
			storeMap = gson.fromJson(reader, Map.class);
		} else {
			storeMap = new HashMap<String, CredentialData>();
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
