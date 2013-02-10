package jdrive.glib;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.api.client.auth.oauth2.Credential;

public interface SingleCredentialStore {

	boolean load(Credential credential) throws FileNotFoundException,
			IOException;

	void store(Credential credential) throws IOException;

	void clear();

}
